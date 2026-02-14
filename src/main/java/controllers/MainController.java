package controllers;

import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur principal de l'application AgriFlow.
 * Gère la navigation entre les vues via la sidebar.
 */
public class MainController implements Initializable {

    @FXML
    private BorderPane mainContainer;
    @FXML
    private Label userNameLabel;
    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnMarketplace;
    @FXML
    private Button btnAjout;
    @FXML
    private Button btnReservations;
    @FXML
    private Button btnAdmin;

    private Button activeButton;
    private static User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getNomComplet());
        }
        // Charger la vue Marketplace par défaut
        afficherMarketplace();
    }

    // ─── Navigation ────────────────────────────────────────────

    @FXML
    public void afficherMarketplace() {
        loadView("/Marketplace.fxml");
        setActiveButton(btnMarketplace);
    }

    @FXML
    public void afficherAjout() {
        loadView("/AjouterAnnonce.fxml");
        setActiveButton(btnAjout);
    }

    @FXML
    public void afficherReservations() {
        loadView("/MesReservations.fxml");
        setActiveButton(btnReservations);
    }

    @FXML
    public void afficherAdmin() {
        loadView("/AdminDashboard.fxml");
        setActiveButton(btnAdmin);
    }

    // ─── Helpers ───────────────────────────────────────────────

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Erreur chargement vue : " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Met à jour le style du bouton actif dans la sidebar.
     */
    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("nav-button-active");
        }
        activeButton = button;
        if (activeButton != null) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    // ─── Gestion Utilisateur ───────────────────────────────────

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }
}
