package controllers;

import entities.Culture;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import mains.MainIrrigationFX;
import services.ServiceCulture;
import services.ServicePlanIrrigation;

import java.io.IOException;
import javafx.stage.Stage;
import java.util.List;

public class AgriContPlanIrrigation {
    @FXML private BorderPane rootPane;
    @FXML private VBox plansContainer;
    @FXML private Button btnIrrigationPlan;
    @FXML private Button btnDiagnostic;

    private Node originalCenter;

    private final ServiceCulture serviceCulture = new ServiceCulture();
    private final ServicePlanIrrigation servicePlan = new ServicePlanIrrigation();

    @FXML
    public void initialize() {
        // cache the original center (the ScrollPane + plansContainer)
        originalCenter = null;
        if (rootPane != null) originalCenter = rootPane.getCenter();
        chargerListeCultures();
        activateTopButton(btnIrrigationPlan);
    }

    private void chargerListeCultures() {
        try {
            List<Culture> cultures = serviceCulture.recupererCultures();
            if (plansContainer != null) {
                plansContainer.getChildren().clear();
                for (Culture c : cultures) {
                    plansContainer.getChildren().add(creerCarte(c));
                }
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
        Label parcelle = new Label("📍 Parcelle : " + c.getParcelleId());
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
            card.getScene().setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // --- Navigation ---

    @FXML void goToDashboard(ActionEvent e) { navigateTo(e, "/Dashboard.fxml"); }
    @FXML void goToHome(ActionEvent e) { navigateTo(e, "/Home.fxml"); } // Ajustez le chemin selon votre projet

    private void navigateTo(ActionEvent event, String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void goToIrrigationPlan(ActionEvent e) {
        // restore original center (the plans list)
        if (rootPane != null && originalCenter != null) {
            rootPane.setCenter(originalCenter);
        }
        chargerListeCultures();
        activateTopButton(btnIrrigationPlan);
    } // Rafraîchit la vue actuelle

    @FXML
    void goToDiagnostic(ActionEvent e) {
        try {
            Parent diag = FXMLLoader.load(getClass().getResource("/AgriculteurDiagnostics.fxml"));
            if (rootPane != null) {
                rootPane.setCenter(diag);
            }
            activateTopButton(btnDiagnostic);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void activateTopButton(Button active) {
        try {
            String activeStyle = "-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; -fx-padding: 6 12; -fx-font-weight: bold;";
            String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #e0e0e0; -fx-padding: 6 12;";
            if (btnIrrigationPlan != null) btnIrrigationPlan.setStyle(btnIrrigationPlan == active ? activeStyle : inactiveStyle);
            if (btnDiagnostic != null) btnDiagnostic.setStyle(btnDiagnostic == active ? activeStyle : inactiveStyle);
        } catch (Exception ignored) {
        }
    }
}