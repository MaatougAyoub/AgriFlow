package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import mains.MainFX;
import entities.CollabRequest;
import services.CollabRequestService;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller pour l'interface Admin de validation des demandes
 */
public class AdminValidationController {
    
    @FXML
    private TableView<CollabRequest> requestsTable;
    
    @FXML
    private TableColumn<CollabRequest, String> titleColumn;
    
    @FXML
    private TableColumn<CollabRequest, String> statusColumn;
    
    @FXML
    private TableColumn<CollabRequest, String> dateColumn;
    
    @FXML
    private ComboBox<String> filterCombo;
    
    private CollabRequestService service = new CollabRequestService();
    
    @FXML
    public void initialize() {
        filterCombo.getItems().addAll("Demandes en attente", "Toutes les demandes", "Validées", "Rejetées");
        filterCombo.setValue("Demandes en attente");
        loadPendingRequests();
    }
    
    private void loadPendingRequests() {
        try {
            List<CollabRequest> requests = service.findByStatus("PENDING");
            System.out.println("✅ Chargé " + requests.size() + " demandes en attente");
        } catch (SQLException e) {
            showError("Erreur lors du chargement", e.getMessage());
        }
    }
    
    @FXML
    private void handleValidate() {
        CollabRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                service.updateStatus(selected.getId(), "VALIDATED");
                loadPendingRequests();
                showInfo("Succès", "Demande validée avec succès !");
            } catch (SQLException e) {
                showError("Erreur", e.getMessage());
            }
        } else {
            showWarning("Aucune sélection", "Veuillez sélectionner une demande.");
        }
    }
    
    @FXML
    private void handleReject() {
        CollabRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Rejeter la demande ?");
            confirm.setContentText("La demande sera marquée comme rejetée.");
            
            if (confirm.showAndWait().get() == ButtonType.OK) {
                try {
                    service.updateStatus(selected.getId(), "REJECTED");
                    loadPendingRequests();
                    showInfo("Succès", "Demande rejetée.");
                } catch (SQLException e) {
                    showError("Erreur", e.getMessage());
                }
            }
        }
    }
    
    @FXML
    private void handleViewDetails() {
        CollabRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            MainFX.showCollabRequestDetails();
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
