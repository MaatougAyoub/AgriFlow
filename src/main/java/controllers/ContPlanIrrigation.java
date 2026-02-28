package controllers;

import entities.Culture;
import services.ServiceCulture;
import services.ServicePlanIrrigation;
import javafx.event.ActionEvent;          // ✅ CORRECT (javafx)
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

import java.io.IOException;
import java.util.List;

public class ContPlanIrrigation {

    @FXML
    private VBox plansContainer;

    private final ServiceCulture serviceCulture = new ServiceCulture();
    private final ServicePlanIrrigation servicePlan = new ServicePlanIrrigation();

    // ───── Navigation vers Dashboard ─────
    @FXML
    private void goToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            ((Node) event.getSource()).getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ───── Déjà sur cette page ─────
    @FXML
    private void goToIrrigationPlan(ActionEvent event) {   // ✅ javafx.event.ActionEvent
        // Déjà sur Irrigation Plan
    }

    @FXML
    public void initialize() {
        chargerListeCultures();
    }

    private void chargerListeCultures() {
        try {
            List<Culture> cultures = serviceCulture.recuperer();
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
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-border-color: #E6E6E6; -fx-border-radius: 10; -fx-cursor: hand;");

        card.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> ouvrirDetailsCulture(card, c));

        Region bar = new Region();
        bar.setPrefWidth(6);
        bar.setMinWidth(6);
        bar.setMaxWidth(6);
        bar.setStyle("-fx-background-color: #2ba3c4; -fx-background-radius: 10;");

        VBox left = new VBox(6);
        Label nomCulture = new Label(c.getNom());
        nomCulture.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");

        Label parcelle = new Label("Parcelle: " + c.getParcelleId());
        parcelle.setStyle("-fx-font-size: 11; -fx-text-fill: #6B7280;");
        left.getChildren().addAll(nomCulture, parcelle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label quantite = new Label(c.getQuantiteEau() + " mm");
        quantite.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #2ba3c4;");

        card.getChildren().addAll(bar, left, spacer, quantite);
        return card;
    }

    private void ouvrirDetailsCulture(HBox card, Culture culture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CultureDetails.fxml"));
            Parent root = loader.load();

            ContCultureDetails controller = loader.getController();
            controller.setCulture(culture);

            int idCulture = culture.getId();
            int planId = servicePlan.getLastPlanIdByCulture(idCulture);

            if (planId == 0) {
                planId = servicePlan.createDraftPlanAndReturnId(idCulture, (float) culture.getQuantiteEau());
            }

            controller.setPlanId(planId);

            Scene scene = card.getScene();
            scene.setRoot(root);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}