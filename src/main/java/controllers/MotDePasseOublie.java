package controllers;

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

import java.net.URL;
import java.util.ResourceBundle;

public class MotDePasseOublie implements Initializable {

    @FXML private TextField emailField;

    @FXML private VBox stepEmailBox;
    @FXML private VBox stepResetBox;

    @FXML private TextField codeField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    // Simulation (tu peux le remplacer par un vrai code envoyé par email)
    private String generatedCode = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hideMessages();
        showStepEmail();
    }

    // ====== Etape 1 ======
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

        // TODO: Vérifier que l'email existe dans la DB (utilisateurs)
        // TODO: Générer un code + l'envoyer par email

        generatedCode = generateSimpleCode();

        // Pour debug (optionnel) : afficher le code dans console
        System.out.println("Code reset (debug) = " + generatedCode);

        showSuccess("Un code a été envoyé à votre email (simulation).");
        showStepReset();
    }

    @FXML
    private void renvoyerCode(ActionEvent event) {
        // Réutiliser la logique
        envoyerCode(event);
    }

    // ====== Etape 2 ======
    @FXML
    private void reinitialiserMotDePasse(ActionEvent event) {
        hideMessages();

        String code = codeField.getText() == null ? "" : codeField.getText().trim();
        String newPass = newPasswordField.getText() == null ? "" : newPasswordField.getText();
        String confirm = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();

        if (generatedCode == null) {
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

        // TODO: Mettre à jour le mot de passe dans la DB
        // Exemple attendu: UPDATE utilisateurs SET motDePasse=? WHERE email=?
        // (idéalement motDePasse hashé)

        showSuccess("Mot de passe réinitialisé avec succès (à brancher sur la DB).");

        // Option: revenir vers la connexion après succès
        // retourConnexion(event);
    }

    // ====== Navigation ======
    @FXML
    private void retourConnexion(ActionEvent event) {
        hideMessages();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignIn.fxml")); // adapte si /fxml/...
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Connexion");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showError("Impossible d'ouvrir la page de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ====== Helpers UI ======
    private void showStepEmail() {
        stepEmailBox.setVisible(true);
        stepEmailBox.setManaged(true);

        stepResetBox.setVisible(false);
        stepResetBox.setManaged(false);
    }

    private void showStepReset() {
        stepResetBox.setVisible(true);
        stepResetBox.setManaged(true);

        // On peut laisser l'étape email visible ou non.
        // Ici on la cache pour faire une vraie interface dynamique.
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
        // Code 6 chiffres
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}