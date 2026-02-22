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
import javafx.scene.input.MouseEvent;
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
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); -fx-cursor: hand;");

        card.setOnMouseClicked(e -> ouvrirDetailsCulture(card, c));

        // Barre d'état (Bleue pour l'agriculteur)
        Region bar = new Region();
        bar.setPrefWidth(6);
        bar.setStyle("-fx-background-color: #2ba3c4; -fx-background-radius: 5;");

        VBox infoBox = new VBox(5);
        Label nom = new Label(c.getNomCulture());
        nom.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label parcelle = new Label("📍 Parcelle : " + c.getIdParcelle());
        parcelle.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
        infoBox.getChildren().addAll(nom, parcelle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label eau = new Label(c.getQuantiteEau() + " mm");
        eau.setStyle("-fx-font-weight: 800; -fx-text-fill: #2ba3c4; -fx-font-size: 15px;");

        card.getChildren().addAll(bar, infoBox, spacer, eau);
        return card;
    }

    private void ouvrirDetailsCulture(HBox card, Culture culture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CultureDetails.fxml"));
            Parent root = loader.load();

            ContCultureDetails controller = loader.getController();

            // 1. Passer la culture
            controller.setCulture(culture);

            // 2. Chercher le plan existant (sans en créer un nouveau)
            int planId = servicePlan.getLastPlanIdByCulture(culture.getIdCulture());
            controller.setPlanId(planId);

            // 3. ACTIVER LE MODE LECTURE SEULE
            controller.setReadOnlyMode(true);

            Scene scene = card.getScene();
            scene.setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Méthodes de navigation génériques
    private void navigateTo(ActionEvent event, String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            ((Stage)((Node)event.getSource()).getScene().getWindow()).getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML void goToDashboard(ActionEvent e) { navigateTo(e, "/Dashboard.fxml"); }
    @FXML void goToDiagnostic(ActionEvent e) { navigateTo(e, "/AgriculteurDiagnostics.fxml"); }

    public void goToHome(ActionEvent actionEvent) {
    }

    public void goToIrrigationPlan(ActionEvent actionEvent) {
    }
}