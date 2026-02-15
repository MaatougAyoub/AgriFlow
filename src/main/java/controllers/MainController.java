package controllers;

import entities.User;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private BorderPane mainContainer;
    @FXML private StackPane contentArea;

    // Drawer
    @FXML private ImageView logoImage;
    @FXML private Label connectedAsLabel;

    @FXML private Button menuProfil;
    @FXML private Button menuListeUtilisateurs;
    @FXML private Button menuMarketplace;
    @FXML private Button menuParcellesCultures;
    @FXML private Button menuCollaborations;
    @FXML private Button menuPlanIrrigation;

    // Boutons à conserver
    @FXML private Button btnAjout;
    @FXML private Button btnReservations;
    @FXML private Button btnAdmin;

    private Map<String, Object> userData;

    public void setUserData(Map<String, Object> userData) {
        this.userData = userData;
    }


    private Button activeButton;
    private static User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshDrawerUserInfo();

        // Par défaut: Marketplace
        goMarketplace();
    }

    private void loadImageInto(ImageView view, String path) {
        try {
            if (view == null) return;
            if (path == null || path.isBlank() || "null".equalsIgnoreCase(path)) {
                view.setImage(null);
                return;
            }

            String normalized = path.trim()
                    .replace("uploadssignatures", "uploads/signatures/")
                    .replace("uploadscertifications", "uploads/certifications/")
                    .replace("uploadscartes_pro", "uploads/cartes_pro/");

            if (normalized.startsWith("uploads") && !normalized.startsWith("uploads/")) {
                normalized = normalized.replaceFirst("^uploads", "uploads/");
            }

            File file = new File(System.getProperty("user.dir"), normalized);
            if (!file.exists()) file = new File(normalized);

            if (!file.exists()) {
                System.out.println("[Profil] Image introuvable: " + normalized);
                view.setImage(null);
                return;
            }

            view.setImage(new Image(file.toURI().toString(), true));
        } catch (Exception e) {
            view.setImage(null);
        }
    }



    private void refreshDrawerUserInfo() {
        if (connectedAsLabel != null) {
            connectedAsLabel.setText(currentUser != null ? currentUser.getNomComplet() : "-");
        }

        // Masquer "Liste des utilisateurs" si pas ADMIN (si ton User expose getRole())
        if (menuListeUtilisateurs != null) {
            boolean isAdmin = currentUser != null
                    && currentUser.getRole() != null
                    && "ADMIN".equalsIgnoreCase(currentUser.getRole());

            menuListeUtilisateurs.setVisible(isAdmin);
            menuListeUtilisateurs.setManaged(isAdmin);
        }
    }


    // ===== Navigation Drawer =====

    @FXML
    private void goProfil(Event event) {
        //loadView("/ProfilUtilisateur.fxml");
        //setActiveButton(menuProfil);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilUtilisateur.fxml"));
            Parent root = loader.load();

            ProfilUtilisateur profil = loader.getController();
            profil.setUserData(userData);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Profil");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goListeUtilisateurs() {
        if (!isAdmin()) {
            System.out.println("Accès refusé: ADMIN uniquement");
            return;
        }
        loadView("/ListeUtilisateurs.fxml");
        setActiveButton(menuListeUtilisateurs);
    }

    @FXML
    private void goMarketplace() {
        loadView("/Marketplace.fxml");
        setActiveButton(menuMarketplace);
    }

    @FXML
    private void goParcellesCultures() {
        // Quand tu auras la vue, change le chemin:
        // loadView("/ParcellesCultures.fxml");
        System.out.println("Parcelles et cultures - non implémenté");
        setActiveButton(menuParcellesCultures);
    }

    @FXML
    private void goCollaborations() {
        System.out.println("Collaborations - non implémenté");
        setActiveButton(menuCollaborations);
    }

    @FXML
    private void goPlanIrrigation() {
        System.out.println("Plan d'Irrigation - non implémenté");
        setActiveButton(menuPlanIrrigation);
    }

    // ===== Boutons conservés =====

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
        // si tu veux restreindre aux admins:
        // if (!isAdmin()) return;

        loadView("/AdminDashboard.fxml");
        setActiveButton(btnAdmin);
    }

    // ===== Helpers =====

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // ✅ si certains contrôleurs ont besoin du user connecté,
            // tu peux les alimenter ici au cas par cas:
            Object controller = loader.getController();
            if (controller instanceof ProfilUtilisateur profilCtrl) {
                // si ProfilUtilisateur utilise Map<String,Object>, tu dois adapter ici.
                // profilCtrl.setUserData(...);
            }
            if (controller instanceof ListeUtilisateurs usersCtrl) {
                // usersCtrl.setUserData(...);
            }

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Erreur chargement vue : " + fxmlPath);
            e.printStackTrace();
        }
    }



    private void setActiveButton(Button button) {
        if (activeButton != null) {
            // style non-actif (uniforme)
            activeButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: white;" +
                            "-fx-padding: 10 12;" +
                            "-fx-border-color: rgba(255,255,255,0.35);" +
                            "-fx-border-radius: 10;"
            );
        }

        activeButton = button;

        if (activeButton != null) {
            // style actif
            activeButton.setStyle(
                    "-fx-background-color: #1B5E20;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 10 12;" +
                            "-fx-background-radius: 10;"
            );
        }
    }

    private boolean isAdmin() {
        return currentUser != null
                && currentUser.getRole() != null
                && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    // ===== Gestion User (utilisé par ReservationDialogController etc.) =====

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