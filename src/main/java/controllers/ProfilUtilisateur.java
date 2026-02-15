
package controllers;

import entities.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;

public class ProfilUtilisateur {

    @FXML private Label roleLabel;
    @FXML private Label errorLabel;

    // Drawer
    @FXML private ImageView logoImage;
    @FXML private Label connectedAsLabel;

    // Boutons profil
    @FXML private Button reclamationsButton;
    //visible seulement pour l'agriculteur
    @FXML private Button listeParcellesButton;
    //Bouton pour afficher la liste des utilisateur (seulrment pour l'admin)
    @FXML private Button menuListeUtilisateurs;

    // commun
    @FXML private Label nomValue;
    @FXML private Label prenomValue;
    @FXML private Label cinValue;
    @FXML private Label emailValue;
    @FXML private Label dateCreationValue;

    // signature
    @FXML private Label signaturePathValue;
    @FXML private ImageView signatureImage;

    // agriculteur
    @FXML private VBox agriculteurBox;
    @FXML private Label adresseValue;
    @FXML private Label parcellesValue;
    @FXML private Label carteProPathValue;
    @FXML private ImageView carteProImage;

    // expert
    @FXML private VBox expertBox;
    @FXML private Label certificationPathValue;
    @FXML private ImageView certificationImage;

    // admin
    @FXML private VBox adminBox;
    @FXML private Label revenuValue;

    private Map<String, Object> userData;

    @FXML
    public void initialize() {
        // Charger le logo (Option 1: depuis uploads/logo/logo.png)
        loadLocalLogo("uploads/logo/logo.png");

        // Option 2 (recommandée): si tu mets logo dans resources/images/logo.png:
        // logoImage.setImage(new Image(getClass().getResource("/images/logo.png").toExternalForm()));
    }

    public void setUserData(Map<String, Object> userData) {
        this.userData = userData;
        renderUser();
    }

    private void renderUser() {
        if (userData == null) return;

        errorLabel.setVisible(false);

        String roleStr = safeString(userData.get("role"));
        //-------------------------------------------------------------------------------------------------------
        // ✅ bouton liste utilisateurs visible uniquement ADMIN
        if (menuListeUtilisateurs != null) {
            boolean isAdmin = "ADMIN".equalsIgnoreCase(roleStr);
            menuListeUtilisateurs.setVisible(isAdmin);
            menuListeUtilisateurs.setManaged(isAdmin);
        }
        //---------------------------------------------------------------------------------------------------------
        roleLabel.setText("Rôle: " + roleStr);

        String nom = safeString(userData.get("nom"));
        String prenom = safeString(userData.get("prenom"));
        if (connectedAsLabel != null) {
            connectedAsLabel.setText(nom + " " + prenom);
        }

        nomValue.setText(nom);
        prenomValue.setText(prenom);
        cinValue.setText(safeString(userData.get("cin")));
        emailValue.setText(safeString(userData.get("email")));
        dateCreationValue.setText(safeString(userData.get("dateCreation")));

        String signaturePath = safeString(userData.get("signature"));
        signaturePathValue.setText(signaturePath.isBlank() ? "-" : signaturePath);
        loadImageInto(signatureImage, signaturePath);

        // hide all specifics
        setSectionVisible(agriculteurBox, false);
        setSectionVisible(expertBox, false);
        setSectionVisible(adminBox, false);

        // bouton parcelles visible uniquement agriculteur
        if (listeParcellesButton != null) {
            listeParcellesButton.setVisible(false);
            listeParcellesButton.setManaged(false);
        }

        try {
            Role role = Role.valueOf(roleStr);

            if (role == Role.AGRICULTEUR) {
                setSectionVisible(agriculteurBox, true);

                listeParcellesButton.setVisible(true);
                listeParcellesButton.setManaged(true);

                adresseValue.setText(safeString(userData.get("adresse")));
                parcellesValue.setText(safeString(userData.get("parcelles")));

                String carteProPath = safeString(userData.get("carte_pro"));
                carteProPathValue.setText(carteProPath.isBlank() ? "-" : carteProPath);
                loadImageInto(carteProImage, carteProPath);

            } else if (role == Role.EXPERT) {
                setSectionVisible(expertBox, true);

                String certPath = safeString(userData.get("certification"));
                certificationPathValue.setText(certPath.isBlank() ? "-" : certPath);
                loadImageInto(certificationImage, certPath);

            } else if (role == Role.ADMIN) {
                setSectionVisible(adminBox, true);
                revenuValue.setText(safeString(userData.get("revenu")));
            }

        } catch (Exception e) {
            errorLabel.setText("Rôle inconnu: " + roleStr);
            errorLabel.setVisible(true);
        }
    }

    private void loadLocalLogo(String relativePath) {
        try {
            File f = new File(System.getProperty("user.dir"), relativePath);
            if (!f.exists()) f = new File(relativePath);
            if (f.exists() && logoImage != null) {
                logoImage.setImage(new Image(f.toURI().toString(), true));
            }
        } catch (Exception ignored) {}
    }

    private String safeString(Object v) {
        if (v == null) return "";
        String s = String.valueOf(v);
        return "null".equalsIgnoreCase(s) ? "" : s;
    }

    private void setSectionVisible(VBox box, boolean visible) {
        box.setVisible(visible);
        box.setManaged(visible);
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

    // ========= Drawer navigation handlers (placeholders) =========

    @FXML private void goProfil(ActionEvent event) {
        // Déjà sur profil: rien à faire
    }

    @FXML
    private void goMarketplace(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
            Parent root = loader.load();

            MainController main = loader.getController(); // ✅ bon controller

            // ✅ si tu as besoin d'initialiser currentUser côté marketplace
            // (à adapter: ici je mets un placeholder si tu n'as pas encore la conversion)
            // MainController.setCurrentUser(...);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Marketplace");
            stage.setScene(new Scene(root));

            // Choisis un seul mode:
            stage.setMaximized(true);
            stage.setFullScreen(true); // optionnel (mais pas nécessaire si maximized)

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Impossible d'ouvrir Marketplace: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    @FXML private void goParcellesCultures(ActionEvent event) {
        showNotImplemented("Parcelles et cultures");
    }

    @FXML private void goCollaborations(ActionEvent event) {
        showNotImplemented("Collaborations");
    }

    @FXML private void goPlanIrrigation(ActionEvent event) {
        showNotImplemented("Plan d'Irrigation");
    }

    @FXML
    private void goListeUtilisateurs(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeUtilisateurs.fxml"));
            Parent root = loader.load();

            ListeUtilisateurs controller = loader.getController();
            controller.setUserData(userData);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Liste des utilisateurs");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Impossible d'ouvrir la liste des utilisateurs: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    private void showNotImplemented(String feature) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Information");
        a.setHeaderText(feature);
        a.setContentText("Module non implémenté pour le moment.");
        a.showAndWait();
    }

    // ========= Boutons existants =========

    @FXML
    private void reclamations(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeReclamtions.fxml"));
            Parent root = loader.load();

            ListeReclamations controller = loader.getController();
            controller.setUserData(userData);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Réclamations");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setMaximized(true);
            stage.setFullScreen(true);

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Impossible d'ouvrir la page Réclamations: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void listeParcelles(ActionEvent event) {
        System.out.println("Ouvrir liste des parcelles id=" + userData.get("id"));
    }

    @FXML
    private void modifierProfil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProfil.fxml"));
            Parent root = loader.load();

            ModifierProfil controller = loader.getController();
            controller.setUserData(userData);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Modifier Profil");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Impossible d'ouvrir la page de modification: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignIn.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Connexion");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}