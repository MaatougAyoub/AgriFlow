package controllers;

import entities.CollabApplication;
import entities.CollabRequest;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import mains.MainFX;
import services.CollabApplicationService;
import services.CollabRequestService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MyApplicationsController {

    @FXML private TableView<CollabApplication> applicationsTable;
    @FXML private TableColumn<CollabApplication, Long> idRequestColumn;
    @FXML private TableColumn<CollabApplication, String> statusColumn;
    @FXML private TableColumn<CollabApplication, String> motivationColumn;
    @FXML private TableColumn<CollabApplication, Void> actionsColumn;

    private final CollabApplicationService applicationService = new CollabApplicationService();
    private final CollabRequestService requestService = new CollabRequestService();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadMyApplications();
    }

    /**
     * Configurer les colonnes du tableau
     */
    private void setupTableColumns() {
        // Colonne ID Request
        idRequestColumn.setCellValueFactory(new PropertyValueFactory<>("requestId"));

        // Colonne Statut avec couleur
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<CollabApplication, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(getStatusText(status));

                    // Couleur selon le statut
                    switch (status.toUpperCase()) {
                        case "PENDING":
                            setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                            break;
                        case "ACCEPTED":
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                            break;
                        case "REJECTED":
                            setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        // Colonne Motivation (tronquée)
        motivationColumn.setCellValueFactory(new PropertyValueFactory<>("motivation"));
        motivationColumn.setCellFactory(column -> new TableCell<CollabApplication, String>() {
            @Override
            protected void updateItem(String motivation, boolean empty) {
                super.updateItem(motivation, empty);

                if (empty || motivation == null) {
                    setText(null);
                } else {
                    // Tronquer à 50 caractères
                    String truncated = motivation.length() > 50
                            ? motivation.substring(0, 50) + "..."
                            : motivation;
                    setText(truncated);
                }
            }
        });

        // Colonne Actions (boutons)
        actionsColumn.setCellFactory(column -> new TableCell<CollabApplication, Void>() {
            private final Button viewButton = new Button("Voir détails");
            private final Button deleteButton = new Button("Retirer");
            private final HBox buttons = new HBox(10, viewButton, deleteButton);

            {
                // Style des boutons
                viewButton.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-cursor: hand;");

                // Actions des boutons
                viewButton.setOnAction(event -> {
                    CollabApplication application = getTableView().getItems().get(getIndex());
                    handleViewDetails(application);
                });

                deleteButton.setOnAction(event -> {
                    CollabApplication application = getTableView().getItems().get(getIndex());
                    handleDeleteApplication(application);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }

    /**
     * Charger les candidatures de l'utilisateur connecté
     */
    private void loadMyApplications() {
        try {
            // ID de l'utilisateur connecté (à récupérer depuis la session)
            long userId = 1L; // Temporaire - à remplacer par l'ID réel

            List<CollabApplication> applications = applicationService.findByCandidateId(userId);

            applicationsTable.getItems().clear();
            applicationsTable.getItems().addAll(applications);

            System.out.println("✅ Chargé " + applications.size() + " candidature(s)");

        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les candidatures : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Voir les détails d'une demande
     */
    private void handleViewDetails(CollabApplication application) {
        try {
            // Récupérer la demande complète
            CollabRequest request = requestService.findById(application.getRequestId());

            if (request != null) {
                MainFX.showCollabRequestDetails(request.getId());
            } else {
                showWarning("Demande introuvable", "La demande n'existe plus.");
            }

        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les détails : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retirer une candidature
     */
    private void handleDeleteApplication(CollabApplication application) {
        // Confirmation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Retirer cette candidature ?");
        confirm.setContentText(
                "Êtes-vous sûr de vouloir retirer votre candidature ?\n\n" +
                        "ID demande : " + application.getRequestId() + "\n" +
                        "Statut : " + getStatusText(application.getStatus())
        );

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer de la base de données
                applicationService.delete(application.getId());

                // Retirer du tableau
                applicationsTable.getItems().remove(application);

                showSuccess("Candidature retirée", "Votre candidature a été retirée avec succès.");

                System.out.println("✅ Candidature #" + application.getId() + " supprimée");

            } catch (SQLException e) {
                showError("Erreur", "Impossible de retirer la candidature : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Retourner à l'accueil
     */
    @FXML
    private void handleBack() {
        MainFX.showExploreCollaborations();  // ✅ CORRIGÉ
    }

    /**
     * Actualiser la liste
     */
    @FXML
    private void handleRefresh() {
        loadMyApplications();
        showInfo("Actualisé", "La liste des candidatures a été actualisée.");
    }

    /**
     * Convertir le statut en texte français
     */
    private String getStatusText(String status) {
        if (status == null) return "INCONNU";

        switch (status.toUpperCase()) {
            case "PENDING":
                return "EN ATTENTE";
            case "ACCEPTED":
                return "ACCEPTÉE";
            case "REJECTED":
                return "REJETÉE";
            default:
                return status;
        }
    }

    /**
     * Afficher une alerte de succès
     */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Afficher une alerte d'information
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Afficher une alerte d'avertissement
     */
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
