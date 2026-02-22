package services.verification;

import entities.VerificationStatus;
import services.ocr.OcrResult;
import services.ocr.OcrService;

import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class ExpertCertificationVerificationService {

    private static final long OCR_TIMEOUT_MS = 25_000;

    private final OcrService ocr;

    public ExpertCertificationVerificationService(OcrService ocr) {
        this.ocr = ocr;
    }

    public VerificationResult verify(Path certImage, String expectedNom, String expectedPrenom) throws Exception {
        OcrResult ocrResult;
        try {
            ocrResult = extractWithTimeout(certImage, "eng+fra+ara", OCR_TIMEOUT_MS);
        } catch (TimeoutException te) {
            return new VerificationResult(
                    VerificationStatus.NEEDS_REVIEW,
                    0.0,
                    null,
                    false,
                    "OCR trop lent (timeout). Validation admin requise."
            );
        }
        String text = (ocrResult.fullText() == null ? "" : ocrResult.fullText()).toLowerCase();

        List<String> reasons = new ArrayList<>();

        boolean nameOk = containsIgnoreCase(text, expectedNom) && containsIgnoreCase(text, expectedPrenom);
        if (!nameOk) reasons.add("Nom/prénom non détecté sur la certification");

        double score = 0.0;
        if (nameOk) score += 0.6;

        // Heuristic credibility keywords
        String[] keywords = new String[]{
                "irrigation", "installation", "technician", "university", "certificate", "training", "fundamentals",
                "programme", "program", "completed", "certification"
        };
        int hits = 0;
        for (String k : keywords) {
            if (text.contains(k)) hits++;
        }
        if (hits >= 2) score += 0.4;
        else reasons.add("Crédibilité faible (mots-clés insuffisants)");

        VerificationStatus status;
        if (!nameOk) {
            status = VerificationStatus.REJECTED;
        } else if (score >= 0.85) {
            status = VerificationStatus.APPROVED;
        } else {
            status = VerificationStatus.NEEDS_REVIEW;
        }

        return new VerificationResult(status, score, null, false, String.join("; ", reasons));
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

    private boolean containsIgnoreCase(String textLowerCase, String value) {
        if (value == null) return false;
        String v = value.trim().toLowerCase();
        if (v.isEmpty()) return false;
        return textLowerCase.contains(v);
    }
}
