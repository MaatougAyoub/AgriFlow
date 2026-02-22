package controllers;

import entities.Agriculteur;
import entities.Expert;
import entities.Role;
import javafx.application.Platform;
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
import services.VerificationCodeEmailService;
import services.ocr.TesseractOcrService;
import services.verification.ExpertCertificationVerificationService;
import services.verification.FarmerCardVerificationService;
import services.verification.VerificationResult;
import utils.XamppUploads;

import java.io.File;
import java.net.URL;
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

    // Steps (form -> code)
    @FXML private VBox stepFormBox;
    @FXML private VBox stepCodeBox;
    @FXML private TextField codeField;

    // Champs Agriculteur
    @FXML private VBox agriculteurFieldsBox;
    @FXML private TextField nomArField;
    @FXML private TextField prenomArField;
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

    // Inscription OTP (simulation)
    private String generatedCode = null;
    private String pendingUserType = null;

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

        showStepForm();
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
            try {
                signaturePath = XamppUploads.save(file, XamppUploads.Category.SIGNATURES);
                signaturePathField.setText(file.getName());
            } catch (Exception e) {
                showError("Erreur upload signature: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void uploadCartePro(ActionEvent event) {
        File file = chooseFile("Sélectionner la carte professionnelle");
        if (file != null) {
            try {
                carteProPath = XamppUploads.save(file, XamppUploads.Category.CARTES);
                carteProPathField.setText(file.getName());
            } catch (Exception e) {
                showError("Erreur upload carte professionnelle: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void uploadCertification(ActionEvent event) {
        File file = chooseFile("Sélectionner la certification");
        if (file != null) {
            try {
                certificationPath = XamppUploads.save(file, XamppUploads.Category.CERTIFICATIONS);
                certificationPathField.setText(file.getName());
            } catch (Exception e) {
                showError("Erreur upload certification: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private File chooseFile(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        return fileChooser.showOpenDialog(nomField.getScene().getWindow());
    }

    // ===== Inscription =====

    @FXML
    private void inscrireUtilisateur(ActionEvent event) {
        hideMessages();

        try {
            if (!validateFields()) return;
            if (!validateSpecificSignupFields()) return;

            pendingUserType = typeUtilisateurCombo.getValue();

            generatedCode = generateSimpleCode();

            showSuccess("Envoi du code par email...");
            showStepCode();
            sendSignupCodeByEmailAsync(emailField.getText().trim(), generatedCode);

        } catch (Exception e) {
            showError("Erreur lors de l'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendSignupCodeByEmailAsync(String email, String code) {
        Thread t = new Thread(() -> {
            try {
                new VerificationCodeEmailService().sendSignupCode(email, code);
                Platform.runLater(() -> showSuccess("Code envoyé par email. Vérifiez votre boîte de réception."));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    generatedCode = null;
                    pendingUserType = null;
                    showError("Impossible d'envoyer le code par email: " + e.getMessage());
                    showStepForm();
                });
            }
        }, "mailersend-signup-code");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void confirmerInscription(ActionEvent event) {
        hideMessages();

        String code = codeField.getText() == null ? "" : codeField.getText().trim();
        if (generatedCode == null || pendingUserType == null) {
            showError("Veuillez d'abord lancer l'inscription pour générer un code.");
            showStepForm();
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

        // invalider le code pour éviter double soumission
        generatedCode = null;

        showSuccess("Vérification OCR en cours...");

        Thread t = new Thread(() -> {
            try {
                if ("Agriculteur".equals(pendingUserType)) {
                    inscrireAgriculteurAvecVerification();
                } else if ("Expert".equals(pendingUserType)) {
                    inscrireExpertAvecVerification();
                } else {
                    Platform.runLater(() -> {
                        showError("Type d'utilisateur invalide.");
                        showStepForm();
                    });
                    return;
                }

                pendingUserType = null;
                Platform.runLater(() -> {
                    showStepForm();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Erreur lors de la confirmation: " + e.getMessage());
                    showStepForm();
                });
                e.printStackTrace();
            }
        }, "signup-ocr-verification");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void retourFormulaire(ActionEvent event) {
        hideMessages();
        // Les champs peuvent changer => on force une nouvelle génération de code
        generatedCode = null;
        pendingUserType = null;
        codeField.clear();
        showStepForm();
    }

    private void inscrireAgriculteurAvecVerification() throws SQLException {
        if (adresseField.getText().trim().isEmpty()) {
            Platform.runLater(() -> showError("L'adresse est obligatoire pour un agriculteur."));
            return;
        }
        if (carteProPath == null || carteProPath.isBlank()) {
            Platform.runLater(() -> showError("La carte professionnelle est obligatoire."));
            return;
        }
        if (signaturePath == null || signaturePath.isBlank()) {
            Platform.runLater(() -> showError("La signature est obligatoire."));
            return;
        }

        VerificationResult verification;
        try {
            FarmerCardVerificationService verifier = new FarmerCardVerificationService(new TesseractOcrService());
            verification = verifier.verify(
                    java.nio.file.Path.of(carteProPath),
                    cinField.getText().trim(),
                    nomArField.getText().trim(),
                    prenomArField.getText().trim()
            );
        } catch (Exception ocrError) {
            // OCR indisponible -> on bloque l'inscription (impossible de vérifier la carte)
            System.err.println("[AgriFlow][OCR][ERREUR] " + ocrError.getMessage());
            Platform.runLater(() -> showError(
                    "Vérification impossible. Impossible de lire la carte professionnelle.\n" +
                    "Veuillez réessayer avec une image plus nette et bien éclairée."
            ));
            return;
        }

        System.out.println(
            "[AgriFlow][VERIFY][FARMER] expectedCin=" + (cinField.getText() == null ? "" : cinField.getText().trim())
                + " extractedCin=" + (verification.extractedCin() == null ? "" : verification.extractedCin())
                + " status=" + verification.status()
                + " score=" + verification.score()
                + " reason=" + (verification.reason() == null ? "" : verification.reason())
        );

        if (verification.status() == entities.VerificationStatus.REJECTED) {
            final String reason = verification.reason();
            Platform.runLater(() -> showError("Inscription refusée: " + reason));
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

        // Arabic name (for OCR matching)
        agriculteur.setNomAr(nomArField.getText().trim());
        agriculteur.setPrenomAr(prenomArField.getText().trim());

        // Verification fields
        agriculteur.setVerificationStatus(verification.status().name());
        agriculteur.setVerificationReason(verification.reason());
        agriculteur.setVerificationScore(verification.score());

        serviceAgriculteur.ajouterAgriculteur(agriculteur);

        Platform.runLater(() -> {
            clearFields();
            if ("APPROVED".equalsIgnoreCase(agriculteur.getVerificationStatus())) {
                showSuccess("Compte vérifié et créé ✅ Vous pouvez vous connecter.");
            } else {
                showSuccess("Compte créé mais en attente de validation admin (revue) ⏳");
            }
        });
    }

    private void inscrireExpertAvecVerification() throws SQLException {
        if (certificationPath == null || certificationPath.isBlank()) {
            Platform.runLater(() -> showError("La certification est obligatoire."));
            return;
        }
        if (signaturePath == null || signaturePath.isBlank()) {
            Platform.runLater(() -> showError("La signature est obligatoire."));
            return;
        }

        VerificationResult verification;
        try {
            ExpertCertificationVerificationService verifier = new ExpertCertificationVerificationService(new TesseractOcrService());
            verification = verifier.verify(
                    java.nio.file.Path.of(certificationPath),
                    nomField.getText().trim(),
                    prenomField.getText().trim()
            );
        } catch (Exception ocrError) {
            verification = new VerificationResult(entities.VerificationStatus.NEEDS_REVIEW, 0.0, null, false,
                    "OCR indisponible: " + ocrError.getMessage());
        }

        if (verification.status() == entities.VerificationStatus.REJECTED) {
            final String reason = verification.reason();
            Platform.runLater(() -> showError("Inscription refusée: " + reason));
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

        expert.setVerificationStatus(verification.status().name());
        expert.setVerificationReason(verification.reason());
        expert.setVerificationScore(verification.score());

        serviceExpert.ajouterExpert(expert);

        Platform.runLater(() -> {
            clearFields();
            if ("APPROVED".equalsIgnoreCase(expert.getVerificationStatus())) {
                showSuccess("Compte vérifié et créé ✅ Vous pouvez vous connecter.");
            } else {
                showSuccess("Compte créé mais en attente de validation admin (revue) ⏳");
            }
        });
    }

    private boolean validateSpecificSignupFields() {
        String type = typeUtilisateurCombo.getValue();

        if ("Agriculteur".equals(type)) {
            if (nomArField.getText() == null || nomArField.getText().trim().isEmpty()) {
                showError("Le nom (arabe) est obligatoire pour vérification.");
                return false;
            }
            if (prenomArField.getText() == null || prenomArField.getText().trim().isEmpty()) {
                showError("Le prénom (arabe) est obligatoire pour vérification.");
                return false;
            }
            if (adresseField.getText() == null || adresseField.getText().trim().isEmpty()) {
                showError("L'adresse est obligatoire pour un agriculteur.");
                return false;
            }
            if (carteProPath == null || carteProPath.isBlank()) {
                showError("La carte professionnelle est obligatoire.");
                return false;
            }
            if (signaturePath == null || signaturePath.isBlank()) {
                showError("La signature est obligatoire.");
                return false;
            }
        } else if ("Expert".equals(type)) {
            if (certificationPath == null || certificationPath.isBlank()) {
                showError("La certification est obligatoire.");
                return false;
            }
            if (signaturePath == null || signaturePath.isBlank()) {
                showError("La signature est obligatoire.");
                return false;
            }
        } else {
            showError("Veuillez sélectionner un type d'utilisateur.");
            return false;
        }

        return true;
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
            //stage.setMaximized(true);
            stage.setFullScreen(true);
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
        if (nomArField != null) nomArField.clear();
        if (prenomArField != null) prenomArField.clear();
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

    private void showStepForm() {
        if (stepFormBox != null) {
            stepFormBox.setVisible(true);
            stepFormBox.setManaged(true);
        }
        if (stepCodeBox != null) {
            stepCodeBox.setVisible(false);
            stepCodeBox.setManaged(false);
        }
        if (codeField != null) {
            codeField.clear();
        }
    }

    private void showStepCode() {
        if (stepCodeBox != null) {
            stepCodeBox.setVisible(true);
            stepCodeBox.setManaged(true);
        }
        if (stepFormBox != null) {
            stepFormBox.setVisible(false);
            stepFormBox.setManaged(false);
        }
        if (codeField != null) {
            codeField.clear();
        }
    }

    private String generateSimpleCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}