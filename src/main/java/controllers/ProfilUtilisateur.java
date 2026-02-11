package controllers;

import entities.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    public void setUserData(Map<String, Object> userData) {
        this.userData = userData;
        renderUser();
    }

    private void renderUser() {
        if (userData == null) return;

        errorLabel.setVisible(false);

        String roleStr = safeString(userData.get("role"));
        roleLabel.setText("Rôle: " + roleStr);

        nomValue.setText(safeString(userData.get("nom")));
        prenomValue.setText(safeString(userData.get("prenom")));
        cinValue.setText(safeString(userData.get("cin")));
        emailValue.setText(safeString(userData.get("email")));
        dateCreationValue.setText(safeString(userData.get("dateCreation")));

        // signature image
        String signaturePath = safeString(userData.get("signature"));
        signaturePathValue.setText(signaturePath.isBlank() ? "-" : signaturePath);
        loadImageInto(signatureImage, signaturePath);

        // hide all specifics
        setSectionVisible(agriculteurBox, false);
        setSectionVisible(expertBox, false);
        setSectionVisible(adminBox, false);

        try {
            Role role = Role.valueOf(roleStr);

            if (role == Role.AGRICULTEUR) {
                setSectionVisible(agriculteurBox, true);

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

    private String safeString(Object v) {
        if (v == null) return "";
        String s = String.valueOf(v);
        return "null".equalsIgnoreCase(s) ? "" : s;
    }

    private void setSectionVisible(VBox box, boolean visible) {
        box.setVisible(visible);
        box.setManaged(visible);
    }

    /**
     * Charge une image depuis un chemin stocké en DB.
     * Gère aussi les anciens chemins cassés:
     * - uploadssignatures...
     * - uploadscertifications...
     * - uploadscartes_pro...
     */
    private void loadImageInto(ImageView view, String path) {
        try {
            if (view == null) return;

            if (path == null || path.isBlank() || "null".equalsIgnoreCase(path)) {
                view.setImage(null);
                return;
            }

            String normalized = path.trim();

            // ✅ réparer anciens formats sans slash
            normalized = normalized
                    .replace("uploadssignatures", "uploads/signatures/")
                    .replace("uploadscertifications", "uploads/certifications/")
                    .replace("uploadscartes_pro", "uploads/cartes_pro/");

            // ✅ assurer "uploads/"
            if (normalized.startsWith("uploads") && !normalized.startsWith("uploads/")) {
                normalized = normalized.replaceFirst("^uploads", "uploads/");
            }

            File file = new File(System.getProperty("user.dir"), normalized);
            if (!file.exists()) {
                file = new File(normalized);
            }

            if (!file.exists()) {
                System.out.println("[Profil] Image introuvable: " + normalized);
                view.setImage(null);
                return;
            }

            Image img = new Image(file.toURI().toString(), true);
            view.setImage(img);

        } catch (Exception e) {
            view.setImage(null);
            e.printStackTrace();
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignIn.fxml")); // adapte si /fxml/
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Connexion");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

}