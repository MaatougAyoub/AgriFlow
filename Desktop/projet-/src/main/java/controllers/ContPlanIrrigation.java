package controllers;

import entities.Culture;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import services.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ContPlanIrrigation {

    @FXML private VBox plansContainer;

    private final ServiceCulture serviceCulture = new ServiceCulture();
    private final ServicePlanIrrigation servicePlan = new ServicePlanIrrigation();
    private final IrrigationSmartService smartService = new IrrigationSmartService();
    private final ServicePlanIrrigationJour serviceJour = new ServicePlanIrrigationJour();

    @FXML
    public void initialize() {
        chargerListeCultures();
    }

    private void chargerListeCultures() {
        try {
            List<Culture> cultures = serviceCulture.recupererCultures();
            plansContainer.getChildren().clear();
            for (Culture c : cultures) {
                plansContainer.getChildren().add(creerCarte(c));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private HBox creerCarte(Culture c) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.getStyleClass().add("culture-card"); // Ajoute du CSS si tu en as
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); -fx-cursor: hand;");

        VBox info = new VBox(5);
        Label name = new Label(c.getNomCulture().toUpperCase());
        name.setStyle("-fx-font-weight: bold; -fx-text-fill: #2D5A27; -fx-font-size: 14;");
        Label detail = new Label("Besoin : " + c.getQuantiteEau() + " mm/semaine");
        info.getChildren().addAll(name, detail);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnIA = new Button("🤖 Optimiser IA");
        btnIA.setStyle("-fx-background-color: #E8F5E9; -fx-text-fill: #2D5A27; -fx-font-weight: bold;");
        btnIA.setOnAction(e -> genererPlanIA(c));

        card.getChildren().addAll(info, spacer, btnIA);
        card.setOnMouseClicked(e -> ouvrirDetailsCulture(card, c));
        return card;
    }

    private void genererPlanIA(Culture c) {
        try {
            // 1. Créer le plan en BDD s'il n'existe pas
            int planId = servicePlan.createDraftPlanAndReturnId(c.getIdCulture(), (float) c.getQuantiteEau());

            // 2. Appeler l'intelligence Open-Meteo
            Map<String, float[]> data = smartService.genererPlanIA(c);

            // 3. Sauvegarder les jours
            for (Map.Entry<String, float[]> entry : data.entrySet()) {
                serviceJour.saveDay(planId, entry.getKey(), entry.getValue()[0], (int)entry.getValue()[1], entry.getValue()[2]);
            }

            showAlert("IA Planification", "Le plan pour " + c.getNomCulture() + " a été optimisé avec la météo réelle !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Connexion météo impossible.");
        }
    }

    // --- Navigation ---
    private void navigateTo(ActionEvent event, String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void goToHome(ActionEvent e) { navigateTo(e, "/ExpertHome.fxml"); }
    @FXML void goToDashboard(ActionEvent e) { navigateTo(e, "/Dashboard.fxml"); }
    @FXML void goToAjouterProduit(ActionEvent e) { navigateTo(e, "/listeProduits.fxml"); }
    @FXML void goToDiagnostic(ActionEvent e) { navigateTo(e, "/ExpertDashboard.fxml"); }
    @FXML void goToIrrigationPlan(ActionEvent e) { navigateTo(e, "/IrrigationPlan.fxml"); }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    // Dans ta méthode ouvrirDetailsCulture :
    private void ouvrirDetailsCulture(HBox card, Culture culture) {
        try {
            // Assure-toi que le nom du fichier est EXACTEMENT CultureDetails.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CultureDetails.fxml"));
            Parent root = loader.load();

            ContCultureDetails controller = loader.getController();
            controller.setCulture(culture);

            int idCulture = culture.getIdCulture();
            int planId = servicePlan.getLastPlanIdByCulture(idCulture);



            controller.setPlanId(planId);

            Scene scene = card.getScene();
            scene.setRoot(root);

        } catch (Exception ex) {
            ex.printStackTrace(); // L'erreur s'affichera ici si le FXML a un problème
        }
    }
}
