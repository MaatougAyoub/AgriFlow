package services;

import entities.Reservation;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

// Génère des contrats PDF pour les réservations (iText 7)
public class PDFService {

        private static final String CONTRATS_DIR = "contrats";
        private static final DeviceRgb AGRI_GREEN = new DeviceRgb(46, 125, 50);
        private static final DeviceRgb AGRI_GRAY = new DeviceRgb(117, 117, 117);

        // Génère un contrat PDF à partir d'une réservation
        public File genererContrat(Reservation reservation) throws IOException {
                // Créer le dossier contrats s'il n'existe pas
                File dossier = new File(CONTRATS_DIR);
                if (!dossier.exists()) {
                        dossier.mkdirs();
                }

                // Générer un identifiant unique pour le contrat
                String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String nomFichier = String.format("Contrat_AgriFlow_%s_%s.pdf", reservation.getId(), uuid);
                File fichierPdf = new File(dossier, nomFichier);

                // Créer le document PDF
                PdfWriter writer = new PdfWriter(fichierPdf);
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);

                // En-tête
                document.add(new Paragraph("CONTRAT DE LOCATION AGRIFLOW")
                                .setFontSize(22)
                                .setBold()
                                .setFontColor(AGRI_GREEN)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginBottom(5));

                document.add(new Paragraph("Smart Farming Tunisia — Marketplace P2P")
                                .setFontSize(12)
                                .setFontColor(AGRI_GRAY)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginBottom(20));

                // Ligne de séparation
                document.add(new Paragraph("═════════════════════════════════════════════════")
                                .setFontColor(AGRI_GREEN)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginBottom(15));

                // Infos du contrat
                String nomProprietaire = "N/A";
                if (reservation.getProprietaire() != null) {
                        nomProprietaire = reservation.getProprietaire().getNomComplet();
                }

                String nomDemandeur = "N/A";
                if (reservation.getDemandeur() != null) {
                        nomDemandeur = reservation.getDemandeur().getNomComplet();
                }

                String nomAnnonce = "N/A";
                if (reservation.getAnnonce() != null) {
                        nomAnnonce = reservation.getAnnonce().getTitre();
                }

                document.add(new Paragraph("PARTIES DU CONTRAT")
                                .setBold()
                                .setFontSize(14)
                                .setFontColor(AGRI_GREEN)
                                .setMarginBottom(10));

                document.add(new Paragraph(
                                String.format("Je soussigné, %s (ci-après \"le Propriétaire\"), loue le matériel " +
                                                "\"%s\" à %s (ci-après \"le Demandeur\") pour la période du %s au %s.",
                                                nomProprietaire,
                                                nomAnnonce,
                                                nomDemandeur,
                                                reservation.getDateDebut() != null
                                                                ? reservation.getDateDebut().toString()
                                                                : "N/A",
                                                reservation.getDateFin() != null ? reservation.getDateFin().toString()
                                                                : "N/A"))
                                .setFontSize(12)
                                .setMarginBottom(20));

                // Tableau financier
                document.add(new Paragraph("DÉTAILS FINANCIERS")
                                .setBold()
                                .setFontSize(14)
                                .setFontColor(AGRI_GREEN)
                                .setMarginBottom(10));

                Table table = new Table(UnitValue.createPercentArray(new float[] { 60, 40 }))
                                .useAllAvailableWidth()
                                .setMarginBottom(20);

                // Entêtes
                table.addHeaderCell(new Cell().add(new Paragraph("Description").setBold())
                                .setBackgroundColor(new DeviceRgb(232, 245, 233)));
                table.addHeaderCell(new Cell().add(new Paragraph("Valeur").setBold())
                                .setBackgroundColor(new DeviceRgb(232, 245, 233)));

                // Durée
                int jours = reservation.getNombreJours();
                table.addCell("Durée de location");
                table.addCell(jours + " jour(s)");

                // Prix unitaire
                double prixJour = 0;
                if (reservation.getAnnonce() != null) {
                        prixJour = reservation.getAnnonce().getPrix();
                }
                table.addCell("Prix par jour");
                table.addCell(String.format("%.2f DT", prixJour));

                // Sous-total
                double sousTotal = prixJour * jours;
                table.addCell("Sous-total");
                table.addCell(String.format("%.2f DT", sousTotal));

                // Commission AgriFlow
                double commission = sousTotal * 0.10;
                table.addCell("Commission AgriFlow (10%)");
                table.addCell(String.format("%.2f DT", commission));

                // Montant total
                table.addCell(new Cell().add(new Paragraph("MONTANT TOTAL").setBold()));
                table.addCell(new Cell().add(new Paragraph(
                                String.format("%.2f DT", reservation.getPrixTotal())).setBold()
                                .setFontColor(AGRI_GREEN)));

                // Caution
                table.addCell("Caution");
                table.addCell(String.format("%.2f DT", reservation.getCaution()));

                document.add(table);

                // Conditions
                document.add(new Paragraph("CONDITIONS GÉNÉRALES")
                                .setBold()
                                .setFontSize(14)
                                .setFontColor(AGRI_GREEN)
                                .setMarginBottom(10));

                document.add(new Paragraph(
                                "1. Le matériel doit être restitué dans le même état qu'à la réception.\n" +
                                                "2. Toute détérioration sera déduite de la caution.\n" +
                                                "3. En cas d'annulation, les conditions de remboursement d'AgriFlow s'appliquent.\n"
                                                +
                                                "4. Ce contrat est généré automatiquement et fait foi entre les parties.")
                                .setFontSize(11)
                                .setMarginBottom(25));

                // Signature numérique
                document.add(new Paragraph("═════════════════════════════════════════════════")
                                .setFontColor(AGRI_GRAY)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginBottom(10));

                String dateSignature = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"));

                document.add(new Paragraph(
                                String.format("Signé numériquement via AgriFlow le %s", dateSignature))
                                .setFontSize(10)
                                .setItalic()
                                .setFontColor(AGRI_GRAY)
                                .setTextAlignment(TextAlignment.CENTER));

                document.add(new Paragraph(
                                String.format("ID Unique du Contrat : AGRI-%s", uuid))
                                .setFontSize(10)
                                .setBold()
                                .setFontColor(AGRI_GREEN)
                                .setTextAlignment(TextAlignment.CENTER));

                document.add(new Paragraph("Statut : " + reservation.getStatut().getLabel())
                                .setFontSize(10)
                                .setFontColor(AGRI_GRAY)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginTop(5));

                // Fermer le document
                document.close();

                System.out.println("Contrat PDF genere : " + fichierPdf.getAbsolutePath());
                return fichierPdf;
        }
}