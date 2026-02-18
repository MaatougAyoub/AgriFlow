package controllers;

import entities.Agriculteur;
import entities.Expert;
import entities.Role;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceAgriculteur;
import services.ServiceExpert;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SignUp implements Initializable {

    @FXML private ComboBox<String> typeUtilisateurCombo;

    // Champs communs
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField cinField;
    @FXML private TextField emailField;
    @FXML private PasswordField motDePasseField;
    @FXML private PasswordField confirmMotDePasseField;

    // Uploads (champ non editable + bouton parcourir dans FXML)
    @FXML private TextField signaturePathField;

    // Labels feedback
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    // Champs Agriculteur
    @FXML private VBox agriculteurFieldsBox;
    @FXML private TextField adresseField;
    @FXML private TextField parcellesField;
    @FXML private TextField carteProPathField;

    // Champs Expert
    @FXML private VBox expertFieldsBox;
    @FXML private TextField certificationPathField;

    // Chemins fichiers (valeurs enregistrées en DB)
    private String signaturePath = null;
    private String carteProPath = null;
    private String certificationPath = null;

    private ServiceAgriculteur serviceAgriculteur;
    private ServiceExpert serviceExpert;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("SignUp controller loaded!");

        serviceAgriculteur = new ServiceAgriculteur();
        serviceExpert = new ServiceExpert();

        typeUtilisateurCombo.setItems(FXCollections.observableArrayList("Agriculteur", "Expert"));

        // ✅ CONTRÔLE CIN À LA SAISIE : uniquement chiffres + max 8
        cinField.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) return change;          // autoriser vide pendant saisie
            if (!newText.matches("\\d{0,8}")) return null; // 0..8 chiffres max
            return change;
        }));

        hideMessages();
        hideSpecificBoxes();
    }

    @FXML
    private void onTypeUtilisateurChange(ActionEvent event) {
        hideMessages();
        hideSpecificBoxes();

        String selectedType = typeUtilisateurCombo.getValue();
        if ("Agriculteur".equals(selectedType)) {
            setBoxVisible(agriculteurFieldsBox, true);
        } else if ("Expert".equals(selectedType)) {
            setBoxVisible(expertFieldsBox, true);
        }
    }

    // ===== Uploads =====

    @FXML
    private void uploadSignature(ActionEvent event) {
        File file = chooseFile("Sélectionner la signature");
        if (file != null) {
            signaturePath = saveFileAndReturnDbPath(file, "signatures");
            signaturePathField.setText(file.getName());
        }
    }

    @FXML
    private void uploadCartePro(ActionEvent event) {
        File file = chooseFile("Sélectionner la carte professionnelle");
        if (file != null) {
            carteProPath = saveFileAndReturnDbPath(file, "cartes_pro");
            carteProPathField.setText(file.getName());
        }
    }

    @FXML
    private void uploadCertification(ActionEvent event) {
        File file = chooseFile("Sélectionner la certification");
        if (file != null) {
            certificationPath = saveFileAndReturnDbPath(file, "certifications");
            certificationPathField.setText(file.getName());
        }
    }

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
     * Sauvegarde le fichier dans uploads/<folderName>/ et retourne un chemin DB relatif:
     * ex: uploads/signatures/1700_file.png
     */
    private String saveFileAndReturnDbPath(File file, String folderName) {
        try {
            Path uploadDir = Paths.get("uploads", folderName);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getName();
            Path targetPath = uploadDir.resolve(fileName);

            Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "uploads/" + folderName + "/" + fileName;

        } catch (IOException e) {
            showError("Erreur lors de l'upload du fichier: " + e.getMessage());
            return null;
        }
    }

    // ===== Inscription =====

    @FXML
    private void inscrireUtilisateur(ActionEvent event) {
        hideMessages();

        try {
            if (!validateFields()) return;

            String type = typeUtilisateurCombo.getValue();
            if ("Agriculteur".equals(type)) {
                inscrireAgriculteur();
            } else if ("Expert".equals(type)) {
                inscrireExpert();
            } else {
                showError("Type d'utilisateur invalide.");
            }

        } catch (Exception e) {
            showError("Erreur lors de l'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void inscrireAgriculteur() throws SQLException {
        if (adresseField.getText().trim().isEmpty()) {
            showError("L'adresse est obligatoire pour un agriculteur.");
            return;
        }
        if (carteProPath == null || carteProPath.isBlank()) {
            showError("La carte professionnelle est obligatoire.");
            return;
        }
        if (signaturePath == null || signaturePath.isBlank()) {
            showError("La signature est obligatoire.");
            return;
        }

        Agriculteur agriculteur = new Agriculteur(
                nomField.getText().trim(),
                prenomField.getText().trim(),
                Integer.parseInt(cinField.getText().trim()),
                emailField.getText().trim(),
                motDePasseField.getText(),
                Role.AGRICULTEUR.toString(),
                LocalDate.now(),
                signaturePath,
                carteProPath,
                adresseField.getText().trim(),
                parcellesField != null ? parcellesField.getText().trim() : ""
        );

        serviceAgriculteur.ajouterAgriculteur(agriculteur);

        showSuccess("Agriculteur inscrit avec succès ✅");
        clearFields();
    }

    private void inscrireExpert() throws SQLException {
        if (certificationPath == null || certificationPath.isBlank()) {
            showError("La certification est obligatoire.");
            return;
        }
        if (signaturePath == null || signaturePath.isBlank()) {
            showError("La signature est obligatoire.");
            return;
        }

        Expert expert = new Expert(
                nomField.getText().trim(),
                prenomField.getText().trim(),
                Integer.parseInt(cinField.getText().trim()),
                emailField.getText().trim(),
                motDePasseField.getText(),
                Role.EXPERT.toString(),
                LocalDate.now(),
                signaturePath,
                certificationPath
        );

        serviceExpert.ajouterExpert(expert);

        showSuccess("Expert inscrit avec succès ✅");
        clearFields();
    }

    private boolean validateFields() {
        if (typeUtilisateurCombo.getValue() == null) {
            showError("Veuillez sélectionner un type d'utilisateur.");
            return false;
        }
        if (nomField.getText().trim().isEmpty()) {
            showError("Le nom est obligatoire.");
            return false;
        }
        if (prenomField.getText().trim().isEmpty()) {
            showError("Le prénom est obligatoire.");
            return false;
        }

        // ✅ CIN: exactement 8 chiffres
        String cinText = cinField.getText() == null ? "" : cinField.getText().trim();
        if (cinText.isEmpty()) {
            showError("Le CIN est obligatoire.");
            return false;
        }
        if (!cinText.matches("\\d{8}")) {
            showError("Le CIN doit contenir exactement 8 chiffres.");
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showError("L'email est obligatoire.");
            return false;
        }
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("L'email n'est pas valide.");
            return false;
        }
        if (motDePasseField.getText().isEmpty()) {
            showError("Le mot de passe est obligatoire.");
            return false;
        }
        if (motDePasseField.getText().length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères.");
            return false;
        }
        if (!motDePasseField.getText().equals(confirmMotDePasseField.getText())) {
            showError("Les mots de passe ne correspondent pas.");
            return false;
        }

        return true;
    }

    // ===== Navigation =====

    @FXML
    private void annuler(ActionEvent event) {
        clearFields();
    }

    @FXML
    private void allerVersConnexion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignIn.fxml"));
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

    // ===== UI Helpers =====

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }

    private void hideMessages() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }

    private void setBoxVisible(VBox box, boolean visible) {
        box.setVisible(visible);
        box.setManaged(visible);
    }

    private void hideSpecificBoxes() {
        setBoxVisible(agriculteurFieldsBox, false);
        setBoxVisible(expertFieldsBox, false);
    }

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
        if (parcellesField != null) parcellesField.clear();
        carteProPathField.clear();
        certificationPathField.clear();

        signaturePath = null;
        carteProPath = null;
        certificationPath = null;

        hideSpecificBoxes();
        hideMessages();
    }
}