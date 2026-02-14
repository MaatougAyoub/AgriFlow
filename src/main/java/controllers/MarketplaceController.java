package controllers;

import entities.Annonce;
import entities.TypeAnnonce;
import services.AnnonceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Contrôleur Marketplace.
 * Affiche les annonces en grille (FlowPane), avec recherche temps réel
 * et filtre par type. Navigation complète vers Ajouter Annonce et Mes
 * Réservations.
 */
public class MarketplaceController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> typeFilter;
    @FXML
    private FlowPane annoncesContainer;
    @FXML
    private Label resultCountLabel;

    private AnnonceService annonceService;
    private List<Annonce> toutesLesAnnonces;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        annonceService = new AnnonceService();
        toutesLesAnnonces = new ArrayList<>();

        // Initialiser le filtre type avec option "Tous"
        typeFilter.getItems().add("Tous les types");
        for (TypeAnnonce t : TypeAnnonce.values()) {
            typeFilter.getItems().add(t.getLabel());
        }
        typeFilter.setValue("Tous les types");

        // Recherche en temps réel
        searchField.textProperty().addListener((obs, oldVal, newVal) -> appliquerFiltres());

        // Filtre par type
        typeFilter.setOnAction(event -> appliquerFiltres());

        // Charger les annonces
        loadAnnonces();
    }

    /**
     * Charge toutes les annonces disponibles depuis la BDD.
     */
    private void loadAnnonces() {
        annoncesContainer.getChildren().clear();
        try {
            toutesLesAnnonces = annonceService.recupererDisponibles();
            displayAnnonces(toutesLesAnnonces);
            resultCountLabel.setText(toutesLesAnnonces.size() + " annonce(s) trouvée(s)");
        } catch (SQLException e) {
            resultCountLabel.setText("Erreur de chargement");
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger les annonces : " + e.getMessage());
        }
    }

    /**
     * Applique les filtres de recherche et de type.
     */
    private void appliquerFiltres() {
        if (toutesLesAnnonces == null)
            return;

        String recherche = searchField.getText() != null
                ? searchField.getText().trim().toLowerCase()
                : "";
        String typeSelectionne = typeFilter.getValue();

        List<Annonce> filtrees = toutesLesAnnonces.stream()
                .filter(a -> {
                    // Filtre par texte
                    if (!recherche.isEmpty()) {
                        String titre = a.getTitre() != null ? a.getTitre().toLowerCase() : "";
                        String desc = a.getDescription() != null ? a.getDescription().toLowerCase() : "";
                        String cat = a.getCategorie() != null ? a.getCategorie().toLowerCase() : "";
                        String loc = a.getLocalisation() != null ? a.getLocalisation().toLowerCase() : "";
                        boolean matchTexte = titre.contains(recherche)
                                || desc.contains(recherche)
                                || cat.contains(recherche)
                                || loc.contains(recherche);
                        if (!matchTexte)
                            return false;
                    }
                    // Filtre par type
                    if (typeSelectionne != null && !"Tous les types".equals(typeSelectionne)) {
                        if (a.getType() == null)
                            return false;
                        if (!a.getType().getLabel().equals(typeSelectionne))
                            return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        annoncesContainer.getChildren().clear();
        displayAnnonces(filtrees);
        resultCountLabel.setText(filtrees.size() + " annonce(s) trouvée(s)");
    }

    /**
     * Affiche les annonces sous forme de cartes dans le FlowPane.
     */
    private void displayAnnonces(List<Annonce> annonces) {
        for (Annonce annonce : annonces) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/AnnonceCard.fxml"));
                VBox card = loader.load();

                AnnonceCardController controller = loader.getController();
                controller.setAnnonce(annonce);
                controller.setOnReserveClick(this::openReservationDialog);
                controller.setOnEditClick(this::onEditAnnonce);
                controller.setOnDeleteClick(this::onDeleteAnnonce);

                card.setPrefWidth(280);
                FlowPane.setMargin(card, new Insets(10));
                annoncesContainer.getChildren().add(card);
            } catch (IOException e) {
                System.err.println("Erreur chargement carte annonce: " + e.getMessage());
            }
        }
    }

    /**
     * Action : Modifier l'annonce.
     * Navigue vers le formulaire AjouterAnnonce en mode Édition.
     */
    private void onEditAnnonce(Annonce annonce) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/AjouterAnnonce.fxml"));
            Parent view = loader.load();

            AjouterAnnonceController controller = loader.getController();
            controller.setAnnonce(annonce); // Mode Modification

            StackPane contentArea = (StackPane) searchField.getScene().lookup("#contentArea");
            if (contentArea == null) {
                contentArea = (StackPane) searchField.getScene().getRoot().lookup("#contentArea");
            }
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (IOException e) {
            System.err.println("Erreur navigation vers modification : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Action : Supprimer l'annonce.
     * Demande confirmation puis supprime de la BDD.
     */
    private void onDeleteAnnonce(Annonce annonce) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression");
        alert.setHeaderText("Supprimer l'annonce ?");
        alert.setContentText(
                "Voulez-vous vraiment supprimer \"" + annonce.getTitre() + "\" ?\nCette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    annonceService.supprimer(annonce);
                    loadAnnonces(); // Rafraîchir la grille
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer : " + e.getMessage());
                }
            }
        });
    }

    /**
     * Ouvre la fenêtre modale de réservation pour une annonce.
     */
    private void openReservationDialog(Annonce annonce) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ReservationDialog.fxml"));
            Parent root = loader.load();

            ReservationDialogController controller = loader.getController();
            controller.setAnnonce(annonce);

            Stage stage = new Stage();
            stage.setTitle("Réserver - " + annonce.getTitre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Recharger après fermeture du dialog
            loadAnnonces();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir le dialogue : " + e.getMessage());
        }
    }

    /**
     * Navigation : ouvre la vue Ajouter Annonce dans le contentArea.
     */
    @FXML
    private void openAjouterAnnonce() {
        naviguerVers("/AjouterAnnonce.fxml");
    }

    /**
     * Méthode générique de navigation : charge un FXML dans le contentArea parent.
     */
    private void naviguerVers(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            StackPane contentArea = (StackPane) searchField.getScene().lookup("#contentArea");
            if (contentArea == null) {
                contentArea = (StackPane) searchField.getScene().getRoot().lookup("#contentArea");
            }
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (IOException e) {
            System.err.println("Erreur navigation vers : " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
