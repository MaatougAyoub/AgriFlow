package controllers;

import entities.User;
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

    @FXML
    private BorderPane mainContainer;
    @FXML
    private StackPane contentArea;

    // Drawer
    @FXML
    private ImageView logoImage;
    @FXML
    private Label connectedAsLabel;
    @FXML
    private Label connectedEmailLabel;
    @FXML
    private Label connectedRoleLabel;
    @FXML
    private Button btnDeconnexion;

    @FXML
    private Button menuProfil;
    @FXML
    private Button menuListeUtilisateurs;
    @FXML
    private Button menuMarketplace;
    @FXML
    private Button menuParcellesCultures;
    @FXML
    private Button menuCollaborations;
    @FXML
    private Button menuPlanIrrigation;

    // Boutons à conserver
    @FXML
    private Button btnAjout;
    @FXML
    private Button btnReservations;
    @FXML
    private Button btnAdmin;

    private Map<String, Object> userData;
    private boolean navigateToProfile = false;
    // Persist last user data across controller instances
    private static java.util.Map<String, Object> lastUserData;

    public void setUserData(Map<String, Object> userData) {
        this.userData = userData;
        // Rafraîchir le drawer avec le nom de l'utilisateur connecté
        refreshDrawerUserInfo();
        // Si on doit naviguer vers le profil après init
        if (navigateToProfile) {
            navigateToProfile = false;
            goProfil();
        }
    }

    public static void setLastUserData(java.util.Map<String, Object> data) {
        lastUserData = data;
    }

    public static java.util.Map<String, Object> getLastUserData() {
        return lastUserData;
    }

    /**
     * Utilisé par retourProfil dans d'autres controllers pour revenir au Main +
     * afficher le profil
     */
    public void setUserDataAndGoToProfil(Map<String, Object> userData) {
        this.navigateToProfile = true;
        setUserData(userData);
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
            if (view == null)
                return;
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
            if (!file.exists())
                file = new File(normalized);

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
        boolean admin = isAdmin();

        // Nom complet
        if (connectedAsLabel != null) {
            connectedAsLabel.setText(currentUser != null ? currentUser.getNomComplet() : "-");
        }

        // Email
        if (connectedEmailLabel != null) {
            connectedEmailLabel
                    .setText(currentUser != null && currentUser.getEmail() != null ? currentUser.getEmail() : "");
        }

        // Role (avec emoji)
        if (connectedRoleLabel != null) {
            if (currentUser != null && currentUser.getRole() != null) {
                String role = currentUser.getRole();
                switch (role.toUpperCase()) {
                    case "ADMIN":
                        connectedRoleLabel.setText("\uD83D\uDC51 Administrateur");
                        connectedRoleLabel
                                .setStyle("-fx-text-fill: #FFD54F; -fx-font-weight: bold; -fx-font-size: 11px;");
                        break;
                    case "EXPERT":
                        connectedRoleLabel.setText("\uD83C\uDF93 Expert");
                        connectedRoleLabel
                                .setStyle("-fx-text-fill: #90CAF9; -fx-font-weight: bold; -fx-font-size: 11px;");
                        break;
                    default:
                        connectedRoleLabel.setText("\uD83C\uDF3E Agriculteur");
                        connectedRoleLabel
                                .setStyle("-fx-text-fill: #A5D6A7; -fx-font-weight: bold; -fx-font-size: 11px;");
                        break;
                }
            } else {
                connectedRoleLabel.setText("");
            }
        }

        // Masquer "Liste des utilisateurs" et "Espace Admin" si pas ADMIN
        if (menuListeUtilisateurs != null) {
            menuListeUtilisateurs.setVisible(admin);
            menuListeUtilisateurs.setManaged(admin);
        }
        if (btnAdmin != null) {
            btnAdmin.setVisible(admin);
            btnAdmin.setManaged(admin);
        }
    }

    // ===== Navigation Drawer =====

    @FXML
    private void goProfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilUtilisateur.fxml"));
            Parent view = loader.load();

            ProfilUtilisateur profil = loader.getController();
            profil.setUserData(userData);

            contentArea.getChildren().setAll(view);
            setActiveButton(menuProfil);
        } catch (Exception e) {
            System.err.println("Erreur chargement profil : " + e.getMessage());
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
        loadView("/ParcellesCultures.fxml");
        setActiveButton(menuParcellesCultures);
    }

    @FXML
    private void goCollaborations() {
        loadView("/fxml/ExploreCollaborations.fxml");
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
        if (!isAdmin()) {
            System.out.println("Accès refusé: ADMIN uniquement");
            return;
        }
        loadView("/AdminDashboard.fxml");
        setActiveButton(btnAdmin);
    }

    @FXML
    private void deconnecter() {
        currentUser = null;
        userData = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignIn.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setTitle("AgriFlow - Connexion");
            stage.setScene(new Scene(root));
            //stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur déconnexion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== Helpers =====

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // ✅ si certains contrôleurs ont besoin du user connecté,
            // tu peux les alimenter ici au cas par cas:
            Object controller = loader.getController();
            if (controller instanceof controllers.ExploreCollaborationsController exploreCtrl) {
                // Passer les données utilisateur si la vue ExploreCollaborations en a besoin
                exploreCtrl.setUserData(userData);
            }
            if (controller instanceof ProfilUtilisateur profilCtrl) {
                // si ProfilUtilisateur utilise Map<String,Object>, tu dois adapter ici.
                // profilCtrl.setUserData(...);
            }
            if (controller instanceof ListeUtilisateurs usersCtrl) {
                usersCtrl.setUserData(userData);
            }

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Erreur chargement vue : " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button button) {
        final String common =
                "-fx-font-size: 20;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 12;" +
                        "-fx-background-insets: 0;" +
                        "-fx-border-insets: 0;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;";

        if (activeButton != null) {
            // style non-actif (uniforme)
            activeButton.setStyle(
                    common +
                            "-fx-font-weight: normal;" +
                            "-fx-background-color: transparent;" +
                            "-fx-border-color: rgba(255,255,255,0.35);" +
                            "-fx-border-width: 1;");
        }

        activeButton = button;

        if (activeButton != null) {
            // style actif
            activeButton.setStyle(
                    common +
                            "-fx-font-weight: bold;" +
                            "-fx-background-color: #1B5E20;" +
                            "-fx-border-color: transparent;" +
                            "-fx-border-width: 1;");
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
