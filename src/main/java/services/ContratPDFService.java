package services;

import entities.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ContratPDFService {

    private final UserService userService;
    private final String CONTRATS_DIRECTORY = "contrats/";

    public ContratPDFService() {
        this.userService = new UserService();
        File contratsDir = new File(CONTRATS_DIRECTORY);
        if (!contratsDir.exists()) {
            contratsDir.mkdirs();
        }
    }

    public String genererContratPDF(Reservation reservation) throws Exception {
        if (reservation == null || reservation.getAnnonce() == null) {
            throw new IllegalArgumentException("Reservation invalide");
        }

        Annonce annonce = reservation.getAnnonce();
        User proprietaire = reservation.getProprietaire();
        User demandeur = reservation.getDemandeur();

        // Charger les signatures depuis les chemins fichiers (table utilisateurs)
        String sigPathProprio = proprietaire != null ? proprietaire.getSignature() : null;
        String sigPathDemandeur = demandeur != null ? demandeur.getSignature() : null;

        String nomFichier = String.format("contrat_%d_%s.pdf",
            reservation.getId(),
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        String cheminFichier = CONTRATS_DIRECTORY + nomFichier;

        genererPDF(reservation, sigPathProprio, sigPathDemandeur, cheminFichier);

        return cheminFichier;
    }

    private void genererPDF(Reservation reservation, String sigPathProprio, String sigPathDemandeur, String chemin) throws Exception {
        Annonce annonce = reservation.getAnnonce();
        User proprietaire = reservation.getProprietaire();
        User demandeur = reservation.getDemandeur();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(chemin));
        Document document = new Document(pdfDoc);

        // En-tête
        document.add(new Paragraph("AGRIFLOW - SMART FARMING")
            .setFontSize(24)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Contrat de " + annonce.getType().getLabel())
            .setFontSize(18)
            .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("\n"));

        // Informations
        Table infoTable = new Table(2);
        infoTable.setWidth(UnitValue.createPercentValue(100));
        infoTable.addCell("Date:").setBold();
        infoTable.addCell(LocalDate.now().format(formatter));
        infoTable.addCell("Référence:").setBold();
        infoTable.addCell("AGR-" + reservation.getId());
        document.add(infoTable);

        document.add(new Paragraph("\n"));

        // Parties
        document.add(new Paragraph("PROPRIÉTAIRE:").setBold());
        document.add(new Paragraph("Nom: " + proprietaire.getNomComplet()));
        document.add(new Paragraph("Email: " + proprietaire.getEmail()));
        document.add(new Paragraph("CIN: " + proprietaire.getCin()));

        document.add(new Paragraph("\n"));

        document.add(new Paragraph("DEMANDEUR:").setBold());
        document.add(new Paragraph("Nom: " + demandeur.getNomComplet()));
        document.add(new Paragraph("Email: " + demandeur.getEmail()));
        document.add(new Paragraph("CIN: " + demandeur.getCin()));

        document.add(new Paragraph("\n"));

        // Objet
        document.add(new Paragraph("OBJET:").setBold().setUnderline());
        document.add(new Paragraph("Titre: " + annonce.getTitre()));
        document.add(new Paragraph("Description: " + annonce.getDescription()));
        document.add(new Paragraph("Prix: " + annonce.getPrixFormate()));

        if (annonce.estEnLocation()) {
            document.add(new Paragraph("Période: Du " + reservation.getDateDebut().format(formatter) +
                " au " + reservation.getDateFin().format(formatter)));
        }

        document.add(new Paragraph("Prix Total: " + String.format("%.2f DT", reservation.getPrixTotal()))
            .setBold().setFontSize(14));

        document.add(new Paragraph("\n"));

        // Signatures
        document.add(new Paragraph("SIGNATURES:").setBold().setUnderline());
        document.add(new Paragraph("\n"));

        Table signaturesTable = new Table(2);
        signaturesTable.setWidth(UnitValue.createPercentValue(100));

        // Signature Propriétaire
        Cell cellProprio = new Cell();
        cellProprio.add(new Paragraph("PROPRIÉTAIRE").setBold().setTextAlignment(TextAlignment.CENTER));
        cellProprio.add(new Paragraph(proprietaire.getNomComplet()).setTextAlignment(TextAlignment.CENTER));

        if (sigPathProprio != null && !sigPathProprio.isEmpty()) {
            try {
                File sigFile = new File(sigPathProprio);
                if (sigFile.exists()) {
                    ImageData imageData = ImageDataFactory.create(sigPathProprio);
                    com.itextpdf.layout.element.Image signatureImage = new com.itextpdf.layout.element.Image(imageData);
                    signatureImage.setWidth(150);
                    signatureImage.setHeight(60);
                    cellProprio.add(signatureImage);
                } else {
                    cellProprio.add(new Paragraph("\n\n_________________").setTextAlignment(TextAlignment.CENTER));
                }
            } catch (Exception e) {
                cellProprio.add(new Paragraph("\n\n_________________").setTextAlignment(TextAlignment.CENTER));
            }
        } else {
            cellProprio.add(new Paragraph("\n\n_________________").setTextAlignment(TextAlignment.CENTER));
        }
        signaturesTable.addCell(cellProprio);

        // Signature Demandeur
        Cell cellDemandeur = new Cell();
        cellDemandeur.add(new Paragraph("DEMANDEUR").setBold().setTextAlignment(TextAlignment.CENTER));
        cellDemandeur.add(new Paragraph(demandeur.getNomComplet()).setTextAlignment(TextAlignment.CENTER));

        if (sigPathDemandeur != null && !sigPathDemandeur.isEmpty()) {
            try {
                File sigFile = new File(sigPathDemandeur);
                if (sigFile.exists()) {
                    ImageData imageData = ImageDataFactory.create(sigPathDemandeur);
                    com.itextpdf.layout.element.Image signatureImage = new com.itextpdf.layout.element.Image(imageData);
                    signatureImage.setWidth(150);
                    signatureImage.setHeight(60);
                    cellDemandeur.add(signatureImage);
                } else {
                    cellDemandeur.add(new Paragraph("\n\n_________________").setTextAlignment(TextAlignment.CENTER));
                }
            } catch (Exception e) {
                cellDemandeur.add(new Paragraph("\n\n_________________").setTextAlignment(TextAlignment.CENTER));
            }
        } else {
            cellDemandeur.add(new Paragraph("\n\n_________________").setTextAlignment(TextAlignment.CENTER));
        }
        signaturesTable.addCell(cellDemandeur);

        document.add(signaturesTable);

        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("Ce contrat a été généré automatiquement par AGRIFLOW.")
            .setFontSize(8)
            .setTextAlignment(TextAlignment.CENTER));

        document.close();
        pdfDoc.close();
    }
}
