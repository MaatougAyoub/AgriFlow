package controllers;

import entities.Agriculteur;
import entities.Expert;
import entities.Role;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import services.ServiceAgriculteur;
import services.ServiceExpert;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SignUp implements Initializable {

    @FXML
    private ComboBox<String> typeUtilisateurCombo;

    // Champs communs
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField cinField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField motDePasseField;
    @FXML
    private PasswordField confirmMotDePasseField;
    @FXML
    private TextField signaturePathField;

    // Labels de feedback
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;

    // Champs Agriculteur
    @FXML
    private VBox agriculteurFieldsBox;
    @FXML
    private TextField adresseField;
    @FXML
    private TextField parcellesField;
    @FXML
    private TextField carteProPathField;

    // Champs Expert
    @FXML
    private VBox expertFieldsBox;
    @FXML
    private TextField certificationPathField;

    // Variables pour stocker les chemins des fichiers uploadés
    private String carteProPath = null;
    private String certificationPath = null;

    // Services
    private ServiceAgriculteur serviceAgriculteur;
    private ServiceExpert serviceExpert;
    private String signaturePath = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("SignUp controller loaded!");
        // Initialiser les services
        serviceAgriculteur = new ServiceAgriculteur();
        serviceExpert = new ServiceExpert();

        // Remplir le ComboBox avec les types d'utilisateurs
        typeUtilisateurCombo.setItems(FXCollections.observableArrayList("Agriculteur", "Expert"));
    }

    /**
     * Méthode appelée lorsque le type d'utilisateur change
     */
    @FXML
    private void onTypeUtilisateurChange(ActionEvent event) {
        String selectedType = typeUtilisateurCombo.getValue();

        // Cacher tous les champs spécifiques d'abord
        agriculteurFieldsBox.setVisible(false);
        agriculteurFieldsBox.setManaged(false);
        expertFieldsBox.setVisible(false);
        expertFieldsBox.setManaged(false);

        // Afficher les champs selon le type sélectionné
        if ("Agriculteur".equals(selectedType)) {
            agriculteurFieldsBox.setVisible(true);
            agriculteurFieldsBox.setManaged(true);
        } else if ("Expert".equals(selectedType)) {
            expertFieldsBox.setVisible(true);
            expertFieldsBox.setManaged(true);
        }

        // Cacher les messages d'erreur/succès
        hideMessages();
    }

    /**
     * Upload de la carte professionnelle (Agriculteur)
     */

    @FXML
    private void uploadSignature(ActionEvent event) {
        File file = chooseFile("Sélectionner la signature");
        if (file != null) {
            signaturePath = saveFile(file, "signatures");
            signaturePathField.setText(file.getName());
        }
    }
    /**
     * Upload de la carte professionnelle (Agriculteur)
     */
    @FXML
    private void uploadCartePro(ActionEvent event) {
        File file = chooseFile("Sélectionner la carte professionnelle");
        if (file != null) {
            carteProPath = saveFile(file, "cartes_pro");
            carteProPathField.setText(file.getName());
        }
    }

    /**
     * Upload de la certification (Expert)
     */
    @FXML
    private void uploadCertification(ActionEvent event) {
        File file = chooseFile("Sélectionner la certification");
        if (file != null) {
            certificationPath = saveFile(file, "certifications");
            certificationPathField.setText(file.getName());
        }
    }

    /**
     * Méthode utilitaire pour choisir un fichier
     */
    private File chooseFile(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        return fileChooser.showOpenDialog(nomField.getScene().getWindow());
    }

    /**
     * Méthode utilitaire pour sauvegarder un fichier dans un dossier spécifique
     */
    private String saveFile(File file, String folderName) {
        try {
            // Créer le dossier s'il n'existe pas
            Path uploadDir = Paths.get("uploads", folderName);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Générer un nom unique pour le fichier
            String fileName = System.currentTimeMillis() + "_" + file.getName();
            Path targetPath = uploadDir.resolve(fileName);

            // Copier le fichier
            Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return targetPath.toString();
        } catch (IOException e) {
            showError("Erreur lors de l'upload du fichier: " + e.getMessage());
            return null;
        }
    }

    /**
     * Inscrire l'utilisateur
     */
    @FXML
    private void inscrireUtilisateur(ActionEvent event) {
        hideMessages();

        try {
            // Validation des champs
            if (!validateFields()) {
                return;
            }

            String type = typeUtilisateurCombo.getValue();

            if ("Agriculteur".equals(type)) {
                inscrireAgriculteur();
            } else if ("Expert".equals(type)) {
                inscrireExpert();
            }

        } catch (Exception e) {
            showError("Erreur lors de l'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inscrire un agriculteur
     */
    private void inscrireAgriculteur() throws SQLException {
        // Validation spécifique agriculteur
        if (adresseField.getText().trim().isEmpty()) {
            showError("L'adresse est obligatoire pour un agriculteur.");
            return;
        }
        if (carteProPath == null) {
            showError("La carte professionnelle est obligatoire.");
            return;
        }

        // Créer l'objet Agriculteur
        Agriculteur agriculteur = new Agriculteur(
                nomField.getText().trim(),
                prenomField.getText().trim(),
                Integer.parseInt(cinField.getText().trim()),
                emailField.getText().trim(),
                motDePasseField.getText(),
                Role.AGRICULTEUR.toString(),
                LocalDate.now(),
                signaturePath.trim().isEmpty() ? null : signaturePath.trim(),
                carteProPath,
                adresseField.getText().trim(),
                parcellesField.getText().trim()
        );

        // Ajouter l'agriculteur via le service
        serviceAgriculteur.ajouterAgriculteur(agriculteur);

        showSuccess("Agriculteur inscrit avec succès ! ✅");
        clearFields();
    }

    /**
     * Inscrire un expert
     */
    private void inscrireExpert() throws SQLException {
        // Validation spécifique expert
        if (certificationPath == null) {
            showError("La certification est obligatoire.");
            return;
        }

        // Créer l'objet Expert
        Expert expert = new Expert(
                nomField.getText().trim(),
                prenomField.getText().trim(),
                Integer.parseInt(cinField.getText().trim()),
                emailField.getText().trim(),
                motDePasseField.getText(),
                Role.EXPERT.toString(),
                LocalDate.now(),
                signaturePath.trim().isEmpty() ? null : signaturePath.trim(),
                certificationPath
        );

        // Ajouter l'expert via le service
        serviceExpert.ajouterExpert(expert);

        showSuccess("Expert inscrit avec succès ! ✅");
        clearFields();
    }

    /**
     * Validation des champs communs
     */
    private boolean validateFields() {
        // Type d'utilisateur
        if (typeUtilisateurCombo.getValue() == null) {
            showError("Veuillez sélectionner un type d'utilisateur.");
            return false;
        }

        // Nom
        if (nomField.getText().trim().isEmpty()) {
            showError("Le nom est obligatoire.");
            return false;
        }

        // Prénom
        if (prenomField.getText().trim().isEmpty()) {
            showError("Le prénom est obligatoire.");
            return false;
        }

        // CIN
        if (cinField.getText().trim().isEmpty()) {
            showError("Le CIN est obligatoire.");
            return false;
        }
        try {
            Integer.parseInt(cinField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Le CIN doit être un nombre valide.");
            return false;
        }

        // Email
        if (emailField.getText().trim().isEmpty()) {
            showError("L'email est obligatoire.");
            return false;
        }
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("L'email n'est pas valide.");
            return false;
        }

        // Mot de passe
        if (motDePasseField.getText().isEmpty()) {
            showError("Le mot de passe est obligatoire.");
            return false;
        }
        if (motDePasseField.getText().length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères.");
            return false;
        }

        // Confirmation mot de passe
        if (!motDePasseField.getText().equals(confirmMotDePasseField.getText())) {
            showError("Les mots de passe ne correspondent pas.");
            return false;
        }

        return true;
    }

    /**
     * Annuler l'inscription
     */
    @FXML
    private void annuler(ActionEvent event) {
        clearFields();
        // TODO: Retourner à la page précédente ou fermer la fenêtre
    }

    /**
     * Aller vers la page de connexion
     */
/*    @FXML
    private void allerVersConnexion(ActionEvent event) {
        // TODO: Charger la page de connexion
        System.out.println("Redirection vers la page de connexion...");
    }*/

    @FXML
    private void allerVersConnexion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignIn.fxml")); // adapte le chemin si besoin
            Parent root = loader.load();

            // Récupérer le stage actuel depuis n'importe quel node de la scène
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setTitle("AgriFlow - Connexion");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showError("Impossible d'ouvrir la page de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Afficher un message d'erreur
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }

    /**
     * Afficher un message de succès
     */
    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }

    /**
     * Cacher les messages
     */
    private void hideMessages() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }

    /**
     * Vider tous les champs
     */
    private void clearFields() {
        typeUtilisateurCombo.setValue(null);
        nomField.clear();
        prenomField.clear();
        cinField.clear();
        emailField.clear();
        motDePasseField.clear();
        confirmMotDePasseField.clear();
        signaturePathField.clear();
        adresseField.clear();
        parcellesField.clear();
        carteProPathField.clear();
        certificationPathField.clear();
        signaturePath = null;
        carteProPath = null;
        certificationPath = null;

        agriculteurFieldsBox.setVisible(false);
        agriculteurFieldsBox.setManaged(false);
        expertFieldsBox.setVisible(false);
        expertFieldsBox.setManaged(false);
    }
}