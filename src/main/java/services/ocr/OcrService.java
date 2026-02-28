package services.ocr;

import java.nio.file.Path;

public interface OcrService {
    OcrResult extractText(Path imagePath, String language) throws Exception;
}
