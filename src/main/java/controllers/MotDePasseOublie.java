package controllers;

import entities.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ServiceAgriculteur;
import services.ServiceExpert;
import utils.MyDatabase;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MotDePasseOublie implements Initializable {

    @FXML
    private TextField emailField;
    @FXML
    private VBox stepEmailBox;
    @FXML
    private VBox stepResetBox;

    @FXML
    private TextField codeField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;

    private String generatedCode = null;
    private String emailCible = null;
    private Role roleCible = null;

    private ServiceAgriculteur serviceAgriculteur;
    private ServiceExpert serviceExpert;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceAgriculteur = new ServiceAgriculteur();
        serviceExpert = new ServiceExpert();

        hideMessages();
        showStepEmail();
    }

    // ===== Étape 1 =====
    @FXML
    private void envoyerCode(ActionEvent event) {
        hideMessages();

        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        if (email.isEmpty()) {
            showError("Veuillez saisir votre email.");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Email invalide.");
            return;
        }

        try {
            // 1) déterminer le rôle (AGRICULTEUR ou EXPERT) depuis table utilisateurs
            Role role = findRoleByEmail(email);
            if (role == null) {
                showError("Aucun compte trouvé avec cet email.");
                return;
            }
            if (role != Role.AGRICULTEUR && role != Role.EXPERT) {
                showError("La réinitialisation est disponible uniquement pour Agriculteur/Expert.");
                return;
            }

            // 2) garder l'état
            this.emailCible = email;
            this.roleCible = role;

            // 3) générer code (simulation)
            generatedCode = generateSimpleCode();
            System.out.println("Code reset (debug) = " + generatedCode);

            showSuccess("Code envoyé (simulation). Vérifiez votre email.");
            showStepReset();

        } catch (SQLException e) {
            showError("Erreur DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void renvoyerCode(ActionEvent event) {
        envoyerCode(event);
    }

    // ===== Étape 2 =====
    @FXML
    private void reinitialiserMotDePasse(ActionEvent event) {
        hideMessages();

        String code = codeField.getText() == null ? "" : codeField.getText().trim();
        String newPass = newPasswordField.getText() == null ? "" : newPasswordField.getText();
        String confirm = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();

        if (emailCible == null || roleCible == null || generatedCode == null) {
            showError("Veuillez d'abord demander un code.");
            showStepEmail();
            return;
        }

        if (code.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        if (!generatedCode.equals(code)) {
            showError("Code incorrect.");
            return;
        }

        if (newPass.length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        if (!newPass.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas.");
            return;
        }

        try {
            // Mettre à jour selon rôle
            if (roleCible == Role.AGRICULTEUR) {
                serviceAgriculteur.modifierMotDePasseParEmail(emailCible, newPass);
            } else if (roleCible == Role.EXPERT) {
                serviceExpert.modifierMotDePasseParEmail(emailCible, newPass);
            }

            showSuccess("Mot de passe réinitialisé avec succès ✅");
            // Optionnel: revenir vers SignIn
            // retourConnexion(event);

        } catch (SQLException e) {
            showError("Erreur lors de la mise à jour du mot de passe: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== Navigation =====
    @FXML
    private void retourConnexion(ActionEvent event) {
        hideMessages();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignIn.fxml")); // adapte si /fxml/...
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Connexion");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            showError("Impossible d'ouvrir la page de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== Helpers DB =====
    private Role findRoleByEmail(String email) throws SQLException {
        Connection cnx = utils.MyDatabase.getInstance().getConnection(); // ne pas fermer

        String sql = "SELECT role FROM utilisateurs WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                return Role.valueOf(rs.getString("role"));
            }
        }
    }

    // ===== Helpers UI =====
    private void showStepEmail() {
        stepEmailBox.setVisible(true);
        stepEmailBox.setManaged(true);
        stepResetBox.setVisible(false);
        stepResetBox.setManaged(false);
    }

    private void showStepReset() {
        stepResetBox.setVisible(true);
        stepResetBox.setManaged(true);
        stepEmailBox.setVisible(false);
        stepEmailBox.setManaged(false);
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