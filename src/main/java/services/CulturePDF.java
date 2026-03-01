package services;

import entities.Culture;
import entities.Parcelle;
import entities.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.nio.file.Files;
import java.nio.file.Path;

import java.io.File;
import java.time.LocalDate;

public class CulturePDF {

    public File genererContratVente(Culture c, User vendeur, User acheteur, Parcelle parcelle) throws Exception {

        String fileName = "contrat_culture_" + c.getId() + "_" + LocalDate.now() + ".pdf";
        File out = new File(System.getProperty("user.home") + File.separator + "Desktop", fileName);

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float left = 60;
                float y = 780;

                // Titre
                y = write(cs, left, y, PDType1Font.HELVETICA_BOLD, 18, "CONTRAT DE VENTE - CULTURE");
                y -= 10;

                // Infos
                y = write(cs, left, y, PDType1Font.HELVETICA, 12,
                        "Date : " + (c.getDateVente() != null ? c.getDateVente().toString() : LocalDate.now()));
                y = write(cs, left, y, PDType1Font.HELVETICA, 12,
                        "Vendeur : " + fullName(vendeur) + " (ID " + vendeur.getId() + ")");
                y = write(cs, left, y, PDType1Font.HELVETICA, 12,
                        "Acheteur : " + fullName(acheteur) + " (ID " + acheteur.getId() + ")");

                y -= 10;

                y = write(cs, left, y, PDType1Font.HELVETICA_BOLD, 12, "Objet de la vente");
                y = write(cs, left, y, PDType1Font.HELVETICA, 12,
                        "Culture : " + safe(c.getNom()) + " | Type : " + safeEnum(c.getTypeCulture()));
                y = write(cs, left, y, PDType1Font.HELVETICA, 12,
                        "Superficie : " + c.getSuperficie() + " m2 | Recolte estimee : " + safeNum(c.getRecolteEstime()) + " Kg");
                y = write(cs, left, y, PDType1Font.HELVETICA, 12,
                        "Prix : " + safeNum(c.getPrixVente()) + " DT");

                y -= 10;

                y = write(cs, left, y, PDType1Font.HELVETICA_BOLD, 12, "Parcelle");
                y = write(cs, left, y, PDType1Font.HELVETICA, 12,
                        "Nom : " + safe(parcelle.getNom()) + " | Type terre : " + safeEnum(parcelle.getTypeTerre()));
                y = write(cs, left, y, PDType1Font.HELVETICA, 12,
                        "Localisation : " + safe(parcelle.getLocalisation()) + " | Superficie parcelle : " + parcelle.getSuperficie() + " m2");

                y -= 10;

                y = write(cs, left, y, PDType1Font.HELVETICA_BOLD, 12, "Clauses");
                y = write(cs, left, y, PDType1Font.HELVETICA, 12,
                        "1) L'acheteur accepte la culture en l'etat.");
                y = write(cs, left, y, PDType1Font.HELVETICA, 12,
                        "2) La recolte/recuperation se fait selon accord entre les parties.");
                y = write(cs, left, y, PDType1Font.HELVETICA, 12,
                        "3) Paiement : " + safeNum(c.getPrixVente()) + " DT.");

                y -= 20;
                float sigY = 120;     // hauteur depuis bas page
                float sigW = 180;
                float sigH = 60;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                cs.newLineAtOffset(left, sigY + 70);
                cs.showText("Signatures");
                cs.endText();

// Vendeur
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 11);
                cs.newLineAtOffset(left, sigY + 50);
                cs.showText("Vendeur: " + vendeur.getNom() + " " + vendeur.getPrenom());
                cs.endText();

                boolean okV = drawSignatureImage(doc, cs, vendeur.getSignature(), left, sigY, sigW, sigH);
                if (!okV) {
                    // fallback ligne si pas d'image
                    cs.moveTo(left, sigY + 20);
                    cs.lineTo(left + sigW, sigY + 20);
                    cs.stroke();
                }

// Acheteur
                float rightX = left + 260;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 11);
                cs.newLineAtOffset(rightX, sigY + 50);
                cs.showText("Acheteur: " + acheteur.getNom() + " " + acheteur.getPrenom());
                cs.endText();

                boolean okA = drawSignatureImage(doc, cs, acheteur.getSignature(), rightX, sigY, sigW, sigH);
                if (!okA) {
                    cs.moveTo(rightX, sigY + 20);
                    cs.lineTo(rightX + sigW, sigY + 20);
                    cs.stroke();
                }
            }

            doc.save(out);
        }

        return out;
    }

    private float write(PDPageContentStream cs, float x, float y,
                        org.apache.pdfbox.pdmodel.font.PDFont font, int size, String text) throws Exception {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - 18;
    }
    private boolean drawSignatureImage(PDDocument doc, PDPageContentStream cs, String path,
                                       float x, float y, float w, float h) {
        try {
            if (path == null || path.isBlank()) return false;
            Path p = Path.of(path);
            if (!Files.exists(p)) return false;

            PDImageXObject img = PDImageXObject.createFromFileByContent(p.toFile(), doc);
            cs.drawImage(img, x, y, w, h);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private String safe(String s) { return (s == null || s.isBlank()) ? "-" : s; }
    private String safeNum(Double d) { return d == null ? "-" : String.valueOf(d); }
    private String safeEnum(Object e) { return e == null ? "-" : e.toString(); }
    private String fullName(User u) { return safe(u.getNom()) + " " + safe(u.getPrenom()); }
}