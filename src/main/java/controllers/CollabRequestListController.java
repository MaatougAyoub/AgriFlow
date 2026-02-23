package controllers;

import entities.CollabRequest;
import services.CollabRequestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.List;

/**
 * Controller pour la liste des demandes de collaboration
 * Gère CollabRequestList.fxml
 */
public class CollabRequestListController {
    
    @FXML private TableView<CollabRequest> requestsTable;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Label resultsLabel;
    
    private CollabRequestService service;
    private ObservableList<CollabRequest> requestsList;
    private static Long selectedRequestId;

    public static Long getSelectedRequestId() {
        return selectedRequestId;
    }

    public static void setSelectedRequestId(Long requestId) {
        selectedRequestId = requestId;
    }
    @FXML
    public void initialize() {
        service = new CollabRequestService();
        requestsList = FXCollections.observableArrayList();
        
        // Initialiser le filtre de statut
        statusFilter.setItems(FXCollections.observableArrayList(
            "Tous", "PENDING", "APPROVED", "REJECTED", "CLOSED"
        ));
        statusFilter.setValue("Tous");
        
        // Charger les données
        loadRequests();
        
        System.out.println("✅ CollabRequestListController initialisé");
    }

    /**
     * Charge toutes les demandes depuis la base
     */
    private void loadRequests() {
        try {
            List<CollabRequest> requests = service.findAll();
            requestsList.clear();
            requestsList.addAll(requests);
            requestsTable.setItems(requestsList);
            updateResultsLabel();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les demandes: " + e.getMessage());
        }
    }
    @FXML
    private void handleViewDetails() {
        CollabRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une demande.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CollabRequestDetails.fxml"));
            Parent root = loader.load();

            // CRUCIAL !
            CollabRequestDetailsController controller = loader.getController();
            controller.setRequestData(selected);

            Stage stage = new Stage();
            stage.setTitle("Détails de la demande");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d’ouvrir les détails : " + e.getMessage());
        }
    }
    /**
     * Recherche avec filtres
     */
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase().trim();
        String status = statusFilter.getValue();
        
        try {
            List<CollabRequest> allRequests = service.findAll();
            requestsList.clear();
            
            for (CollabRequest req : allRequests) {
                boolean matchesKeyword = keyword.isEmpty() || 
                                        (req.getTitle() != null && req.getTitle().toLowerCase().contains(keyword)) ||
                                        (req.getDescription() != null && req.getDescription().toLowerCase().contains(keyword));
                
                boolean matchesStatus = status.equals("Tous") || req.getStatus().equals(status);
                
                if (matchesKeyword && matchesStatus) {
                    requestsList.add(req);
                }
            }
            
            updateResultsLabel();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche: " + e.getMessage());
        }
    }
    
    /**
     * Rafraîchit la liste
     */
    @FXML
    private void handleRefresh() {
        loadRequests();
        searchField.clear();
        statusFilter.setValue("Tous");
    }
    
    /**
     * Modifier une demande
     */
    @FXML
    private void handleEdit() {
        CollabRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une demande à modifier.");
            return;
        }
        showAlert(Alert.AlertType.INFORMATION, "Édition", "Fonctionnalité d'édition en cours de développement...\n\nDemande sélectionnée: " + selected.getTitle());
    }
    
    /**
     * Supprimer une demande
     */
    @FXML
    private void handleDelete() {
        CollabRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une demande à supprimer.");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer la demande ?");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer:\n\n" + selected.getTitle() + "\n\nCette action est irréversible.");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                service.delete(selected.getId());
                loadRequests();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande supprimée avec succès !");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer: " + e.getMessage());
            }
        }
    }
    
    /**
     * Valider une demande (passer en APPROVED)
     */
    @FXML
    private void handleApprove() {
        updateStatus("APPROVED", "✅ Demande validée avec succès !");
    }
    
    /**
     * Rejeter une demande (passer en REJECTED)
     */
    @FXML
    private void handleReject() {
        updateStatus("REJECTED", "❌ Demande rejetée");
    }
    
    /**
     * Met à jour le statut d'une demande
     */
    private void updateStatus(String status, String successMessage) {
        CollabRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une demande.");
            return;
        }
        
        try {
            selected.setStatus(status);
            service.update(selected);
            loadRequests();
            showAlert(Alert.AlertType.INFORMATION, "Succès", successMessage);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour: " + e.getMessage());
        }
    }
    
    /**
     * Met à jour le label du nombre de résultats
     */
    private void updateResultsLabel() {
        int count = requestsList.size();
        resultsLabel.setText(count + " demande(s) affichée(s)");
    }
    
    /**
     * Affiche une alerte
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
