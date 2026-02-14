package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import mains.MainFX;
import entities.CollabRequest;
import services.CollabRequestService;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller pour l'interface d'exploration des demandes de collaboration
 */
public class ExploreCollaborationsController {
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> locationFilter;
    
    @FXML
    private ComboBox<String> statusFilter;
    
    private CollabRequestService service = new CollabRequestService();
    
    @FXML
    public void initialize() {
        loadCollaborations();
    }
    
    private void loadCollaborations() {
        try {
            List<CollabRequest> requests = service.findAll();
            System.out.println("✅ Chargé " + requests.size() + " demandes de collaboration");
        } catch (SQLException e) {
            showError("Erreur lors du chargement des collaborations", e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        System.out.println("🔍 Recherche: " + searchText);
    }
    
    @FXML
    private void handleViewDetails() {
        MainFX.showCollabRequestDetails();
    }
    
    @FXML
    private void handlePublishRequest() {
        MainFX.showPublishRequest();
    }
    
    @FXML
    private void handleMyRequests() {
        MainFX.showMyRequests();
    }
    
    @FXML
    private void handleMyApplications() {
        MainFX.showMyApplications();
    }
    
    @FXML
    private void handleAdminValidation() {
        MainFX.showAdminValidation();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
