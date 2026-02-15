package controllers;

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


    private static final String DB_URL = "jdbc:mysql://localhost:3306/AgriFlow";
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
    private void goToIrrigationPlan(ActionEvent event) {
        navigateTo(event, "/PalnIrrigation.fxml");
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
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
        String sql = "SELECT COUNT(DISTINCT id_parcelle) AS total FROM culture";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) lblNbParcelles.setText(String.valueOf(rs.getInt("total")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerNombreCultures() {
        String sql = "SELECT COUNT(*) AS total FROM culture";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) lblNbCultures.setText(String.valueOf(rs.getInt("total")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerTotalEauSemaine() {
        String sql = "SELECT SUM(quantite_eau) AS total_eau FROM culture";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int eauSemaine = rs.getInt("total_eau") * 7;
                lblTotalEau.setText(eauSemaine + " L");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerBarChart() {
        String sql = "SELECT `nom-culture`, quantite_eau FROM culture";
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Quantité d'eau (L)");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString("nom-culture"), rs.getInt("quantite_eau")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        barChartEau.getData().clear();
        barChartEau.getData().add(series);
        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-bar-fill: #2196F3;");

            }


        }
    }
}