package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import mains.MainFX;
import entities.CollabRequest;
import services.CollabRequestService;
import validators.CollabRequestValidator;

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
    
    private CollabRequestService service = new CollabRequestService();
    
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
            String title = titleField.getText();
            String description = descriptionArea.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            int neededPeople = neededPeopleCombo.getValue();
            
            // Validation
            CollabRequestValidator.validateTitle(title);
            CollabRequestValidator.validateDescription(description);
            CollabRequestValidator.validateDates(startDate, endDate);
            CollabRequestValidator.validateNeededPeople(neededPeople);
            
            // Créer la demande
            CollabRequest request = new CollabRequest(
                1L, // requester_id (demo)
                title,
                description,
                startDate,
                endDate,
                neededPeople,
                "PENDING"
            );
            
            long id = service.add(request);
            System.out.println("✅ Demande publiée avec l'ID: " + id);
            
            showInfo("Succès", "Votre demande a été publiée avec succès ! Elle est en attente de validation.");
            MainFX.showMyRequests();
            
        } catch (IllegalArgumentException e) {
            showError("Validation échouée", e.getMessage());
        } catch (SQLException e) {
            showError("Erreur de base de données", e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        MainFX.showExploreCollaborations();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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
