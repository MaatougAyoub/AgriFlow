package controllers;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import mains.MainFX;
import entities.CollabRequest;
import services.CollabRequestService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ExploreCollaborationsController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> locationFilter;

    @FXML
    private ComboBox<String> typeFilter;




    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField salaryMinField;

    @FXML
    private GridPane cardsGrid;

    private CollabRequestService service = new CollabRequestService();
    private List<CollabRequest> allRequests;

    @FXML
    public void initialize() {
        initializeFilters();
        loadCollaborations();
    }

    private void initializeFilters() {
        // Remplir les ComboBox
        locationFilter.getItems().addAll("Lieu", "Sidi Khaled", "Sfax", "Nabeul", "Tozeur", "Bizerte", "Sousse");
        locationFilter.setValue("Lieu");

        typeFilter.getItems().addAll("type", "Récolte", "Plantation", "Taille", "Irrigation", "Entretien");
        typeFilter.setValue("type");


    }

    private void loadCollaborations() {
        try {
            // Charger SEULEMENT les demandes approuvées
            allRequests = service.findByStatus("APPROVED");
            System.out.println("✅ Chargé " + allRequests.size() + " demandes approuvées");
            displayCards(allRequests);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des collaborations", e.getMessage());
        }
    }

    private void displayCards(List<CollabRequest> requests) {
        cardsGrid.getChildren().clear();

        int column = 0;
        int row = 0;

        for (CollabRequest request : requests) {
            VBox card = createRequestCard(request);
            cardsGrid.add(card, column, row);

            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }

        if (requests.isEmpty()) {
            Text noResults = new Text("Aucune collaboration trouvée.");
            noResults.setStyle("-fx-font-size: 18px; -fx-fill: #757575;");
            cardsGrid.add(noResults, 0, 0);
        }
    }

    private VBox createRequestCard(CollabRequest request) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(350);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // Titre
        Text title = new Text(request.getTitle());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Lieu
        HBox locationBox = new HBox(8);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        Text locationIcon = new Text("📍");
        Text locationText = new Text(request.getLocation());
        locationText.setStyle("-fx-fill: #757575;");
        locationBox.getChildren().addAll(locationIcon, locationText);

        // Dates
        HBox dateBox = new HBox(8);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        Text dateIcon = new Text("📅");
        Text dateText = new Text(request.getStartDate() + " - " + request.getEndDate());
        dateText.setStyle("-fx-fill: #757575;");
        dateBox.getChildren().addAll(dateIcon, dateText);

        // Salaire
        HBox salaryBox = new HBox(8);
        salaryBox.setAlignment(Pos.CENTER_LEFT);
        Text salaryIcon = new Text("💰");
        Text salaryText = new Text(String.format("%.0f DT/jour", request.getSalary()));
        salaryText.setStyle("-fx-fill: #4CAF50; -fx-font-weight: bold; -fx-font-size: 16px;");
        salaryBox.getChildren().addAll(salaryIcon, salaryText);

        // Bouton
        Button detailsBtn = new Button("Voir détails");
        detailsBtn.getStyleClass().add("btn-primary");
        detailsBtn.setMaxWidth(Double.MAX_VALUE);
        detailsBtn.setUserData(request.getId());
        detailsBtn.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CollabRequestDetails.fxml"));
                Parent root = loader.load();

                // Passe directement l'objet complet
                CollabRequestDetailsController controller = loader.getController();
                controller.setRequestData(request);

                Stage stage = new Stage();
                stage.setTitle("Détails de la demande");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                showError("Erreur", "Impossible d’ouvrir les détails : " + e.getMessage());
                e.printStackTrace();
            }
        });

        card.getChildren().addAll(title, locationBox, dateBox, salaryBox, detailsBtn);

        return card;
    }

    @FXML
    private void handleSearch() {
        System.out.println("🔍 Recherche avec filtres...");

        List<CollabRequest> filtered = allRequests.stream()
                .filter(this::matchesFilters)
                .collect(Collectors.toList());

        System.out.println("✅ " + filtered.size() + " résultats trouvés");
        displayCards(filtered);
    }

    private boolean matchesFilters(CollabRequest request) {
        // Filtre texte de recherche
        String searchText = searchField.getText();
        if (searchText != null && !searchText.trim().isEmpty()) {
            String search = searchText.toLowerCase();
            if (!request.getTitle().toLowerCase().contains(search) &&
                    !request.getLocation().toLowerCase().contains(search) &&
                    !request.getDescription().toLowerCase().contains(search)) {
                return false;
            }
        }

        // Filtre lieu
        String location = locationFilter.getValue();
        if (location != null && !location.equals("Tous")) {
            if (!request.getLocation().equalsIgnoreCase(location)) {
                return false;
            }
        }



        // Filtre type (dans le titre)
        String type = typeFilter.getValue();
        if (type != null && !type.equals("Tous")) {
            if (!request.getTitle().toLowerCase().contains(type.toLowerCase())) {
                return false;
            }
        }

        // Filtre salaire min
        String salaryMin = salaryMinField.getText();
        if (salaryMin != null && !salaryMin.trim().isEmpty()) {
            try {
                double minSalary = Double.parseDouble(salaryMin);
                if (request.getSalary() < minSalary) {
                    return false;
                }
            } catch (NumberFormatException e) {
                // Ignorer si invalide
            }
        }

        // Filtre date début
        LocalDate startDate = startDatePicker.getValue();
        if (startDate != null) {
            if (request.getStartDate().isBefore(startDate)) {
                return false;
            }
        }

        // Filtre date fin
        LocalDate endDate = endDatePicker.getValue();
        if (endDate != null) {
            if (request.getEndDate().isAfter(endDate)) {
                return false;
            }
        }

        return true;
    }

    @FXML
    private void handleRefresh() {
        System.out.println("🔄 Rafraîchissement des données...");

        // Réinitialiser les filtres
        searchField.clear();
        locationFilter.setValue("Tous");
        typeFilter.setValue("Tous");
        salaryMinField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);

        // Recharger toutes les demandes
        loadCollaborations();
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