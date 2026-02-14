package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import mains.MainFX;
import entities.CollabRequest;
import services.CollabRequestService;

import java.sql.SQLException;

/**
 * Controller pour l'interface de détails d'une demande de collaboration
 */
public class CollabRequestDetailsController {
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private Label locationLabel;
    
    @FXML
    private Label dateLabel;
    
    @FXML
    private Label neededPeopleLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private TextArea descriptionArea;
    
    private CollabRequestService service = new CollabRequestService();
    private long requestId = 1; // Pour démo, normalement passé en paramètre
    
    @FXML
    public void initialize() {
        loadRequestDetails();
    }
    
    private void loadRequestDetails() {
        try {
            CollabRequest request = service.findById(requestId);
            if (request != null) {
                titleLabel.setText(request.getTitle());
                statusLabel.setText(request.getStatus());
                descriptionArea.setText(request.getDescription());
                neededPeopleLabel.setText(String.valueOf(request.getNeededPeople()));
                dateLabel.setText(request.getStartDate() + " - " + request.getEndDate());
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement des détails", e.getMessage());
        }
    }
    
    @FXML
    private void handleApply() {
        System.out.println("📝 Postuler à la demande #" + requestId);
        showInfo("Candidature envoyée", "Votre candidature a été envoyée avec succès !");
    }
    
    @FXML
    private void handleBack() {
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
