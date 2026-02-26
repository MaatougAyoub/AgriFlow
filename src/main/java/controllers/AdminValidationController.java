package controllers;

import entities.CollabRequest;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import mains.MainFX;
import services.CollabRequestService;
import utils.TelegramNotifier;

import java.sql.SQLException;
import java.util.List;

public class AdminValidationController {

    @FXML
    private ComboBox<String> filterCombo;

    @FXML
    private VBox requestsContainer;

    private CollabRequestService service = new CollabRequestService();

    @FXML
    public void initialize() {
        initializeFilter();
        loadPendingRequests();
    }

    private void initializeFilter() {
        filterCombo.getItems().addAll("Toutes", "En attente", "Approuvées", "Rejetées");
        filterCombo.setValue("En attente");
        filterCombo.setOnAction(e -> handleFilterChange());
    }

    private void loadPendingRequests() {
        try {
            List<CollabRequest> requests = service.findByStatus("PENDING");
            System.out.println("✅ Chargé " + requests.size() + " demandes en attente");
            displayRequests(requests);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des demandes", e.getMessage());
        }
    }

    private void handleFilterChange() {
        String filter = filterCombo.getValue();
        try {
            List<CollabRequest> requests;

            switch (filter) {
                case "En attente":
                    requests = service.findByStatus("PENDING");
                    break;
                case "Approuvées":
                    requests = service.findByStatus("APPROVED");
                    break;
                case "Rejetées":
                    requests = service.findByStatus("REJECTED");
                    break;
                default:
                    requests = service.findAll();
            }

            System.out.println("✅ Filtré : " + requests.size() + " demandes (" + filter + ")");
            displayRequests(requests);

        } catch (SQLException e) {
            showError("Erreur lors du filtrage", e.getMessage());
        }
    }

    private void displayRequests(List<CollabRequest> requests) {
        requestsContainer.getChildren().clear();

        if (requests.isEmpty()) {
            Text noRequests = new Text("Aucune demande à afficher.");
            noRequests.setStyle("-fx-font-size: 16px; -fx-fill: #757575;");
            requestsContainer.getChildren().add(noRequests);
            return;
        }

        for (CollabRequest request : requests) {
            VBox requestCard = createRequestCard(request);
            requestsContainer.getChildren().add(requestCard);
        }
    }

    private VBox createRequestCard(CollabRequest request) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // En-tête avec titre et statut
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        Text title = new Text(request.getTitle());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Text publisher = new Text("Publié par : " + request.getPublisher());
        publisher.setStyle("-fx-fill: #757575; -fx-font-size: 12px;");
        titleBox.getChildren().addAll(title, publisher);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(request.getStatus());
        statusLabel.setStyle(getStatusStyle(request.getStatus()));
        statusLabel.setPadding(new Insets(5, 15, 5, 15));

        header.getChildren().addAll(titleBox, spacer, statusLabel);

        // Informations
        HBox infoBox = new HBox(30);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Text location = new Text("📍 " + request.getLocation());
        Text dates = new Text("📅 " + request.getStartDate() + " - " + request.getEndDate());
        Text salary = new Text("💰 " + request.getSalary() + " DT/jour");
        salary.setStyle("-fx-fill: #2E7D32; -fx-font-weight: bold;");

        infoBox.getChildren().addAll(location, dates, salary);

        // Boutons d'action (seulement si PENDING)
        HBox actionsBox = new HBox(15);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        if (request.getStatus().equals("PENDING")) {
            Button validateBtn = new Button("✓ Valider");
            validateBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                    "-fx-padding: 8 20; -fx-background-radius: 8; -fx-font-size: 14px;");
            validateBtn.setOnAction(e -> handleValidate(request));

            Button rejectBtn = new Button("✗ Rejeter");
            rejectBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; " +
                    "-fx-padding: 8 20; -fx-background-radius: 8; -fx-font-size: 14px;");
            rejectBtn.setOnAction(e -> handleReject(request));

            actionsBox.getChildren().addAll(validateBtn, rejectBtn);
        } else {
            Text statusText = new Text("Déjà traitée");
            statusText.setStyle("-fx-fill: #757575; -fx-font-style: italic;");
            actionsBox.getChildren().add(statusText);
        }

        card.getChildren().addAll(header, infoBox, actionsBox);

        return card;
    }

    private String getStatusStyle(String status) {
        switch (status) {
            case "PENDING":
                return "-fx-background-color: #FFA726; -fx-text-fill: white; -fx-background-radius: 15;";
            case "APPROVED":
                return "-fx-background-color: #66BB6A; -fx-text-fill: white; -fx-background-radius: 15;";
            case "REJECTED":
                return "-fx-background-color: #EF5350; -fx-text-fill: white; -fx-background-radius: 15;";
            default:
                return "-fx-background-color: #BDBDBD; -fx-text-fill: white; -fx-background-radius: 15;";
        }
    }

    private void handleValidate(CollabRequest request) {
        System.out.println("✅ Validation de la demande #" + request.getId());

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Valider cette demande ?");
        confirm.setContentText("La demande \"" + request.getTitle() + "\" sera visible publiquement.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                service.updateStatus(request.getId(), "APPROVED");

                // Notification Telegram (si configurée)
                String msg = "✅ Demande approuvée : \"" + request.getTitle() + "\"\n"
                        + "📍 Lieu : " + request.getLocation() + "\n"
                        + "🗓 Période : " + request.getStartDate() + " → " + request.getEndDate();
                TelegramNotifier.sendText(msg);

                showInfo("Succès", "La demande a été validée avec succès !");
                handleFilterChange(); // Rafraîchir la liste
            } catch (SQLException e) {
                showError("Erreur", "Impossible de valider la demande.");
            }
        }
    }

    private void handleReject(CollabRequest request) {
        System.out.println("❌ Rejet de la demande #" + request.getId());

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Rejeter cette demande ?");
        confirm.setContentText("La demande \"" + request.getTitle() + "\" sera rejetée.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                service.updateStatus(request.getId(), "REJECTED");

                // Notification Telegram (si configurée)
                String msg = "❌ Demande rejetée : \"" + request.getTitle() + "\"\n"
                        + "📍 Lieu : " + request.getLocation();
                TelegramNotifier.sendText(msg);

                showInfo("Rejet", "La demande a été rejetée.");
                handleFilterChange(); // Rafraîchir la liste
            } catch (SQLException e) {
                showError("Erreur", "Impossible de rejeter la demande.");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        System.out.println("🔄 Rafraîchissement...");
        handleFilterChange();
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
