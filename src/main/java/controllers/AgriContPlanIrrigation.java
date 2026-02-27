package controllers;

import entities.Culture;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ServiceCulture;
import services.ServicePlanIrrigation;

import java.io.IOException;
import java.util.List;

public class AgriContPlanIrrigation {
    @FXML private VBox plansContainer;

    private final ServiceCulture serviceCulture = new ServiceCulture();
    private final ServicePlanIrrigation servicePlan = new ServicePlanIrrigation();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox creerCarte(Culture c) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); -fx-cursor: hand;");

        card.setOnMouseClicked(e -> ouvrirDetailsCulture(card, c));

        Region bar = new Region();
        bar.setPrefWidth(6);
        bar.setStyle("-fx-background-color: #2ba3c4; -fx-background-radius: 5;");

        VBox infoBox = new VBox(5);
        Label nom = new Label(c.getNom());
        nom.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label parcelle = new Label("📍 Parcelle : " + c.getIdParcelle());
        parcelle.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
        infoBox.getChildren().addAll(nom, parcelle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label eau = new Label(String.format("%.1f mm", c.calculerBesoinEau()));
        eau.setStyle("-fx-font-weight: 800; -fx-text-fill: #2ba3c4; -fx-font-size: 15px;");

        card.getChildren().addAll(bar, infoBox, spacer, eau);
        return card;
    }

    private void ouvrirDetailsCulture(HBox card, Culture culture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CultureDetailsAgri.fxml"));
            Parent root = loader.load();

            // CORRECTION ICI : Utilisation du type exact défini dans le FXML
            ContCultureDetailsAgri controller = loader.getController();

            // Passer les données au contrôleur
            controller.setCulture(culture);
            int planId = servicePlan.getLastPlanIdByCulture(culture.getId());
            controller.setPlanId(planId);
            controller.setReadOnlyMode(true);

            // Changement de scène
            Scene scene = card.getScene();
            scene.setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // --- Navigation ---

    private void navigateTo(ActionEvent event, String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML void goToDashboard(ActionEvent e) { navigateTo(e, "/Dashboard.fxml"); }
    @FXML void goToDiagnostic(ActionEvent e) { navigateTo(e, "/AgriculteurDiagnostics.fxml"); }
    @FXML void goToHome(ActionEvent e) { navigateTo(e, "/Home.fxml"); } // Ajustez le chemin selon votre projet
    @FXML void goToIrrigationPlan(ActionEvent e) { chargerListeCultures(); } // Rafraîchit la vue actuelle
}