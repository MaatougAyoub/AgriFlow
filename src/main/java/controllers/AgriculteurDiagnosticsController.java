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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
            HBox row = new HBox(0);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 15 20; -fx-border-color: #F8F8F8; -fx-border-width: 0 0 1 0; -fx-background-color: white;");

            // 1. Image arrondie
            ImageView iv = new ImageView();
            iv.setFitHeight(45);
            iv.setFitWidth(45);
            Rectangle clip = new Rectangle(45, 45);
            clip.setArcWidth(15);
            clip.setArcHeight(15);
            iv.setClip(clip);

            if (d.getImagePath() != null) {
                File file = new File(d.getImagePath());
                if (file.exists()) iv.setImage(new Image(file.toURI().toString()));
            }

            VBox imgBox = new VBox(iv);
            imgBox.setPrefWidth(80);

            // 2. Nom Culture
            Label lblCulture = new Label(d.getNomCulture());
            lblCulture.setPrefWidth(200);
            lblCulture.setFont(Font.font("System", FontWeight.BOLD, 14));
            lblCulture.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #6B7280;");

            // 3. Description
            Label lblDesc = new Label(d.getDescription() != null ? d.getDescription() : "Aucune description");
            lblDesc.setPrefWidth(300);
            lblDesc.setTextFill(Color.web("#666666"));
            lblDesc.setStyle("-fx-font-size: 13px;");

            // 4. Statut
            Label lblStatut = new Label(d.getStatut().toUpperCase());
            lblStatut.setPrefWidth(120);
            lblStatut.setAlignment(Pos.CENTER);
            lblStatut.setStyle("-fx-padding: 5 10; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 10px;");

            if ("En attente".equalsIgnoreCase(d.getStatut())) {
                lblStatut.setStyle(lblStatut.getStyle() + "-fx-background-color: #FFF4E5; -fx-text-fill: #FF9800;");
            } else {
                lblStatut.setStyle(lblStatut.getStyle() + "-fx-background-color: #E8F5E9; -fx-text-fill: #4CAF50;");
            }

            row.getChildren().addAll(imgBox, lblCulture, lblDesc, lblStatut);
            row.setOnMouseEntered(e -> row.setStyle(row.getStyle() + "-fx-background-color: #FDFDFD;"));
            row.setOnMouseExited(e -> row.setStyle(row.getStyle() + "-fx-background-color: white;"));
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