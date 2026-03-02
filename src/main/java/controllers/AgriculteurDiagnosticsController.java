package controllers;

import entities.Diagnostic;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import mains.MainIrrigationFX;
import services.DiagnosticService;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AgriculteurDiagnosticsController {

    @FXML private VBox listContainer;
    private final DiagnosticService service = new DiagnosticService();

    @FXML
    public void initialize() {
        refreshList();
    }

    private void refreshList() {
        listContainer.getChildren().clear();

        // ✅ Récupérer l'ID depuis MainController (déjà configuré par SignIn.java)
        User currentUser = MainController.getCurrentUser();

        if (currentUser == null) {
            System.err.println("❌ MainController.getCurrentUser() retourne null !");
            Label erreur = new Label("⚠️ Veuillez vous reconnecter.");
            erreur.setStyle("-fx-text-fill: #E74C3C; -fx-font-size: 16; -fx-padding: 30;");
            listContainer.getChildren().add(erreur);
            return;
        }

        int idAgriculteur = currentUser.getId();
        System.out.println("══════════════════════════════════════════");
        System.out.println("👤 Utilisateur connecté : " + currentUser.getPrenom() + " " + currentUser.getNom());
        System.out.println("👤 ID = " + idAgriculteur);
        System.out.println("👤 Email = " + currentUser.getEmail());
        System.out.println("══════════════════════════════════════════");

        if (idAgriculteur <= 0) {
            Label erreur = new Label("⚠️ ID utilisateur invalide. Reconnectez-vous.");
            erreur.setStyle("-fx-text-fill: #E74C3C; -fx-font-size: 16; -fx-padding: 30;");
            listContainer.getChildren().add(erreur);
            return;
        }

        List<Diagnostic> diagnostics = service.recupererParAgriculteur(idAgriculteur);
        System.out.println("📋 Diagnostics trouvés : " + diagnostics.size());

        if (diagnostics.isEmpty()) {
            Label vide = new Label("Aucun diagnostic trouvé.");
            vide.setStyle("-fx-text-fill: #999; -fx-font-size: 14; -fx-padding: 30;");
            listContainer.getChildren().add(vide);
            return;
        }

        for (Diagnostic d : diagnostics) {
            final String styleNormal = "-fx-padding: 14 24; -fx-border-color: #F0F0F0; -fx-border-width: 0 0 1 0; -fx-background-color: white;";
            final String styleHover  = "-fx-padding: 14 24; -fx-border-color: #F0F0F0; -fx-border-width: 0 0 1 0; -fx-background-color: #F7FFF7;";

            HBox row = new HBox(0);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle(styleNormal);

            // 1. Image arrondie
            ImageView iv = new ImageView();
            iv.setFitHeight(46);
            iv.setFitWidth(46);
            iv.setPreserveRatio(true);
            Rectangle clip = new Rectangle(46, 46);
            clip.setArcWidth(12);
            clip.setArcHeight(12);
            iv.setClip(clip);

            if (d.getImagePath() != null) {
                File file = new File(d.getImagePath());
                if (file.exists()) iv.setImage(new Image(file.toURI().toString()));
            }

            HBox imgBox = new HBox(iv);
            imgBox.setAlignment(Pos.CENTER_LEFT);
            imgBox.setPrefWidth(70);

            // 2. Nom Culture
            Label lblCulture = new Label(d.getNomCulture() != null ? d.getNomCulture() : "—");
            lblCulture.setPrefWidth(180);
            lblCulture.setWrapText(false);
            lblCulture.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #2D5016;");

            // 3. Description (truncated visually via label width)
            String desc = d.getDescription() != null ? d.getDescription() : "Aucune description";
            Label lblDesc = new Label(desc);
            lblDesc.setPrefWidth(380);
            lblDesc.setWrapText(false);
            lblDesc.setStyle("-fx-font-size: 12; -fx-text-fill: #6B7280;");

            // 4. Statut badge
            String statut = d.getStatut() != null ? d.getStatut() : "—";
            Label lblStatut = new Label(statut.toUpperCase());
            lblStatut.setPrefWidth(140);
            lblStatut.setAlignment(Pos.CENTER);
            if ("En attente".equalsIgnoreCase(statut)) {
                lblStatut.setStyle("-fx-padding: 5 14; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 10; -fx-background-color: #FFF3E0; -fx-text-fill: #E65100;");
            } else if ("Traité".equalsIgnoreCase(statut) || "Traite".equalsIgnoreCase(statut)) {
                lblStatut.setStyle("-fx-padding: 5 14; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 10; -fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32;");
            } else {
                lblStatut.setStyle("-fx-padding: 5 14; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 10; -fx-background-color: #E3F2FD; -fx-text-fill: #1565C0;");
            }

            // 5. Date
            String dateStr = d.getDateEnvoi() != null
                    ? d.getDateEnvoi().toLocalDate().toString()
                    : "—";
            Label lblDate = new Label(dateStr);
            lblDate.setPrefWidth(100);
            lblDate.setAlignment(Pos.CENTER);
            lblDate.setStyle("-fx-font-size: 11; -fx-text-fill: #9EA3AE;");

            row.getChildren().addAll(imgBox, lblCulture, lblDesc, lblStatut, lblDate);
            row.setOnMouseEntered(e -> row.setStyle(styleHover));
            row.setOnMouseExited(e -> row.setStyle(styleNormal));
            listContainer.getChildren().add(row);
        }
    }

    // Navigation
    private void navigateTo(ActionEvent event, String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void goToIrrigation(ActionEvent event) {
        MainIrrigationFX.showExploreIrrigations();
    }

    @FXML private void goToDashboard(ActionEvent event) {
        navigateTo(event, "/Dashboard.fxml");
    }

    @FXML private void goToAjouterReclamation(ActionEvent event) {
        navigateTo(event, "/Diagnostic.fxml");
    }
}