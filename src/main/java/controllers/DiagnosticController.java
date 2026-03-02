package controllers;

import entities.Culture;
import entities.Diagnostic;
import entities.User;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import services.AIService;
import services.DiagnosticService;
import services.ServiceCulture;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class DiagnosticController {

    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<String> cultureCombo;
    @FXML
    private Label imagePathLabel;
    @FXML
    private ImageView previewImage;
    @FXML
    private ProgressIndicator aiLoader;
    @FXML
    private Button btnIA;

    private File selectedFile;
    private final AIService aiService = new AIService();
    private final DiagnosticService diagnosticService = new DiagnosticService();
    private final ServiceCulture serviceCulture = new ServiceCulture();

    @FXML
    public void initialize() {
        chargerCultures();
        aiLoader.setVisible(false);
    }

    private void chargerCultures() {
        try {
            List<Culture> cultures = serviceCulture.recupererCultures();
            cultureCombo.getItems().clear();
            for (Culture c : cultures) {
                cultureCombo.getItems().add(c.getNom());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        // On récupère la fenêtre parente pour éviter les bugs d'affichage
        Window owner = null;
        if (previewImage != null && previewImage.getScene() != null) {
            owner = previewImage.getScene().getWindow();
        }

        selectedFile = fileChooser.showOpenDialog(owner);

        if (selectedFile != null) {
            // Sécurité : mise à jour du label seulement s'il existe
            if (imagePathLabel != null) {
                imagePathLabel.setText(selectedFile.getName());
            }

            // Chargement de l'image (URI format requis par JavaFX)
            try {
                Image img = new Image(selectedFile.toURI().toString(), true);
                if (previewImage != null) {
                    previewImage.setImage(img);
                }
            } catch (Exception e) {
                System.err.println("Erreur d'affichage : " + e.getMessage());
            }
        }
    }

    @FXML
    public void analyserImageIA() {
        if (selectedFile == null) {
            showAlert("Attention", "Veuillez d'abord choisir une image.");
            return;
        }

        aiLoader.setVisible(true);
        btnIA.setDisable(true);
        descriptionArea.setText("Analyse en cours par l'IA...");

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return aiService.decrireImage(selectedFile);
            }
        };

        task.setOnSucceeded(e -> {
            descriptionArea.setText(task.getValue());
            aiLoader.setVisible(false);
            btnIA.setDisable(false);
        });

        task.setOnFailed(e -> {
            descriptionArea.setText("Erreur lors de la connexion à l'IA.");
            aiLoader.setVisible(false);
            btnIA.setDisable(false);
        });

        new Thread(task).start();
    }

    @FXML
    public void envoyerDiagnostic(ActionEvent event) {
        String culture = cultureCombo.getValue();
        String description = descriptionArea.getText();

        if (culture == null || description.isEmpty() || selectedFile == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        User currentUser = MainController.getCurrentUser();
        if (currentUser == null) {
            showAlert("Erreur", "Utilisateur non connecté. Veuillez vous reconnecter.");
            return;
        }

        Diagnostic diag = new Diagnostic();
        diag.setIdAgriculteur(currentUser.getId());
        diag.setNomCulture(culture);
        diag.setDescription(description);
        diag.setImagePath(selectedFile.getAbsolutePath());
        diag.setStatut("En attente");
        diag.setDateEnvoi(LocalDateTime.now());

        diagnosticService.ajouterDiagnostic(diag);
        showAlert("Succès", "Diagnostic envoyé à l'expert.");
        AgriculteurDiagnostics(event);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void AgriculteurDiagnostics(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AgriculteurDiagnostics.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}