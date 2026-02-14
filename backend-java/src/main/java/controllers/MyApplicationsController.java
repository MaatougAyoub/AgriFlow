package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import mains.MainFX;
import entities.CollabApplication;
import services.CollabApplicationService;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller pour l'interface "Mes Candidatures"
 */
public class MyApplicationsController {
    
    @FXML
    private TableView<CollabApplication> applicationsTable;
    
    @FXML
    private TableColumn<CollabApplication, String> titleColumn;
    
    @FXML
    private TableColumn<CollabApplication, String> statusColumn;
    
    private CollabApplicationService service = new CollabApplicationService();
    
    @FXML
    public void initialize() {
        loadMyApplications();
    }
    
    private void loadMyApplications() {
        try {
            List<CollabApplication> applications = service.findAll();
            System.out.println("✅ Chargé " + applications.size() + " candidatures");
            // TODO: Filtrer par applicant_id de l'utilisateur connecté
        } catch (SQLException e) {
            showError("Erreur lors du chargement", e.getMessage());
        }
    }
    
    @FXML
    private void handleViewRequest() {
        CollabApplication selected = applicationsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            MainFX.showCollabRequestDetails();
        } else {
            showWarning("Aucune sélection", "Veuillez sélectionner une candidature.");
        }
    }
    
    @FXML
    private void handleWithdraw() {
        CollabApplication selected = applicationsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Retirer la candidature ?");
            confirm.setContentText("Cette action est irréversible.");
            
            if (confirm.showAndWait().get() == ButtonType.OK) {
                try {
                    service.delete(selected.getId());
                    loadMyApplications();
                    showInfo("Succès", "Candidature retirée avec succès.");
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
