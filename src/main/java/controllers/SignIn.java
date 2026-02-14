package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceAuth;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SignIn implements Initializable {

    @FXML private TextField emailField;
    @FXML private PasswordField motDePasseField;
    @FXML private CheckBox rememberMeCheck;

    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private Preferences prefs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prefs = Preferences.userNodeForPackage(SignIn.class);

        // Charger email mémorisé (si option activée)
        boolean remember = prefs.getBoolean("remember_enabled", false);
        String savedEmail = prefs.get("remember_email", "");

        rememberMeCheck.setSelected(remember);
        if (remember && savedEmail != null && !savedEmail.isBlank()) {
            emailField.setText(savedEmail);
        }

        hideMessages();
    }

    @FXML
    private void seConnecter(ActionEvent event) {
        hideMessages();

        String email = (emailField.getText() == null) ? "" : emailField.getText().trim();
        String mdp = (motDePasseField.getText() == null) ? "" : motDePasseField.getText();

        // Validation
        if (email.isEmpty() || mdp.isEmpty()) {
            showError("Veuillez saisir votre email et votre mot de passe.");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Email invalide.");
            return;
        }

        // Remember me
        if (rememberMeCheck.isSelected()) {
            prefs.putBoolean("remember_enabled", true);
            prefs.put("remember_email", email);
        } else {
            prefs.putBoolean("remember_enabled", false);
            prefs.remove("remember_email");
        }

        // Auth DB + ouverture du profil
        try {
            ServiceAuth auth = new ServiceAuth();
            Map<String, Object> userData = auth.loginAndFetchProfile(email, mdp);

            if (userData == null) {
                showError("Email ou mot de passe incorrect.");
                return;
            }

            // Charger la page profil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilUtilisateur.fxml")); // adapte si /fxml/
            Parent root = loader.load();

            // Passer les données au controller ProfilUtilisateur
            ProfilUtilisateur profilController = loader.getController();
            profilController.setUserData(userData);

            // Afficher dans le même Stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Profil");
            stage.setScene(new Scene(root));
            stage.show();
            //ouvrir la page en plein écran
            stage.setMaximized(true);

        } catch (Exception e) {
            showError("Erreur lors de la connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annuler(ActionEvent event) {
        hideMessages();
        emailField.clear();
        motDePasseField.clear();
        rememberMeCheck.setSelected(false);
    }

    @FXML
    private void allerVersInscription(ActionEvent event) {
        hideMessages();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUp.fxml")); // adapte si /fxml/
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Inscription");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showError("Impossible d'ouvrir la page d'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void motDePasseOublie(ActionEvent event) {
        hideMessages();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MotDePasseOublie.fxml")); // adapte si /fxml/
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Mot de passe oublié");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showError("Impossible d'ouvrir la page: " + e.getMessage());
            e.printStackTrace();
        }
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
}