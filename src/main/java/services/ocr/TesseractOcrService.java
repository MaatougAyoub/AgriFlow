package services.ocr;

import net.sourceforge.tess4j.Tesseract;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TesseractOcrService implements OcrService {

    @Override
    public OcrResult extractText(Path imagePath, String language) throws Exception {
        if (imagePath == null) {
            throw new IllegalArgumentException("imagePath is null");
        }
        if (!Files.exists(imagePath)) {
            throw new IllegalArgumentException("Image introuvable: " + imagePath);
        }

        Path preprocessed = preprocessToTempPng(imagePath);
        try {
            Tesseract tesseract = new Tesseract();
            // Ensure datapath points to the actual tessdata folder (tess4j expects the folder that contains the .traineddata files)
            tesseract.setDatapath(resolveTessDataDir().toString());
            if (language != null && !language.isBlank()) {
                tesseract.setLanguage(language);
            }

            String text = tesseract.doOCR(preprocessed.toFile());
            if (text == null) text = "";
            return new OcrResult(text);
        } finally {
            try {
                Files.deleteIfExists(preprocessed);
            } catch (Exception ignored) {
            }
        }
    }

    private Path resolveTessDataDir() {
        // We need to return the actual tessdata directory that contains the .traineddata files.
        String env = System.getenv("TESSDATA_PREFIX");
        if (env != null && !env.isBlank()) {
            Path p = Path.of(env);
            // If env points directly to tessdata, use it
            if (p.getFileName() != null && p.getFileName().toString().equalsIgnoreCase("tessdata")) {
                if (Files.exists(p)) return p;
                // else fallthrough
            }
            // If env points to the parent, check parent/tessdata
            if (Files.exists(p.resolve("tessdata"))) {
                return p.resolve("tessdata");
            }
        }

        List<Path> candidates = new ArrayList<>();
        candidates.add(Path.of("C:\\Program Files\\Tesseract-OCR\\tessdata"));
        candidates.add(Path.of("C:\\Program Files (x86)\\Tesseract-OCR\\tessdata"));
        candidates.add(Path.of("C:\\tesseract\\tessdata"));

        for (Path c : candidates) {
            if (Files.exists(c)) {
                return c;
            }
        }

        throw new IllegalStateException(
                "Tesseract introuvable. Installez Tesseract OCR et configurez TESSDATA_PREFIX (ex: C:/Program Files/Tesseract-OCR or C:/Program Files/Tesseract-OCR/tessdata)."
        );
    }

    private Path preprocessToTempPng(Path input) throws Exception {
        BufferedImage original = ImageIO.read(input.toFile());
        if (original == null) {
            // fallback: just copy
            Path tmp = Files.createTempFile("ocr_", ".png");
            Files.copy(input, tmp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return tmp;
        }

        // upscale a bit to improve OCR
        int targetW = Math.max(original.getWidth(), 1400);
        double scale = (double) targetW / (double) original.getWidth();
        int targetH = (int) Math.round(original.getHeight() * scale);

        BufferedImage scaled = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(original, 0, 0, targetW, targetH, null);
        g.dispose();

        // grayscale
        BufferedImage gray = new BufferedImage(targetW, targetH, BufferedImage.TYPE_BYTE_GRAY);
        Graphics gg = gray.getGraphics();
        gg.drawImage(scaled, 0, 0, null);
        gg.dispose();

        Path tmp = Files.createTempFile("ocr_", ".png");
        ImageIO.write(gray, "png", tmp.toFile());
        return tmp;
    }
}
