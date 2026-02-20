package controllers;

import entities.Reservation;
import entities.StatutReservation;
import entities.User;
import services.PDFService;
import services.ServiceReservation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

// Controleur mta3 "Mes Reservations" - ytalla3 les reservations mta3 el user
// kol reservation tetaffiche f carte (card) fih les details + bouton PDF
public class MesReservationsController implements Initializable {

    // SVG Path Constants
    private static final String SVG_CALENDAR = "M9 11H7v2h2v-2zm4 0h-2v2h2v-2zm4 0h-2v2h2v-2zm2-7h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V9h14v11z";
    private static final String SVG_CLOCK = "M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67V7z";
    private static final String SVG_MONEY = "M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z";
    private static final String SVG_TAG = "M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41 0-.55-.23-1.06-.59-1.42zM5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7z";
    private static final String SVG_DELETE = "M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z";
    private static final String SVG_PDF = "M8 16h8v2H8zm0-4h8v2H8zm6-10H6c-1.1 0-2 .9-2 2v16c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm4 18H6V4h7v5h5v11z";
    private static final String SVG_PACKAGE = "M20 2H4c-1 0-2 .9-2 2v3.01c0 .72.43 1.34 1 1.69V20c0 1.1 1.1 2 2 2h14c.9 0 2-.9 2-2V8.7c.57-.35 1-.97 1-1.69V4c0-1.1-1-2-2-2zm-5 12H9v-2h6v2zm5-7H4V4h16v3z";

    @FXML
    private VBox cardsContainer;
    @FXML
    private Label infoLabel;
    @FXML
    private ScrollPane scrollPane;

    private final ServiceReservation reservationService = new ServiceReservation();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chargerReservations();
    }

    private void chargerReservations() {
        cardsContainer.getChildren().clear();

        User currentUser = MainController.getCurrentUser();
        if (currentUser == null) {
            infoLabel.setText("Aucun utilisateur connecté.");
            return;
        }

        try {
            // ===== SECTION 1: Demandes Reçues (sur MON matériel) =====
            List<Reservation> recues = reservationService.recupererParProprietaire(currentUser.getId());

            if (!recues.isEmpty()) {
                Label sectionRecues = new Label("📩 Demandes Reçues (" + recues.size() + ")");
                sectionRecues.setStyle(
                        "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1565C0; -fx-padding: 0 0 10 5;");
                sectionRecues.setMaxWidth(Double.MAX_VALUE);
                cardsContainer.getChildren().add(sectionRecues);

                // n7ottou les cartes mta3 demandes reçues fi FlowPane wa7dou bech yjiw mna4min
                FlowPane flowRecues = new FlowPane();
                flowRecues.setHgap(18);
                flowRecues.setVgap(18);

                for (Reservation reservation : recues) {
                    VBox card = creerCarteDemandeRecue(reservation);
                    flowRecues.getChildren().add(card);
                }
                cardsContainer.getChildren().add(flowRecues);

                // Séparateur
                Region sep = new Region();
                sep.setPrefHeight(2);
                sep.setPrefWidth(Double.MAX_VALUE);
                sep.setMaxWidth(Double.MAX_VALUE);
                sep.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 0;");
                cardsContainer.getChildren().add(sep);
            }

            // ===== SECTION 2: Mes Demandes (envoyées) =====
            List<Reservation> envoyees = reservationService.recupererParDemandeur(currentUser.getId());

            Label sectionEnvoyees = new Label("📤 Mes Demandes (" + envoyees.size() + ")");
            sectionEnvoyees.setStyle(
                    "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E7D32; -fx-padding: 10 0 10 5;");
            sectionEnvoyees.setMaxWidth(Double.MAX_VALUE);
            cardsContainer.getChildren().add(sectionEnvoyees);

            int total = recues.size() + envoyees.size();
            infoLabel.setText(total + " réservation(s) trouvée(s)");

            if (envoyees.isEmpty()) {
                VBox emptyBox = new VBox(10);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.setPadding(new Insets(40));

                SVGPath emptyIcon = createSVGIcon(SVG_PACKAGE, "#BDBDBD", 2.0);
                Label emptyLabel = new Label("Aucune demande envoyée pour le moment.");
                emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #9E9E9E;");
                Label emptyHint = new Label("Rendez-vous sur le Marketplace pour réserver un équipement.");
                emptyHint.setStyle("-fx-font-size: 13px; -fx-text-fill: #BDBDBD;");

                emptyBox.getChildren().addAll(emptyIcon, emptyLabel, emptyHint);
                cardsContainer.getChildren().add(emptyBox);
            } else {
                // w houni zeda FlowPane o5ra lel demandes envoyées bech yotl3ou mna4min b3ad 3la b3adh'hom
                FlowPane flowEnvoyees = new FlowPane();
                flowEnvoyees.setHgap(18);
                flowEnvoyees.setVgap(18);
                
                for (Reservation reservation : envoyees) {
                    VBox card = creerCarteReservation(reservation);
                    flowEnvoyees.getChildren().add(card);
                }
                cardsContainer.getChildren().add(flowEnvoyees);
            }
        } catch (SQLException e) {
            infoLabel.setText("Erreur de chargement : " + e.getMessage());
            System.err.println("Erreur chargement réservations : " + e.getMessage());
        }
    }

    // Construction des cartes

    private VBox creerCarteReservation(Reservation reservation) {
        VBox card = new VBox();
        card.setSpacing(0);
        card.setPrefWidth(310);
        card.setMaxWidth(310);
        card.setStyle(cardStyle(false));

        HBox header = creerHeader(reservation);
        VBox body = creerBody(reservation);
        HBox footer = creerFooter(reservation);

        card.getChildren().addAll(header, body, footer);

        card.setOnMouseEntered(e -> card.setStyle(cardStyle(true)));
        card.setOnMouseExited(e -> card.setStyle(cardStyle(false)));

        return card;
    }

    private String cardStyle(boolean hovered) {
        if (hovered) {
            return "-fx-background-color: white; -fx-background-radius: 14; "
                    + "-fx-effect: dropshadow(gaussian, rgba(45,90,39,0.20), 16, 0, 0, 5); "
                    + "-fx-scale-x: 1.02; -fx-scale-y: 1.02;";
        }
        return "-fx-background-color: white; -fx-background-radius: 14; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);";
    }

    /**
     * Header avec gradient coloré + titre + badge statut.
     * EN_ATTENTE = bleu, ACCEPTEE = vert, REFUSEE = rouge, etc.
     */
    private HBox creerHeader(Reservation reservation) {
        String bgColor = getStatutHeaderColor(reservation);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);
        header.setPadding(new Insets(14, 16, 14, 16));
        header.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 14 14 0 0;");

        SVGPath packageIcon = createSVGIcon(SVG_PACKAGE, "white", 0.75);

        String titre = "N/A";
        if (reservation.getAnnonce() != null && reservation.getAnnonce().getTitre() != null) {
            titre = reservation.getAnnonce().getTitre();
            if (titre.length() > 28)
                titre = titre.substring(0, 25) + "...";
        }
        Label titreLabel = new Label(titre);
        titreLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        titreLabel.setMaxWidth(170);
        HBox.setHgrow(titreLabel, Priority.ALWAYS);

        Label badge = creerBadgeStatut(reservation);

        header.getChildren().addAll(packageIcon, titreLabel, badge);
        return header;
    }

    private VBox creerBody(Reservation reservation) {
        VBox body = new VBox();
        body.setSpacing(10);
        body.setPadding(new Insets(16, 16, 12, 16));

        String dateDebut = reservation.getDateDebut() != null ? reservation.getDateDebut().format(DATE_FMT) : "N/A";
        String dateFin = reservation.getDateFin() != null ? reservation.getDateFin().format(DATE_FMT) : "N/A";
        HBox ligneDates = creerLigneInfo(SVG_CALENDAR, "#1E88E5", dateDebut + "  →  " + dateFin, "#555");

        long jours = 0;
        if (reservation.getDateDebut() != null && reservation.getDateFin() != null) {
            jours = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin()) + 1;
        }
        HBox ligneDuree = creerLigneInfo(SVG_CLOCK, "#1E88E5", jours + " jour(s)", "#555");

        String prix = String.format("%.2f DT", reservation.getPrixTotal());
        HBox lignePrix = creerLigneInfo(SVG_MONEY, "#2D5A27", prix, "#2D5A27");
        if (lignePrix.getChildren().size() > 1) {
            lignePrix.getChildren().get(1)
                    .setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2D5A27;");
        }

        body.getChildren().addAll(ligneDates, ligneDuree, lignePrix);

        if (reservation.getCaution() > 0) {
            HBox ligneCaution = creerLigneInfo(SVG_TAG, "#7B1FA2",
                    String.format("Caution: %.2f DT", reservation.getCaution()), "#7B1FA2");
            body.getChildren().add(ligneCaution);
        }

        // Afficher la réponse du propriétaire (si disponible)
        if (reservation.getReponseProprietaire() != null && !reservation.getReponseProprietaire().isBlank()) {
            String statutEmoji = reservation.getStatut() == StatutReservation.ACCEPTEE ? "✅" : "❌";
            Label reponseLabel = new Label(
                    statutEmoji + " Réponse du propriétaire : " + reservation.getReponseProprietaire());
            reponseLabel.setWrapText(true);
            String bgColor = reservation.getStatut() == StatutReservation.ACCEPTEE ? "#E8F5E9" : "#FFEBEE";
            String txtColor = reservation.getStatut() == StatutReservation.ACCEPTEE ? "#2E7D32" : "#C62828";
            reponseLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + txtColor
                    + "; -fx-padding: 6 8; -fx-background-color: " + bgColor + "; -fx-background-radius: 6;");
            body.getChildren().add(reponseLabel);
        }

        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #EEEEEE;");
        body.getChildren().add(separator);

        return body;
    }

    private HBox creerFooter(Reservation reservation) {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setSpacing(12);
        footer.setPadding(new Insets(0, 16, 14, 16));

        // Delete button — rouge
        Button btnSupprimer = creerIconButton(SVG_DELETE, "#E53935", "#FFEBEE", "#FFCDD2");
        btnSupprimer.setTooltip(new Tooltip("Supprimer la réservation"));
        btnSupprimer.setOnAction(e -> supprimerReservation(reservation));

        // PDF button — bleu
        Button btnContrat = creerIconButton(SVG_PDF, "#1565C0", "#E3F2FD", "#BBDEFB");
        btnContrat.setTooltip(new Tooltip("Générer contrat PDF"));
        btnContrat.setOnAction(e -> genererContratPDF(reservation));

        footer.getChildren().addAll(btnSupprimer, btnContrat);
        return footer;
    }

    // ===== Carte pour les demandes reçues (propriétaire) =====

    private VBox creerCarteDemandeRecue(Reservation reservation) {
        VBox card = new VBox();
        card.setSpacing(0);
        card.setPrefWidth(340);
        card.setMaxWidth(340);
        card.setStyle(cardStyle(false));

        HBox header = creerHeader(reservation);
        VBox body = creerBodyDemandeRecue(reservation);
        HBox footer = creerFooterDemandeRecue(reservation);

        card.getChildren().addAll(header, body, footer);

        card.setOnMouseEntered(e -> card.setStyle(cardStyle(true)));
        card.setOnMouseExited(e -> card.setStyle(cardStyle(false)));

        return card;
    }

    private VBox creerBodyDemandeRecue(Reservation reservation) {
        VBox body = new VBox();
        body.setSpacing(10);
        body.setPadding(new Insets(16, 16, 12, 16));

        // Qui demande ?
        String demandeurNom = "Inconnu";
        if (reservation.getDemandeur() != null) {
            demandeurNom = reservation.getDemandeur().getNomComplet();
        }
        HBox ligneDemandeur = creerLigneInfo(SVG_PACKAGE, "#6A1B9A", "De : " + demandeurNom, "#6A1B9A");
        if (ligneDemandeur.getChildren().size() > 1) {
            ligneDemandeur.getChildren().get(1)
                    .setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #6A1B9A;");
        }

        // Dates
        String dateDebut = reservation.getDateDebut() != null ? reservation.getDateDebut().format(DATE_FMT) : "N/A";
        String dateFin = reservation.getDateFin() != null ? reservation.getDateFin().format(DATE_FMT) : "N/A";
        HBox ligneDates = creerLigneInfo(SVG_CALENDAR, "#1E88E5", dateDebut + "  →  " + dateFin, "#555");

        long jours = 0;
        if (reservation.getDateDebut() != null && reservation.getDateFin() != null) {
            jours = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin()) + 1;
        }
        HBox ligneDuree = creerLigneInfo(SVG_CLOCK, "#1E88E5", jours + " jour(s)", "#555");

        // Prix
        String prix = String.format("%.2f DT", reservation.getPrixTotal());
        HBox lignePrix = creerLigneInfo(SVG_MONEY, "#2D5A27", prix, "#2D5A27");
        if (lignePrix.getChildren().size() > 1) {
            lignePrix.getChildren().get(1)
                    .setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2D5A27;");
        }

        body.getChildren().addAll(ligneDemandeur, ligneDates, ligneDuree, lignePrix);

        // Message du demandeur
        if (reservation.getMessageDemande() != null && !reservation.getMessageDemande().isBlank()) {
            Label msgLabel = new Label("💬 " + reservation.getMessageDemande());
            msgLabel.setWrapText(true);
            msgLabel.setStyle(
                    "-fx-font-size: 12px; -fx-text-fill: #616161; -fx-font-style: italic; -fx-padding: 6 8; -fx-background-color: #F5F5F5; -fx-background-radius: 6;");
            body.getChildren().add(msgLabel);
        }

        // Réponse du propriétaire (si déjà répondu)
        if (reservation.getReponseProprietaire() != null && !reservation.getReponseProprietaire().isBlank()) {
            Label reponseLabel = new Label("✉ Votre réponse : " + reservation.getReponseProprietaire());
            reponseLabel.setWrapText(true);
            reponseLabel.setStyle(
                    "-fx-font-size: 12px; -fx-text-fill: #2E7D32; -fx-padding: 6 8; -fx-background-color: #E8F5E9; -fx-background-radius: 6;");
            body.getChildren().add(reponseLabel);
        }

        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #EEEEEE;");
        body.getChildren().add(separator);

        return body;
    }

    private HBox creerFooterDemandeRecue(Reservation reservation) {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setSpacing(12);
        footer.setPadding(new Insets(8, 16, 14, 16));

        if (reservation.getStatut() == StatutReservation.EN_ATTENTE) {
            // Bouton Accepter
            Button btnAccepter = new Button("✅ Accepter");
            btnAccepter.setStyle("-fx-background-color: #43A047; -fx-text-fill: white; -fx-font-weight: bold; "
                    + "-fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px;");
            btnAccepter.setOnMouseEntered(e -> btnAccepter
                    .setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; "
                            + "-fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
            btnAccepter.setOnMouseExited(e -> btnAccepter
                    .setStyle("-fx-background-color: #43A047; -fx-text-fill: white; -fx-font-weight: bold; "
                            + "-fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px;"));
            btnAccepter.setOnAction(e -> onAccepterReservation(reservation));

            // Bouton Refuser
            Button btnRefuser = new Button("❌ Refuser");
            btnRefuser.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold; "
                    + "-fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px;");
            btnRefuser.setOnMouseEntered(e -> btnRefuser
                    .setStyle("-fx-background-color: #C62828; -fx-text-fill: white; -fx-font-weight: bold; "
                            + "-fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
            btnRefuser.setOnMouseExited(e -> btnRefuser
                    .setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold; "
                            + "-fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px;"));
            btnRefuser.setOnAction(e -> onRefuserReservation(reservation));

            footer.getChildren().addAll(btnAccepter, btnRefuser);
        } else {
            // Déjà traité — afficher le statut
            Label statutLabel = new Label(reservation.getStatut() != null ? reservation.getStatut().getLabel() : "N/A");
            statutLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #757575;");
            footer.getChildren().add(statutLabel);
        }

        return footer;
    }

    private void onAccepterReservation(Reservation reservation) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(
                "Marhbé bik ! Demande ma9boula.");
        dialog.setTitle("Acceptation");
        dialog.setHeaderText("Accepter la demande de "
                + (reservation.getDemandeur() != null ? reservation.getDemandeur().getNomComplet() : "?"));
        dialog.setContentText("Message mta3ek (optionnel) :");

        dialog.showAndWait().ifPresent(reponse -> {
            try {
                reservationService.accepterReservation(reservation.getId(), reponse.trim());
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("C bon");
                ok.setHeaderText(null);
                ok.setContentText("Réservation t9eblet ✅");
                ok.showAndWait();
                chargerReservations();
            } catch (SQLException e) {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Mochkla");
                err.setContentText("Famma mochkla, ma tnajamch t'accepter : " + e.getMessage());
                err.showAndWait();
            }
        });
    }

    private void onRefuserReservation(Reservation reservation) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Refus");
        dialog.setHeaderText("Bech tarfedh demande mta3 "
                + (reservation.getDemandeur() != null ? reservation.getDemandeur().getNomComplet() : "?"));
        dialog.setContentText("A3tih rayek 3lech rfatht :");

        dialog.showAndWait().ifPresent(reponse -> {
            if (reponse.trim().isEmpty()) {
                Alert warn = new Alert(Alert.AlertType.WARNING);
                warn.setTitle("Rod balek");
                warn.setContentText("A3mel mzouya a3tih sbab l'refus.");
                warn.showAndWait();
                return;
            }
            try {
                reservationService.refuserReservation(reservation.getId(), reponse.trim());
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Succès");
                ok.setHeaderText(null);
                ok.setContentText("Réservation refusée.");
                ok.showAndWait();
                chargerReservations();
            } catch (SQLException e) {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Erreur");
                err.setContentText("Impossible de refuser : " + e.getMessage());
                err.showAndWait();
            }
        });
    }

    // SVG Helpers

    private SVGPath createSVGIcon(String path, String color, double scale) {
        SVGPath svg = new SVGPath();
        svg.setContent(path);
        svg.setFill(Color.web(color));
        svg.setScaleX(scale);
        svg.setScaleY(scale);
        return svg;
    }

    private HBox creerLigneInfo(String svgPath, String iconColor, String texte, String textColor) {
        HBox ligne = new HBox();
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setSpacing(10);

        SVGPath icon = createSVGIcon(svgPath, iconColor, 0.7);
        StackPane iconContainer = new StackPane(icon);
        iconContainer.setMinWidth(24);
        iconContainer.setMaxWidth(24);

        Label valeurLabel = new Label(texte);
        valeurLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + textColor + ";");

        ligne.getChildren().addAll(iconContainer, valeurLabel);
        return ligne;
    }

    private Button creerIconButton(String svgPath, String iconColor, String bgNormal, String bgHover) {
        SVGPath icon = createSVGIcon(svgPath, iconColor, 0.72);

        Button btn = new Button();
        btn.setGraphic(icon);
        String normalStyle = "-fx-background-color: " + bgNormal + "; "
                + "-fx-background-radius: 50; -fx-min-width: 42; -fx-min-height: 42; "
                + "-fx-max-width: 42; -fx-max-height: 42; -fx-cursor: hand; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 4, 0, 0, 1);";
        String hoverStyle = "-fx-background-color: " + bgHover + "; "
                + "-fx-background-radius: 50; -fx-min-width: 42; -fx-min-height: 42; "
                + "-fx-max-width: 42; -fx-max-height: 42; -fx-cursor: hand; "
                + "-fx-scale-x: 1.1; -fx-scale-y: 1.1; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);";

        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));

        return btn;
    }

    // Badges et couleurs

    private Label creerBadgeStatut(Reservation reservation) {
        String statutText = reservation.getStatut() != null ? reservation.getStatut().getLabel() : "N/A";
        Label badge = new Label(statutText);
        badge.setStyle(getBadgeStyle(reservation));
        return badge;
    }

    private String getBadgeStyle(Reservation r) {
        String base = "-fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;";
        if (r.getStatut() == null) {
            return base + " -fx-background-color: rgba(255,255,255,0.25); -fx-text-fill: white;";
        }
        switch (r.getStatut()) {
            case ACCEPTEE:
            case TERMINEE:
                return base + " -fx-background-color: rgba(255,255,255,0.92); -fx-text-fill: #388E3C;";
            case REFUSEE:
            case ANNULEE:
                return base + " -fx-background-color: rgba(255,255,255,0.92); -fx-text-fill: #E53935;";
            case EN_COURS:
                return base + " -fx-background-color: rgba(255,255,255,0.92); -fx-text-fill: #1565C0;";
            case EN_ATTENTE:
            default:
                return base + " -fx-background-color: rgba(255,255,255,0.92); -fx-text-fill: #1565C0;";
        }
    }

    /**
     * Couleurs des headers par statut — BLEU pour EN_ATTENTE au lieu d'orange.
     */
    private String getStatutHeaderColor(Reservation r) {
        if (r.getStatut() == null)
            return "linear-gradient(to right, #1E88E5, #1565C0)";
        switch (r.getStatut()) {
            case ACCEPTEE:
                return "linear-gradient(to right, #43A047, #2E7D32)";
            case REFUSEE:
                return "linear-gradient(to right, #E53935, #C62828)";
            case EN_COURS:
                return "linear-gradient(to right, #7B1FA2, #6A1B9A)";
            case TERMINEE:
                return "linear-gradient(to right, #546E7A, #37474F)";
            case ANNULEE:
                return "linear-gradient(to right, #8D6E63, #5D4037)";
            case EN_ATTENTE:
            default:
                return "linear-gradient(to right, #1E88E5, #1565C0)";
        }
    }

    // Actions

    private void supprimerReservation(Reservation reservation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Fasa5");
        confirmation.setHeaderText("Met2aked t7eb tfasa5 hal réservation ?");
        confirmation.setContentText("Annonce : "
                + (reservation.getAnnonce() != null ? reservation.getAnnonce().getTitre() : "N/A")
                + "\nRod balek, l'action hedhi irréversible.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    reservationService.supprimer(reservation);
                    System.out.println("Réservation supprimée : ID " + reservation.getId());
                    chargerReservations();
                } catch (SQLException e) {
                    Alert erreur = new Alert(Alert.AlertType.ERROR);
                    erreur.setTitle("Erreur");
                    erreur.setContentText("Impossible de supprimer : " + e.getMessage());
                    erreur.showAndWait();
                }
            }
        });
    }

    /**
     * Génère un contrat PDF avec UUID de signature numérique.
     */
    private void genererContratPDF(Reservation reservation) {
        try {
            PDFService pdfService = new PDFService();
            java.io.File fichierPdf = pdfService.genererContrat(reservation);

            String uuid = UUID.randomUUID().toString().toUpperCase();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Contrat PDF Généré");
            alert.setHeaderText("Contrat créé et signé numériquement !");
            alert.setContentText(
                    "Réservation #" + reservation.getId() + "\n"
                            + "Fichier : " + fichierPdf.getAbsolutePath() + "\n\n"
                            + "Signature numérique UUID : " + uuid);
            alert.showAndWait();

            // Ouvrir le PDF sur Windows
            String cheminPdf = fichierPdf.getAbsolutePath();
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start", "", cheminPdf });
            }
        } catch (Exception e) {
            Alert erreur = new Alert(Alert.AlertType.ERROR);
            erreur.setTitle("Erreur");
            erreur.setContentText("Impossible de générer le contrat : " + e.getMessage());
            erreur.showAndWait();
        }
    }

    // Navigation

    @FXML
    private void retourMarketplace() {
        naviguerVers("/Marketplace.fxml");
    }

    private void naviguerVers(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            StackPane contentArea = (StackPane) cardsContainer.getScene().lookup("#contentArea");
            if (contentArea == null) {
                contentArea = (StackPane) cardsContainer.getScene().getRoot().lookup("#contentArea");
            }
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (IOException e) {
            System.err.println("Erreur navigation vers : " + fxmlPath);
            e.printStackTrace();
        }
    }
}
