package controllers;

import entities.Culture;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ContDashboard implements Initializable {

    @FXML
    private Label lblNbParcelles;
    @FXML
    private Label lblNbCultures;
    @FXML
    private Label lblTotalEau;
    @FXML
    private BarChart<String, Number> barChartEau;
    @FXML
    private PieChart pieChartCultures;


    private static final String DB_URL = "jdbc:mysql://localhost:3306/agriflow";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chargerNombreParcelles();
        chargerNombreCultures();
        chargerTotalEauSemaine();
        chargerBarChart();
    }

    @FXML
    private void goToHome(ActionEvent event) {
        navigateTo(event, "/ExpertHome.fxml");
    }
    @FXML
    private void goToIrrigationPlan(ActionEvent event) {
        navigateTo(event, "/ExperpalnIrrigation.fxml");
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
    }
    @FXML
    public void goToDiagnostic(ActionEvent event) { navigateTo(event, "/ExpertDashboard.fxml");}
    @FXML
    private void goToAjouterProduit(ActionEvent event) {
        navigateTo(event, "/listeProduits.fxml");
    }


    // ───── Méthode utilitaire de navigation ─────
    private void navigateTo(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de chargement : " + fxmlPath);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private void chargerNombreParcelles() {
        String sql = "SELECT COUNT(DISTINCT parcelle_id ) AS total FROM cultures";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) lblNbParcelles.setText(String.valueOf(rs.getInt("total")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerNombreCultures() {
        String sql = "SELECT COUNT(*) AS total FROM cultures";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) lblNbCultures.setText(String.valueOf(rs.getInt("total")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerTotalEauSemaine() {
        // On récupère les données nécessaires au calcul dynamique
        String sql = "SELECT c.type_culture, p.superficie " +
                "FROM cultures c " +
                "JOIN parcelle p ON c.parcelle_id = p.id";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            float totalJournalier = 0;

            while (rs.next()) {
                // Création d'un objet temporaire pour utiliser votre logique switch
                String typeStr = rs.getString("type_culture");
                float superficie = rs.getFloat("superficie");

                try {
                    Culture.TypeCulture typeEnum = Culture.TypeCulture.valueOf(typeStr.toUpperCase());
                    // On crée une instance pour accéder à calculerBesoinEau()
                    Culture temp = new Culture(0, "", 0, superficie, typeEnum);
                    totalJournalier += temp.calculerBesoinEau();
                } catch (IllegalArgumentException e) {
                    System.err.println("Type culture inconnu : " + typeStr);
                }
            }

            // Calcul pour la semaine (x7)
            float eauSemaine = totalJournalier * 7;
            lblTotalEau.setText(String.format("%.1f L", eauSemaine));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerBarChart() {
        // JOIN pour avoir la superficie et le type pour chaque culture
        String sql = "SELECT c.nom, c.type_culture, p.superficie " +
                "FROM cultures c " +
                "JOIN parcelle p ON c.parcelle_id = p.id";

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Besoin en eau (mm/jour)");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String nom = rs.getString("nom");
                String typeStr = rs.getString("type_culture");
                float superficie = rs.getFloat("superficie");

                try {
                    Culture.TypeCulture typeEnum = Culture.TypeCulture.valueOf(typeStr.toUpperCase());
                    Culture temp = new Culture(0, nom, 0, superficie, typeEnum);

                    // On ajoute le résultat du calcul au graphique
                    series.getData().add(new XYChart.Data<>(nom, temp.calculerBesoinEau()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        barChartEau.getData().clear();
        barChartEau.getData().add(series);

        // Style des barres (exécuté après l'ajout à la scène pour que le Node existe)
        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-bar-fill: #2196F3; -fx-background-radius: 5 5 0 0;");
            }
        }
    }
}