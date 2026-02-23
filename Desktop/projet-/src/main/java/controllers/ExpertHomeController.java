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
import org.json.JSONObject;
import services.ServiceParcelle;
import services.WeatherService;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ExpertHomeController {

    @FXML private TextField latField;
    @FXML private TextField lonField;
    @FXML private Label tempLabel;
    @FXML private Label cityLabel;
    @FXML private Label weatherIconLabel;
    @FXML private Label humidityLabel;
    @FXML private Label timeLabel;
    @FXML private Label dateLabel;

    // Nouveaux labels pour les statistiques
    @FXML private Label lblNbParcelles;
    @FXML private Label lblNbCultures;

    private final WeatherService weatherService = new WeatherService();
    private final ServiceParcelle serviceParcelle = new ServiceParcelle();
    private final int idParcelleActuelle = 1;

    // Configuration de la base de données
    private static final String DB_URL = "jdbc:mysql://localhost:3306/AgriFlow";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    @FXML
    public void initialize() throws SQLException {
        // 1. Affichage de la Date et de l'Heure
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM")));
        timeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        // 2. Chargement des statistiques depuis la BDD
        chargerStatistiques();

        // 3. Chargement de la position et météo
        String loc = serviceParcelle.recupererLocalisation(idParcelleActuelle);
        if (loc != null && loc.contains(",")) {
            String[] parts = loc.split(",");
            latField.setText(parts[0]);
            lonField.setText(parts[1]);
            actualiserMeteoCartes(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
        }
    }

    private void chargerStatistiques() {
        String sqlParcelles = "SELECT COUNT(DISTINCT id_parcelle) AS total FROM culture";
        String sqlCultures = "SELECT COUNT(*) AS total FROM culture";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Nombre de Parcelles
            PreparedStatement ps1 = conn.prepareStatement(sqlParcelles);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) lblNbParcelles.setText(String.valueOf(rs1.getInt("total")));

            // Nombre de Cultures
            PreparedStatement ps2 = conn.prepareStatement(sqlCultures);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) lblNbCultures.setText(String.valueOf(rs2.getInt("total")));

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
            if (humidityLabel != null) humidityLabel.setText(String.format("%.0f%%", hum));
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

    // --- NAVIGATION ---
    private void navigateTo(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML public void goToHome(ActionEvent event) { navigateTo(event, "/ExpertHome.fxml"); }
    @FXML public void goToIrrigationPlan(ActionEvent event) { navigateTo(event, "/ExperpalnIrrigation.fxml"); }
    @FXML public void goToDashboard(ActionEvent event) { navigateTo(event, "/Dashboard.fxml"); }
    @FXML public void goToAjouterProduit(ActionEvent event) { navigateTo(event, "/ListeProduits.fxml"); }
    @FXML public void goToReclamations(ActionEvent event) { navigateTo(event, "/ExpertDashboard.fxml"); }
}