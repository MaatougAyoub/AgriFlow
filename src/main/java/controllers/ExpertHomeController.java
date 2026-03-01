package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import org.json.JSONObject;
import services.ExpertService;
import services.ServiceParcelle;
import services.WeatherService;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ExpertHomeController {

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static ExpertHomeController instance;

    /** Called by sub-page controllers to navigate without reloading Main.fxml */
    public static ExpertHomeController getInstance() {
        return instance;
    }

    // ── Root pane injected by FXML ─────────────────────────────────────────
    @FXML
    private BorderPane rootPane;

    /** Saved dashboard center so "goToHome" can restore it without reloading */
    private Node homeCenterNode;

    @FXML
    private TextField latField;
    @FXML
    private TextField lonField;
    @FXML
    private Label tempLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label weatherIconLabel;
    @FXML
    private Label humidityLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label dateLabel;

    // Nouveaux labels pour les statistiques
    @FXML
    private Label lblNbParcelles;
    @FXML
    private Label lblNbCultures;
    @FXML
    private Label badgeDiagnostic;

    // Top bar buttons (to manage active state)
    @FXML
    private Button btnHome;
    @FXML
    private Button btnIrrigation;
    @FXML
    private Button btnAnalytics;
    @FXML
    private Button btnProducts;
    @FXML
    private Button btnDiagnostics;

    private final WeatherService weatherService = new WeatherService();
    private final ServiceParcelle serviceParcelle = new ServiceParcelle();
    private final ExpertService expertService = new ExpertService();
    private final int idParcelleActuelle = 1;

    // Configuration de la base de données
    // private static final String DB_URL = "jdbc:mysql://localhost:3306/agriflow";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/agriflow8";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    @FXML
    public void initialize() {
        instance = this; // register singleton so sub-pages can navigate back

        // 1. Affichage de la Date et de l'Heure
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM")));
        timeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        chargerBadgeDiagnostics();

        // 2. Chargement des statistiques depuis la BDD
        chargerStatistiques();

        // set initial active button
        setActiveButton(btnHome);

        // 3. Chargement de la position et météo
        try {
            String loc = serviceParcelle.recupererLocalisation(idParcelleActuelle);
            if (loc != null && loc.contains(",")) {
                String[] parts = loc.split(",");
                latField.setText(parts[0]);
                lonField.setText(parts[1]);
                actualiserMeteoCartes(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération localisation : " + e.getMessage());
        }

        // 4. Save the dashboard center so we can restore it when "Home" is clicked
        homeCenterNode = rootPane.getCenter();
    }

    private void setActiveButton(Button active) {
        // reset styles
        Button[] buttons = new Button[] { btnHome, btnIrrigation, btnAnalytics, btnProducts, btnDiagnostics };
        for (Button b : buttons) {
            if (b == null)
                continue;
            // default inactive style
            b.setStyle("-fx-background-color: transparent; -fx-text-fill: #e0e0e0; -fx-padding: 6 12;");
        }
        if (active != null) {
            // apply active style
            active.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-padding: 6 12; -fx-font-weight: bold;");
        }
    }

    private void chargerStatistiques() {
        String sqlParcelles = "SELECT COUNT(DISTINCT parcelle_id ) AS total FROM cultures";
        String sqlCultures = "SELECT COUNT(*) AS total FROM cultures";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Nombre de Parcelles
            PreparedStatement ps1 = conn.prepareStatement(sqlParcelles);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next())
                lblNbParcelles.setText(String.valueOf(rs1.getInt("total")));

            // Nombre de Cultures
            PreparedStatement ps2 = conn.prepareStatement(sqlCultures);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next())
                lblNbCultures.setText(String.valueOf(rs2.getInt("total")));

        } catch (SQLException e) {
            System.err.println("Erreur chargement stats : " + e.getMessage());
            lblNbParcelles.setText("!");
            lblNbCultures.setText("!");
        }
    }

    private void actualiserMeteoCartes(double lat, double lon) {
        try {
            JSONObject forecast = weatherService.getForecast(lat, lon);
            double temp = forecast.getJSONObject("daily").getJSONArray("temperature_2m_max").getDouble(0);
            double hum = forecast.getJSONObject("daily").getJSONArray("relative_humidity_2m_max").getDouble(0);

            tempLabel.setText(String.format("%.0f°C", temp));
            if (humidityLabel != null)
                humidityLabel.setText(String.format("%.0f%%", hum));
            weatherIconLabel.setText(temp > 22 ? "☀️" : "⛅");

        } catch (Exception e) {
            System.err.println("Erreur météo : " + e.getMessage());
        }
    }

    @FXML
    private void saveLocation(ActionEvent event) {
        String latRaw = latField.getText().trim().replace(",", ".");
        String lonRaw = lonField.getText().trim().replace(",", ".");

        if (latRaw.isEmpty() || lonRaw.isEmpty()) {
            showAlert("Erreur", "Veuillez saisir les coordonnées.");
            return;
        }

        if (serviceParcelle.modifierLocalisation(idParcelleActuelle, latRaw + "," + lonRaw)) {
            actualiserMeteoCartes(Double.parseDouble(latRaw), Double.parseDouble(lonRaw));
            showAlert("Succès", "Position mise à jour !");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void chargerBadgeDiagnostics() {
        int nbAttente = expertService.getNombreDiagnosticsEnAttente();

        if (nbAttente > 0) {
            badgeDiagnostic.setText(String.valueOf(nbAttente));
            badgeDiagnostic.setVisible(true);
        } else {
            badgeDiagnostic.setVisible(false);
        }
    }

    // --- NAVIGATION ────────────────────────────────────────────────────────
    /**
     * Loads an FXML and sets it as the center of ExpertHome's BorderPane.
     * The top bar stays persistent. Pass null for fxmlPath to restore home dashboard.
     */
    public void loadCenterPage(String fxmlPath) {
        if (fxmlPath == null) {
            rootPane.setCenter(homeCenterNode);
            return;
        }
        try {
            Parent loaded = FXMLLoader.load(getClass().getResource(fxmlPath));
            rootPane.setCenter(loaded);
        } catch (IOException e) {
            System.err.println("[NAV] ERREUR chargement " + fxmlPath + " : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[NAV] ERREUR inattendue pour " + fxmlPath + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void goToHome(ActionEvent event) {
        setActiveButton(btnHome);
        loadCenterPage(null); // restore saved dashboard content
    }

    @FXML
    public void goToIrrigationPlan(ActionEvent event) {
        setActiveButton(btnIrrigation);
        loadCenterPage("/ExperpalnIrrigation.fxml");
    }

    @FXML
    public void goToDashboard(ActionEvent event) {
        setActiveButton(btnAnalytics);
        loadCenterPage("/dashboard.fxml");
    }

    @FXML
    public void goToAjouterProduit(ActionEvent event) {
        setActiveButton(btnProducts);
        loadCenterPage("/listeProduits.fxml");
    }

    @FXML
    public void goToReclamations(ActionEvent event) {
        setActiveButton(btnDiagnostics);
        loadCenterPage("/ExpertDashboard.fxml");
    }
}