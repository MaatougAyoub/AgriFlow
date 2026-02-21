package controllers;

import entities.Role;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceAuth;
import services.ServiceProfil;
import services.VerificationCodeEmailService;
import utils.XamppUploads;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class ModifierProfil {

    @FXML
    private Label roleLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;

    // step boxes
    @FXML
    private VBox stepEditBox;
    @FXML
    private VBox stepCodeBox;

    // commun
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;

    // fichiers
    @FXML
    private TextField signaturePathField;

    // agri
    @FXML
    private VBox agriculteurBox;
    @FXML
    private TextField adresseField;
    @FXML
    private TextField parcellesField;
    @FXML
    private TextField carteProPathField;

    // expert
    @FXML
    private VBox expertBox;
    @FXML
    private TextField certificationPathField;

    // admin
    @FXML
    private VBox adminBox;
    @FXML
    private TextField revenuField;

    // code
    @FXML
    private TextField codeField;

    private Map<String, Object> userData;
    private Map<String, Object> pendingData;
    private String generatedCode;

    // nouveaux chemins (si l'utilisateur change l'image)
    private String newSignaturePath = null;
    private String newCarteProPath = null;
    private String newCertificationPath = null;

    private final ServiceProfil serviceProfil = new ServiceProfil();

    public void setUserData(Map<String, Object> userData) {
        this.userData = userData;
        fillFormFromUserData();
    }

    private void fillFormFromUserData() {
        hideMessages();

        String roleStr = String.valueOf(userData.get("role"));
        roleLabel.setText("Rôle: " + roleStr);

        nomField.setText(safeString(userData.get("nom")));
        prenomField.setText(safeString(userData.get("prenom")));
        emailField.setText(safeString(userData.get("email")));

        signaturePathField.setText(XamppUploads.fileNameFromPath(safeString(userData.get("signature"))));
        carteProPathField.setText(XamppUploads.fileNameFromPath(safeString(userData.get("carte_pro"))));
        certificationPathField.setText(XamppUploads.fileNameFromPath(safeString(userData.get("certification"))));

        // reset "new paths"
        newSignaturePath = null;
        newCarteProPath = null;
        newCertificationPath = null;

        // hide all
        setBoxVisible(agriculteurBox, false);
        setBoxVisible(expertBox, false);
        setBoxVisible(adminBox, false);

        Role role = Role.valueOf(roleStr);
        if (role == Role.AGRICULTEUR) {
            setBoxVisible(agriculteurBox, true);
            adresseField.setText(safeString(userData.get("adresse")));
            parcellesField.setText(safeString(userData.get("parcelles")));
        } else if (role == Role.EXPERT) {
            setBoxVisible(expertBox, true);
        } else if (role == Role.ADMIN) {
            setBoxVisible(adminBox, true);
            revenuField.setText(safeString(userData.get("revenu")));
        }

        showStepEdit();
    }

    // ===== Upload handlers =====

    @FXML
    private void uploadSignature(ActionEvent event) {
        File file = chooseFile("Sélectionner une signature");
        if (file != null) {
            try {
                String absPath = XamppUploads.save(file, XamppUploads.Category.SIGNATURES);
                newSignaturePath = absPath;
                signaturePathField.setText(file.getName()); // affichage UI: nom fichier
            } catch (Exception e) {
                showError("Erreur upload signature: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void uploadCartePro(ActionEvent event) {
        File file = chooseFile("Sélectionner une carte professionnelle");
        if (file != null) {
            try {
                String absPath = XamppUploads.save(file, XamppUploads.Category.CARTES);
                newCarteProPath = absPath;
                carteProPathField.setText(file.getName());
            } catch (Exception e) {
                showError("Erreur upload carte professionnelle: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void uploadCertification(ActionEvent event) {
        File file = chooseFile("Sélectionner une certification");
        if (file != null) {
            try {
                String absPath = XamppUploads.save(file, XamppUploads.Category.CERTIFICATIONS);
                newCertificationPath = absPath;
                certificationPathField.setText(file.getName());
            } catch (Exception e) {
                showError("Erreur upload certification: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private File chooseFile(String title) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        return fc.showOpenDialog(nomField.getScene().getWindow());
    }

    // ===== Step 1: demander code =====

    @FXML
    private void demanderCode(ActionEvent event) {
        hideMessages();

        if (!validateForm())
            return;

        pendingData = collectFormData();

        generatedCode = generateSimpleCode();

        showSuccess("Envoi du code par email...");
        showStepCode();

        String email = String.valueOf(pendingData.get("email"));
        sendProfileUpdateCodeByEmailAsync(email, generatedCode);
    }

    private void sendProfileUpdateCodeByEmailAsync(String email, String code) {
        Thread t = new Thread(() -> {
            try {
                new VerificationCodeEmailService().sendProfileUpdateCode(email, code);
                Platform.runLater(() -> showSuccess("Code envoyé par email. Vérifiez votre boîte de réception."));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    generatedCode = null;
                    pendingData = null;
                    showError("Impossible d'envoyer le code par email: " + e.getMessage());
                    showStepEdit();
                });
            }
        }, "mailersend-profile-code");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void retourEdition(ActionEvent event) {
        hideMessages();
        showStepEdit();
    }

    // ===== Step 2: confirmer =====

    @FXML
    private void confirmerEtEnregistrer(ActionEvent event) {
        hideMessages();

        String code = codeField.getText() == null ? "" : codeField.getText().trim();
        if (generatedCode == null) {
            showError("Veuillez d'abord demander un code.");
            showStepEdit();
            return;
        }
        if (code.isEmpty()) {
            showError("Veuillez saisir le code.");
            return;
        }
        if (!generatedCode.equals(code)) {
            showError("Code incorrect.");
            return;
        }

        try {
            serviceProfil.updateProfil(pendingData);

            // Recharger l'utilisateur depuis DB (si email a changé)
            ServiceAuth auth = new ServiceAuth();
            Map<String, Object> refreshed = auth.loginAndFetchProfile(
                    String.valueOf(pendingData.get("email")),
                    String.valueOf(userData.get("motDePasse")));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.setUserDataAndGoToProfil(refreshed != null ? refreshed : pendingData);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Marketplace");
            stage.setScene(new Scene(root));
            //stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.show();

        } catch (Exception e) {
            showError("Erreur lors de l'enregistrement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annuler(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.setUserDataAndGoToProfil(userData);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Marketplace");
            stage.setScene(new Scene(root));
            //stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception e) {
            showError("Impossible de revenir au profil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== Helpers =====

    private boolean validateForm() {
        String nom = nomField.getText() == null ? "" : nomField.getText().trim();
        String prenom = prenomField.getText() == null ? "" : prenomField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            showError("Nom, prénom et email sont obligatoires.");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Email invalide.");
            return false;
        }

        // Uploads obligatoires
        String signatureEffective = (newSignaturePath != null) ? newSignaturePath : safeString(userData.get("signature"));
        if (signatureEffective.isBlank()) {
            showError("La signature est obligatoire.");
            return false;
        }

        Role role = Role.valueOf(String.valueOf(userData.get("role")));
        if (role == Role.AGRICULTEUR) {
            String adresse = adresseField.getText() == null ? "" : adresseField.getText().trim();
            if (adresse.isEmpty()) {
                showError("Adresse obligatoire pour l'agriculteur.");
                return false;
            }

            String carteEffective = (newCarteProPath != null) ? newCarteProPath : safeString(userData.get("carte_pro"));
            if (carteEffective.isBlank()) {
                showError("La carte professionnelle est obligatoire.");
                return false;
            }
        } else if (role == Role.EXPERT) {
            String certEffective = (newCertificationPath != null) ? newCertificationPath : safeString(userData.get("certification"));
            if (certEffective.isBlank()) {
                showError("La certification est obligatoire.");
                return false;
            }
        }
        return true;
    }

    private Map<String, Object> collectFormData() {
        Map<String, Object> m = new HashMap<>();

        m.put("id", userData.get("id"));
        m.put("role", userData.get("role"));

        m.put("nom", nomField.getText().trim());
        m.put("prenom", prenomField.getText().trim());
        m.put("email", emailField.getText().trim());

        // ✅ si l'utilisateur a uploadé une nouvelle image, utiliser newXxxPath (DB
        // path)
        // sinon garder l'ancien path depuis userData
        m.put("signature", newSignaturePath != null ? newSignaturePath : safeString(userData.get("signature")));

        Role role = Role.valueOf(String.valueOf(userData.get("role")));
        if (role == Role.AGRICULTEUR) {
            m.put("adresse", adresseField.getText() == null ? "" : adresseField.getText().trim());
            m.put("parcelles", parcellesField.getText() == null ? "" : parcellesField.getText().trim());
            m.put("carte_pro", newCarteProPath != null ? newCarteProPath : safeString(userData.get("carte_pro")));
        } else if (role == Role.EXPERT) {
            m.put("certification",
                    newCertificationPath != null ? newCertificationPath : safeString(userData.get("certification")));
        } else if (role == Role.ADMIN) {
            String rev = revenuField.getText() == null ? "" : revenuField.getText().trim();
            m.put("revenu", rev.isBlank() ? null : rev);
        }

        return m;
    }

    private void showStepEdit() {
        stepEditBox.setVisible(true);
        stepEditBox.setManaged(true);
        stepCodeBox.setVisible(false);
        stepCodeBox.setManaged(false);
        codeField.clear();
    }

    private void showStepCode() {
        stepCodeBox.setVisible(true);
        stepCodeBox.setManaged(true);
        stepEditBox.setVisible(false);
        stepEditBox.setManaged(false);
        codeField.clear();
    }

    private void setBoxVisible(VBox box, boolean visible) {
        box.setVisible(visible);
        box.setManaged(visible);
    }

    private String safeString(Object v) {
        if (v == null)
            return "";
        String s = String.valueOf(v);
        return "null".equalsIgnoreCase(s) ? "" : s;
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }

    private void showSuccess(String msg) {
        successLabel.setText(msg);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }

    private void hideMessages() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }

    private String generateSimpleCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}