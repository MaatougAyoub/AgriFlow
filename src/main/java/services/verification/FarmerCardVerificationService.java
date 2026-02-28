package services.verification;

import entities.VerificationStatus;
import services.ocr.OcrResult;
import services.ocr.OcrService;

import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FarmerCardVerificationService {

    private static final long OCR_TIMEOUT_MS = 25_000;

    private static final Pattern CIN_PATTERN = Pattern.compile("\\b\\d{8}\\b");

    private final OcrService ocr;

    public FarmerCardVerificationService(OcrService ocr) {
        this.ocr = ocr;
    }

    public VerificationResult verify(Path cardImage, String expectedCin, String expectedNomAr, String expectedPrenomAr) throws Exception {
        String expectedCinDigits = digitsOnly(expectedCin);

        // 1) Fast pass to catch CIN mismatch early (digits are usually recognized better with eng)
        String cinFoundFast = null;
        List<String> cinCandidatesFast = List.of();
        try {
            OcrResult fast = extractWithTimeout(cardImage, "ara+eng", 8_000);
            String fastText = normalizeForSearch(fast.fullText());
            cinCandidatesFast = extractAllCins(fastText);
            cinFoundFast = extractCinBestCandidate(fastText, cinCandidatesFast);
        } catch (TimeoutException ignored) {
            // ignore and continue with full OCR
        } catch (Exception ignored) {
            // ignore and continue with full OCR
        }

        logCinDebug("FAST", expectedCinDigits, cinFoundFast, cinCandidatesFast);

        if (expectedCinDigits != null && expectedCinDigits.matches("\\d{8}")
                && cinFoundFast != null && !cinFoundFast.equals(expectedCinDigits)) {
            return new VerificationResult(
                    VerificationStatus.REJECTED,
                    0.0,
                    cinFoundFast,
                    false,
                    "CIN différent: saisi=" + expectedCinDigits + ", détecté=" + cinFoundFast
            );
        }

        // 2) Full OCR pass
        OcrResult ocrResult;
        try {
            ocrResult = extractWithTimeout(cardImage, "ara+fra+eng", OCR_TIMEOUT_MS);
        } catch (TimeoutException te) {
            // Si le fast-pass a déjà détecté un CIN différent, on peut rejeter fermement
            if (cinFoundFast != null && expectedCinDigits != null
                    && !cinFoundFast.equals(expectedCinDigits)) {
                return new VerificationResult(
                        VerificationStatus.REJECTED,
                        0.0,
                        cinFoundFast,
                        false,
                        "CIN différent: saisi=" + expectedCinDigits + ", détecté=" + cinFoundFast
                );
            }
            return new VerificationResult(
                    VerificationStatus.NEEDS_REVIEW,
                    0.0,
                    cinFoundFast,
                    false,
                    "OCR trop lent (timeout). Validation admin requise."
            );
        }

        String rawText = ocrResult.fullText();
        String text = normalizeForSearch(rawText);

        List<String> reasons = new ArrayList<>();

        List<String> cinCandidatesFull = extractAllCins(text);
        String cinFound = extractCinBestCandidate(text, cinCandidatesFull);
        logCinDebug("FULL", expectedCinDigits, cinFound, cinCandidatesFull);
        if (cinFound == null) {
            cinFound = cinFoundFast;
        }
        boolean cinMatches = expectedCinDigits != null && expectedCinDigits.matches("\\d{8}") && expectedCinDigits.equals(cinFound);

        boolean fellah = text.contains("فلاح");

        String nomAr = ArabicTextNormalizer.normalize(expectedNomAr);
        String prenomAr = ArabicTextNormalizer.normalize(expectedPrenomAr);
        boolean nameOk = !nomAr.isBlank() && !prenomAr.isBlank() && text.contains(nomAr) && text.contains(prenomAr);

        double score = 0.0;
        if (cinMatches) score += 0.55;
        else {
            if (cinFound == null) {
                reasons.add("CIN non détecté");
            } else {
                reasons.add("CIN détecté différent (" + cinFound + ")");
            }
        }

        if (fellah) score += 0.25;
        else reasons.add("Mot clé 'فلاح' non détecté");

        if (nameOk) score += 0.20;
        else reasons.add("Nom/prénom arabe non détecté ou ne correspond pas");

        // Decision
        VerificationStatus status;
        if (!cinMatches) {
            status = VerificationStatus.REJECTED;
        } else if (score >= 0.90) {
            status = VerificationStatus.APPROVED;
        } else {
            status = VerificationStatus.NEEDS_REVIEW;
        }

        String reason = String.join("; ", reasons);
        return new VerificationResult(status, score, cinFound, fellah, reason);
    }

    private static String digitsOnly(String s) {
        if (s == null) return null;
        String d = s.replaceAll("\\D", "");
        return d.isBlank() ? null : d;
    }

    private static String normalizeForSearch(String input) {
        // Keep Arabic letters, digits and spaces. Remove punctuation/noise to make keyword matching more reliable.
        String s = ArabicTextNormalizer.normalize(input);
        s = s.replaceAll("[^\\p{IsArabic}0-9A-Za-z ]", " ");
        s = s.replaceAll("\\s+", " ").trim();
        return s;
    }

    private OcrResult extractWithTimeout(Path image, String languages, long timeoutMs) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "ocr-worker");
            t.setDaemon(true);
            return t;
        });

        Future<OcrResult> future = executor.submit(() -> ocr.extractText(image, languages));
        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            future.cancel(true);
            throw te;
        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            if (cause instanceof Exception ex) throw ex;
            throw new RuntimeException(cause);
        } finally {
            executor.shutdownNow();
        }
    }

    private List<String> extractAllCins(String normalizedText) {
        if (normalizedText == null) return List.of();
        List<String> list = new ArrayList<>();
        Matcher m = CIN_PATTERN.matcher(normalizedText);
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }

    private String extractCinBestCandidate(String normalizedText, List<String> candidates) {
        if (normalizedText == null || candidates == null || candidates.isEmpty()) return null;

        // Prefer the 8-digit number closest to Arabic labels on the farmer card
        int[] keywordPositions = keywordPositions(normalizedText);
        if (keywordPositions.length == 0) {
            return candidates.get(0);
        }

        String best = null;
        int bestDistance = Integer.MAX_VALUE;

        for (String cin : candidates) {
            int idx = normalizedText.indexOf(cin);
            if (idx < 0) continue;
            int dist = minAbsDistance(idx, keywordPositions);
            if (dist < bestDistance) {
                bestDistance = dist;
                best = cin;
            }
        }

        return best != null ? best : candidates.get(0);
    }

    private int[] keywordPositions(String text) {
        if (text == null || text.isBlank()) return new int[0];
        // Common labels on the Tunisian farmer card
        String[] keys = new String[] {"رقم", "بتو", "ب ت و"};
        List<Integer> pos = new ArrayList<>();
        for (String k : keys) {
            int from = 0;
            while (true) {
                int idx = text.indexOf(k, from);
                if (idx < 0) break;
                pos.add(idx);
                from = idx + k.length();
            }
        }
        return pos.stream().filter(Objects::nonNull).mapToInt(Integer::intValue).toArray();
    }

    private int minAbsDistance(int index, int[] positions) {
        int best = Integer.MAX_VALUE;
        for (int p : positions) {
            best = Math.min(best, Math.abs(index - p));
        }
        return best;
    }

    private void logCinDebug(String stage, String expectedCinDigits, String selectedCin, List<String> candidates) {
        // Helps diagnose cases where OCR reads the wrong number.
        // Remove/guard in production.
        System.out.println(
                "[AgriFlow][OCR][CIN][" + stage + "] expected=" + (expectedCinDigits == null ? "" : expectedCinDigits)
                        + " selected=" + (selectedCin == null ? "" : selectedCin)
                        + " candidates=" + (candidates == null ? "[]" : candidates)
        );
    }
}
