package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mains.MainFX;
import entities.CollabRequest;
import services.CollabApplicationService;
import services.CollabRequestService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MyRequestsController {

    @FXML private TableView<CollabRequest> requestsTable;
    @FXML private TableColumn<CollabRequest, String> titleColumn;
    @FXML private TableColumn<CollabRequest, LocalDate> startDateColumn;
    @FXML private TableColumn<CollabRequest, LocalDate> endDateColumn;
    @FXML private TableColumn<CollabRequest, String> statusColumn;
    @FXML private TableColumn<CollabRequest, Integer> neededPeopleColumn;
    @FXML private TableColumn<CollabRequest, Integer> applicationsCountColumn;

    private CollabRequestService requestService = new CollabRequestService();
    private CollabApplicationService applicationService = new CollabApplicationService();

    private static Long selectedRequestId;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadRequests();
    }

    private void setupTableColumns() {
        // Configurer les colonnes de base
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        neededPeopleColumn.setCellValueFactory(new PropertyValueFactory<>("neededPeople"));

        // Colonne pour le nombre de candidatures
        applicationsCountColumn.setCellValueFactory(cellData -> {
            try {
                int count = applicationService.countApplicationsByRequestId(cellData.getValue().getId());
                return javafx.beans.binding.Bindings.createObjectBinding(() -> count);
            } catch (SQLException e) {
                e.printStackTrace();
                return javafx.beans.binding.Bindings.createObjectBinding(() -> 0);
            }
        });

        // Style pour le statut
        statusColumn.setCellFactory(column -> new TableCell<CollabRequest, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "PENDING":
                            setStyle("-fx-background-color: #FFA726; -fx-text-fill: white; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5;");
                            break;
                        case "APPROVED":
                            setStyle("-fx-background-color: #66BB6A; -fx-text-fill: white; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5;");
                            break;
                        case "REJECTED":
                            setStyle("-fx-background-color: #EF5350; -fx-text-fill: white; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5;");
                            break;
                        default:
                            setStyle("-fx-alignment: CENTER;");
                    }
                }
            }
        });

        // Style pour le nombre de candidatures (badge)
        applicationsCountColumn.setCellFactory(column -> new TableCell<CollabRequest, Integer>() {
            @Override
            protected void updateItem(Integer count, boolean empty) {
                super.updateItem(count, empty);
                if (empty || count == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(count.toString());
                    if (count > 0) {
                        setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-font-size: 14px; -fx-border-radius: 15; -fx-background-radius: 15; -fx-padding: 5 12;");
                    } else {
                        setStyle("-fx-text-fill: #BDBDBD; -fx-alignment: CENTER; -fx-font-size: 14px;");
                    }
                }
            }
        });

        // Centrer les colonnes numériques
        neededPeopleColumn.setStyle("-fx-alignment: CENTER;");
        applicationsCountColumn.setStyle("-fx-alignment: CENTER;");
    }

    private void loadRequests() {
        try {
            // TODO: Filtrer par requester_id de l'utilisateur connecté
            // Pour l'instant, on charge toutes les demandes
            List<CollabRequest> requests = requestService.findAll();

            if (requestsTable != null) {
                requestsTable.getItems().clear();
                requestsTable.getItems().addAll(requests);
            }

            System.out.println("✅ Chargé " + requests.size() + " demande(s)");

        } catch (SQLException e) {
            showError("Erreur lors du chargement des demandes", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewDetails() {
        CollabRequest selected = requestsTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            System.out.println("🔍 Affichage des détails de la demande #" + selected.getId());
            MainFX.showCollabRequestDetails(selected.getId());
        } else {
            showWarning("Aucune sélection", "Veuillez sélectionner une demande.");
        }
    }

    @FXML
    private void handleViewApplications() {
        CollabRequest selected = requestsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Aucune sélection", "Veuillez sélectionner une demande pour voir les candidatures.");
            return;
        }

        try {
            int count = applicationService.countApplicationsByRequestId(selected.getId());

            if (count == 0) {
                showInfo("Aucune candidature",
                        "Cette demande n'a reçu aucune candidature pour le moment.\n\n" +
                                "Les candidats pourront postuler une fois la demande approuvée.");
                return;
            }

            // Stocker l'ID de la demande sélectionnée
            selectedRequestId = selected.getId();

            System.out.println("📋 Affichage de " + count + " candidature(s) pour la demande #" + selected.getId());

            // Naviguer vers la page ViewApplications
            MainFX.showViewApplications();

        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les candidatures : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        CollabRequest selected = requestsTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer cette demande ?");
            confirm.setContentText("Êtes-vous sûr de vouloir supprimer \"" + selected.getTitle() + "\" ?\n\n" +
                    "⚠️ Toutes les candidatures associées seront également supprimées.");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                try {
                    requestService.delete(selected.getId());
                    showInfo("Succès", "La demande a été supprimée avec succès.");
                    loadRequests();
                } catch (SQLException e) {
                    showError("Erreur", "Impossible de supprimer la demande.");
                }
            }
        } else {
            showWarning("Aucune sélection", "Veuillez sélectionner une demande à supprimer.");
        }
    }

    @FXML
    private void handleBack() {
        MainFX.showExploreCollaborations();
    }

    // Getter pour l'ID de la demande sélectionnée
    public static Long getSelectedRequestId() {
        return selectedRequestId;
    }

    // Méthodes utilitaires
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
