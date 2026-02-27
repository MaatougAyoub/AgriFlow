package controllers;

import entities.CollabRequest;
import entities.DailyForecast;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mains.MainCollabFX;
import mains.MainFX;
import services.CollabRequestService;
import services.WeatherService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CollabRequestDetailsController {

    // ✅ Déclaration des éléments FXML
    @FXML private Text titleText;
    @FXML private Text locationText;
    @FXML private Text dateRangeText;
    @FXML private Text neededPeopleText;
    @FXML private Text salaryText;
    @FXML private Text publisherText;
    @FXML private TextArea descriptionArea;
    @FXML private Button applyButton;
    @FXML private VBox weatherContainer;

    private CollabRequest currentRequest;
    private final CollabRequestService requestService = new CollabRequestService();
    private final WeatherService weatherService = new WeatherService();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("EEE d MMM", java.util.Locale.FRENCH);

    /**
     * Définir les données de la demande à afficher
     */
    public void setRequestData(CollabRequest request) {
        System.out.println("setRequestData CALLED, request=" + request);

        this.currentRequest = request;
        displayRequestDetails();
    }

    /**
     * Afficher les détails de la demande
     */
    private void displayRequestDetails() {
        if (currentRequest == null) return;

        titleText.setText(currentRequest.getTitle());
        locationText.setText(currentRequest.getLocation());
        dateRangeText.setText(currentRequest.getStartDate() + " - " + currentRequest.getEndDate());
        neededPeopleText.setText(String.valueOf(currentRequest.getNeededPeople()));
        salaryText.setText(currentRequest.getSalary() + " DT/jour");
        descriptionArea.setText(currentRequest.getDescription());
        descriptionArea.setEditable(false);

        // Récupérer le nom du publisher (si disponible)
        try {
            publisherText.setText("Publié par Ali Ben Ahmed");
        } catch (Exception e) {
            publisherText.setText("Publié par un agriculteur");
        }

        loadWeatherForecast();
    }

    private void loadWeatherForecast() {
        if (weatherContainer == null) return;
        weatherContainer.getChildren().clear();

        if (currentRequest.getLatitude() == null || currentRequest.getLongitude() == null) {
            Text noLoc = new Text("Prévisions non disponibles (localisation de la parcelle non renseignée).");
            noLoc.setStyle("-fx-fill: #757575; -fx-font-size: 14px;");
            weatherContainer.getChildren().add(noLoc);
            return;
        }

        // Open-Meteo ne fournit que les 16 prochains jours
        long joursRestants = ChronoUnit.DAYS.between(LocalDate.now(), currentRequest.getStartDate());
        if (joursRestants > 16) {
            Text horsPeriode = new Text("Les prévisions météo (Open-Meteo) ne couvrent que les 16 prochains jours. La période de travail de cette demande est au-delà ; consultez la météo plus proche des dates.");
            horsPeriode.setStyle("-fx-fill: #757575; -fx-font-size: 14px;");
            horsPeriode.setWrappingWidth(700);
            weatherContainer.getChildren().add(horsPeriode);
            return;
        }

        Text loading = new Text("Chargement des prévisions...");
        loading.setStyle("-fx-fill: #757575; -fx-font-size: 14px;");
        weatherContainer.getChildren().add(loading);

        new Thread(() -> {
            List<DailyForecast> forecast = weatherService.getForecast(
                currentRequest.getLatitude(),
                currentRequest.getLongitude(),
                currentRequest.getStartDate(),
                currentRequest.getEndDate()
            );
            Platform.runLater(() -> displayForecast(forecast));
        }).start();
    }

    private void displayForecast(List<DailyForecast> forecast) {
        weatherContainer.getChildren().clear();
        if (forecast == null || forecast.isEmpty()) {
            Text t = new Text("Aucune prévision disponible pour cette période (données indisponibles ou période hors des 16 prochains jours).");
            t.setStyle("-fx-fill: #757575; -fx-font-size: 14px;");
            t.setWrappingWidth(700);
            weatherContainer.getChildren().add(t);
            return;
        }
        for (DailyForecast day : forecast) {
            HBox card = new HBox(20);
            card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            card.setPadding(new Insets(12, 16, 12, 16));
            card.setStyle("-fx-background-color: #E3F2FD; -fx-background-radius: 8;");

            String dateStr = day.getDate().format(DATE_FMT);
            String temps = String.format("%.0f°C / %.0f°C", day.getTempMin(), day.getTempMax());
            String precip = day.getPrecipitationMm() > 0
                ? String.format("%.1f mm pluie", day.getPrecipitationMm())
                : "Pas de pluie";

            Text dateText = new Text(dateStr);
            dateText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Text tempText = new Text(temps);
            tempText.setStyle("-fx-font-size: 14px; -fx-fill: #1565C0;");
            Text precipText = new Text(precip);
            precipText.setStyle("-fx-font-size: 13px; -fx-fill: #424242;");
            Text descText = new Text(day.getWeatherDescription());
            descText.setStyle("-fx-font-size: 13px; -fx-fill: #616161;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            VBox left = new VBox(4, dateText, descText);
            VBox right = new VBox(4, tempText, precipText);
            card.getChildren().addAll(left, spacer, right);
            weatherContainer.getChildren().add(card);
        }
    }

    /**
     * Gérer le clic sur le bouton "Postuler"
     */
    @FXML
    private void handleApply() {
        // Vérification critique AVANT tout !
        if (currentRequest == null) {
            showError("Erreur", "Aucune donnée de la demande sélectionnée ! Impossible d’ouvrir le formulaire de candidature.");
            System.err.println("handleApply appelé mais currentRequest est null !");
            return;
        }
        try {
            // Chargement de la fenêtre FXML du modal de candidature
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ApplyCollaboration.fxml"));
            Parent root = loader.load();

            // Récupérer le controller du modal
            ApplyCollaborationController controller = loader.getController();

            // Passer les infos nécessaires au controller du modal
            controller.setRequestData(currentRequest.getId(), currentRequest.getTitle());

            // Création et configuration de la fenêtre modale
            Stage modalStage = new Stage();
            modalStage.setTitle("Postuler à l'offre");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(applyButton.getScene().getWindow());
            modalStage.setScene(new Scene(root));
            modalStage.setResizable(false);

            // Afficher le modal et bloquer la fenêtre parente
            modalStage.showAndWait();

        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire de candidature : " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Gérer le clic sur le bouton "Retour"
     */
    @FXML
    private Button backButton;

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        // Si c'est le Stage principal, navigue. Sinon, ferme la fenêtre.
        if (stage.getOwner() == null) {
            // Fenêtre principale, on fait une navigation :
            MainCollabFX.showExploreCollaborations();
        } else {
            // Fenêtre modale, on peut fermer :
            stage.close();
        }
    }

    /**
     * Afficher une alerte d'erreur
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
