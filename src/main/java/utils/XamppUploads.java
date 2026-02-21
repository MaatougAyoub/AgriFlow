package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class XamppUploads {

    private XamppUploads() {
    }

    // Base demandée: C:\xampp\htdocs
    public static final Path BASE_DIR = Paths.get("C:\\xampp\\htdocs");

    public enum Category {
        SIGNATURES("signatures"),
        CERTIFICATIONS("certifications"),
        CARTES("cartes");

        private final String folderName;

        Category(String folderName) {
            this.folderName = folderName;
        }

        public String folderName() {
            return folderName;
        }
    }

    /**
     * Sauvegarde un fichier sous C:\xampp\htdocs\<category>\ et retourne le chemin
     * ABSOLU à stocker en base.
     */
    public static String save(File file, Category category) throws IOException {
        if (file == null) {
            return null;
        }

        if (!Files.exists(BASE_DIR)) {
            throw new IOException("Le dossier base n'existe pas: " + BASE_DIR + " (installez XAMPP ou corrigez le chemin)");
        }

        Path targetDir = BASE_DIR.resolve(category.folderName());
        Files.createDirectories(targetDir);

        String safeName = file.getName().replaceAll("[^A-Za-z0-9._-]", "_");
        String fileName = System.currentTimeMillis() + "_" + safeName;

        Path targetPath = targetDir.resolve(fileName);
        Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath.toString();
    }

    public static String fileNameFromPath(String maybePath) {
        if (maybePath == null) {
            return "";
        }
        String s = maybePath.trim();
        if (s.isEmpty()) {
            return "";
        }
        try {
            Path p = Paths.get(s);
            Path name = p.getFileName();
            return name == null ? s : name.toString();
        } catch (Exception ignored) {
            return s;
        }
    }
}
