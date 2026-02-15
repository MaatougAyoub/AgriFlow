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

// Controleur principal - yger la navigation bin les vues (Marketplace, Ajout, Reservations, Admin)
// el sidebar m3a les boutons kenou houni, w el contenu yetbadel fl contentArea
public class MainController implements Initializable {

    // @FXML = houni JavaFX yrabat el element mel FXML (fichier XML mta3 el interface)
    @FXML
    private BorderPane mainContainer; // el layout principal (sidebar + contenu)
    @FXML
    private Label userNameLabel; // esm el user fl sidebar
    @FXML
    private StackPane contentArea; // el zone win ncharjou les vues

    @FXML
    private Button btnMarketplace;
    @FXML
    private Button btnAjout;
    @FXML
    private Button btnReservations;
    @FXML
    private Button btnAdmin;

    private Button activeButton; // le bouton actif fl sidebar (pour le style)
    private static User currentUser; // el user connecte (static bech nwsloulou mel controllers lokhrin)

    // initialize() = tetna3da automatiquement ki el FXML yet5arj
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getNomComplet());
        }
        // par defaut, n7ammlou la vue Marketplace
        afficherMarketplace();
    }

    // ===== NAVIGATION : kol methode t7amel vue FXML mokhtalfa =====

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

    // ===== Helpers =====

    // n7ammlou el fichier FXML w n7attoueh fl contentArea (zone mta3 el contenu)
    private void loadView(String fxmlPath) {
        try {
            // FXMLLoader y9ra el fichier FXML w yraj3a l interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            // nbadlou el contenu fl StackPane
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Erreur chargement vue : " + fxmlPath);
            e.printStackTrace();
        }
    }

    // nbadlou el style mta3 el bouton actif (bech el user ychouf winou houwa)
    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("nav-button-active");
        }
        activeButton = button;
        if (activeButton != null) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    // ===== Gestion User (static bech n9raw min n7eb men ay controller) =====

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
