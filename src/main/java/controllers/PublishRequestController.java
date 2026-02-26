package controllers;

import entities.CollabRequest;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import mains.MainFX;
import netscape.javascript.JSObject;
import services.CollabRequestService;
import utils.MapPickerBridge;
import validators.CollabRequestValidator;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controller pour le formulaire de publication d'une demande
 */
public class PublishRequestController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField locationField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<Integer> neededPeopleCombo;

    @FXML
    private TextField salaryField;

    @FXML
    private TextArea descriptionArea;

    private final CollabRequestService service = new CollabRequestService();

    // Coordonnées sélectionnées via la carte (facultatives)
    private Double selectedLatitude;
    private Double selectedLongitude;
    private Stage mapStage;
    private MapPickerBridge mapPickerBridge;

    @FXML
    public void initialize() {
        // Remplir le combo box avec des valeurs de 1 à 50
        for (int i = 1; i <= 50; i++) {
            neededPeopleCombo.getItems().add(i);
        }
        neededPeopleCombo.setValue(1);
    }

    @FXML
    private void handlePublish() {
        try {
            // Récupérer les valeurs
            String title = titleField.getText();
            String location = locationField.getText();
            String description = descriptionArea.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            Integer neededPeople = neededPeopleCombo.getValue();
            String salaryText = salaryField.getText();

            // Validation des champs obligatoires
            if (!validateFields(title, location, description, startDate, endDate, neededPeople, salaryText)) {
                return;
            }

            // Validation avec le validator
            CollabRequestValidator.validateTitle(title);
            CollabRequestValidator.validateDescription(description);
            CollabRequestValidator.validateDates(startDate, endDate);
            CollabRequestValidator.validateNeededPeople(neededPeople);

            // Parser le salaire
            double salary;
            try {
                salary = Double.parseDouble(salaryText);
                if (salary < 0) {
                    showError("Erreur de saisie", "Le salaire ne peut pas être négatif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Erreur de saisie", "Le salaire doit être un nombre valide.");
                return;
            }

            // Créer la demande avec TOUS les champs
            CollabRequest request = new CollabRequest();
            request.setRequesterId(1L); // ID utilisateur connecté (à remplacer par session)
            request.setTitle(title);
            request.setLocation(location);
            request.setDescription(description);
            request.setStartDate(startDate);
            request.setEndDate(endDate);
            request.setNeededPeople(neededPeople);
            request.setSalary(salary);
            request.setPublisher("Ali Ben Ahmed"); // Nom utilisateur connecté (à remplacer par session)
            request.setStatus("PENDING"); // Statut PENDING par défaut

            // Si l'utilisateur a choisi un point sur la carte, on persiste les coordonnées
            if (selectedLatitude != null && selectedLongitude != null) {
                request.setLatitude(selectedLatitude);
                request.setLongitude(selectedLongitude);
            }

            // Sauvegarder
            long id = service.add(request);

            if (id > 0) {
                System.out.println("✅ Demande publiée avec l'ID: " + id + " (statut: PENDING)");

                showInfo("Succès",
                        "Votre demande a été soumise avec succès !\n\n" +
                                "Elle sera visible sur la page \"Explore Collaborations\" " +
                                "après validation par un administrateur.\n\n" +
                                "Vous pouvez suivre son statut dans \"Mes Demandes\".");

                MainFX.showMyRequests();
            } else {
                showError("Erreur", "Impossible de publier la demande.");
            }

        } catch (IllegalArgumentException e) {
            showError("Validation échouée", e.getMessage());
        } catch (SQLException e) {
            showError("Erreur de base de données", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ouvre une fenêtre avec une carte OpenStreetMap (Leaflet) pour choisir la parcelle.
     */
    @FXML
    private void handleChooseOnMap() {
        if (mapStage != null && mapStage.isShowing()) {
            mapStage.toFront();
            return;
        }

        WebView webView = new WebView();
        webView.setPrefSize(900, 600);
        WebEngine engine = webView.getEngine();

        mapPickerBridge = new MapPickerBridge(() -> {
            // Callback invoqué depuis MapPickerBridge.setLocation(...)
            locationField.setText(mapPickerBridge.getAddress());
            selectedLatitude = mapPickerBridge.getLatitude();
            selectedLongitude = mapPickerBridge.getLongitude();

            if (mapStage != null) {
                mapStage.close();
                mapStage = null;
            }
        });

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("javaBridge", mapPickerBridge);
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Erreur", "Impossible de connecter la carte à l'application.");
                }
            }
        });

        URL htmlUrl = getClass().getResource("/html/map_picker.html");
        if (htmlUrl == null) {
            showError("Erreur", "Fichier map_picker.html introuvable dans les ressources.");
            return;
        }
        engine.load(htmlUrl.toExternalForm());

        mapStage = new Stage();
        mapStage.setTitle("Choisir l'emplacement de la parcelle");
        mapStage.setScene(new Scene(webView));
        mapStage.setOnHidden(event -> mapStage = null);
        mapStage.show();
    }

    /**
     * Valide que tous les champs obligatoires sont remplis
     */
    private boolean validateFields(String title, String location, String description,
                                   LocalDate startDate, LocalDate endDate,
                                   Integer neededPeople, String salary) {

        if (title == null || title.trim().isEmpty()) {
            showWarning("Champ manquant", "Le titre est obligatoire.");
            return false;
        }

        if (location == null || location.trim().isEmpty()) {
            showWarning("Champ manquant", "Le lieu est obligatoire.");
            return false;
        }

        if (description == null || description.trim().isEmpty()) {
            showWarning("Champ manquant", "La description est obligatoire.");
            return false;
        }

        if (startDate == null) {
            showWarning("Champ manquant", "La date de début est obligatoire.");
            return false;
        }

        if (endDate == null) {
            showWarning("Champ manquant", "La date de fin est obligatoire.");
            return false;
        }

        if (neededPeople == null || neededPeople < 1) {
            showWarning("Champ manquant", "Le nombre de personnes doit être au moins 1.");
            return false;
        }

        if (salary == null || salary.trim().isEmpty()) {
            showWarning("Champ manquant", "Le salaire est obligatoire.");
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Annuler la publication ?");
        confirm.setContentText("Les informations saisies seront perdues.");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            MainFX.showExploreCollaborations();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
