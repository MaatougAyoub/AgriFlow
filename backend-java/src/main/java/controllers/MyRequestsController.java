package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import mains.MainFX;
import entities.CollabRequest;
import services.CollabRequestService;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller pour l'interface "Mes Demandes"
 */
public class MyRequestsController {
    
    @FXML
    private TableView<CollabRequest> requestsTable;
    
    @FXML
    private TableColumn<CollabRequest, String> titleColumn;
    
    @FXML
    private TableColumn<CollabRequest, String> statusColumn;
    
    @FXML
    private TableColumn<CollabRequest, String> datesColumn;
    
    private CollabRequestService service = new CollabRequestService();
    
    @FXML
    public void initialize() {
        loadMyRequests();
    }
    
    private void loadMyRequests() {
        try {
            List<CollabRequest> requests = service.findAll();
            System.out.println("✅ Chargé " + requests.size() + " demandes");
            // TODO: Filtrer par requester_id de l'utilisateur connecté
        } catch (SQLException e) {
            showError("Erreur lors du chargement", e.getMessage());
        }
    }
    
    @FXML
    private void handleViewDetails() {
        CollabRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            MainFX.showCollabRequestDetails();
        } else {
            showWarning("Aucune sélection", "Veuillez sélectionner une demande.");
        }
    }
    
    @FXML
    private void handleDelete() {
        CollabRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer la demande ?");
            confirm.setContentText("Cette action est irréversible.");
            
            if (confirm.showAndWait().get() == ButtonType.OK) {
                try {
                    service.delete(selected.getId());
                    loadMyRequests();
                    showInfo("Succès", "Demande supprimée avec succès.");
                } catch (SQLException e) {
                    showError("Erreur", e.getMessage());
                }
            }
        }
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
    
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
