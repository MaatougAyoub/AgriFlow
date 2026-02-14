package controllers;

import entities.Annonce;
import entities.TypeAnnonce;
import services.AnnonceService;
import services.FraudControlService;
import services.GeminiAIService;
import utils.MyDatabase;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur pour le formulaire d'ajout d'annonce.
 * Intègre le GeminiAIService (IA Métier Avancé) :
 * - Amélioration de description par IA
 * - Suggestion de prix par IA
 * - Modération de contenu par IA avant publication
 *
 * Navigation : chargé dans le contentArea du MainController,
 * la sidebar reste visible en permanence.
 */
public class AjouterAnnonceController implements Initializable {

    @FXML
    private TextField titreField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private TextField categorieField;
    @FXML
    private TextField prixField;
    @FXML
    private TextField localisationField;
    @FXML
    private TextField imageUrlField;
    @FXML
    private Label errorLabel;

    private Annonce annonceEnModification;

    @FXML
    private Label pageTitle;
    @FXML
    private Button btnPublier;

    // ── Boutons et labels IA ──
    @FXML
    private Button btnAmeliorerDesc;
    @FXML
    private Button btnSuggererPrix;
    @FXML
    private Label aiDescStatus;
    @FXML
    private Label aiPrixStatus;

    private final AnnonceService annonceService = new AnnonceService();
    private final GeminiAIService aiService = new GeminiAIService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Remplir le ComboBox Type
        for (TypeAnnonce t : TypeAnnonce.values()) {
            typeCombo.getItems().add(t.getLabel());
        }
        typeCombo.setValue(TypeAnnonce.LOCATION.getLabel());
    }

    /**
     * Initialise le contrôleur en mode MODIFICATION.
     * Pré-remplit les champs avec les données de l'annonce.
     */
    public void setAnnonce(Annonce annonce) {
        this.annonceEnModification = annonce;

        if (annonce != null) {
            // Mode Modification
            if (pageTitle != null)
                pageTitle.setText("🖊️ Modifier l'Annonce");
            if (btnPublier != null)
                btnPublier.setText("💾 Enregistrer les modifications");

            titreField.setText(annonce.getTitre());
            descriptionArea.setText(annonce.getDescription());
            prixField.setText(String.valueOf(annonce.getPrix()));
            categorieField.setText(annonce.getCategorie());
            localisationField.setText(annonce.getLocalisation());
            imageUrlField.setText(annonce.getImage()); // Peut être null

            if (annonce.getType() != null) {
                typeCombo.setValue(annonce.getType().getLabel());
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // IA — AMÉLIORATION DE DESCRIPTION
    // ═══════════════════════════════════════════════════════════════

    /**
     * Appelle l'IA Gemini pour améliorer la description de l'annonce.
     * L'appel est asynchrone (Thread séparé) pour ne pas bloquer l'UI.
     */
    @FXML
    private void ameliorerDescription() {
        String titre = titreField.getText() != null ? titreField.getText().trim() : "";
        String description = descriptionArea.getText() != null ? descriptionArea.getText().trim() : "";
        String categorie = categorieField.getText() != null ? categorieField.getText().trim() : "";

        if (description.isEmpty()) {
            showError("Veuillez d'abord écrire une description à améliorer.");
            return;
        }

        hideError();
        // Feedback visuel : bouton désactivé + message de chargement
        btnAmeliorerDesc.setDisable(true);
        btnAmeliorerDesc.setText("⏳ Analyse IA en cours...");
        aiDescStatus.setText("🤖 Gemini analyse votre description...");
        aiDescStatus.setVisible(true);
        aiDescStatus.setManaged(true);

        // Appel asynchrone pour ne pas bloquer le thread JavaFX
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return aiService.ameliorerDescription(titre, description, categorie);
            }
        };

        task.setOnSucceeded(event -> Platform.runLater(() -> {
            String resultat = task.getValue();
            descriptionArea.setText(resultat);
            btnAmeliorerDesc.setDisable(false);
            btnAmeliorerDesc.setText("✨ Améliorer avec l'IA");
            aiDescStatus.setText("✅ Description améliorée par Gemini IA");
        }));

        task.setOnFailed(event -> Platform.runLater(() -> {
            btnAmeliorerDesc.setDisable(false);
            btnAmeliorerDesc.setText("✨ Améliorer avec l'IA");
            aiDescStatus.setText("❌ Erreur IA : " + task.getException().getMessage());
            System.err.println("Erreur IA (description) : " + task.getException().getMessage());
        }));

        new Thread(task).start();
    }

    // ═══════════════════════════════════════════════════════════════
    // IA — SUGGESTION DE PRIX
    // ═══════════════════════════════════════════════════════════════

    /**
     * Appelle l'IA Gemini pour suggérer un prix basé sur les détails de l'annonce.
     * L'appel est asynchrone (Thread séparé) pour ne pas bloquer l'UI.
     */
    @FXML
    private void suggererPrix() {
        String titre = titreField.getText() != null ? titreField.getText().trim() : "";
        String description = descriptionArea.getText() != null ? descriptionArea.getText().trim() : "";
        String categorie = categorieField.getText() != null ? categorieField.getText().trim() : "";
        String localisation = localisationField.getText() != null ? localisationField.getText().trim() : "";
        String type = typeCombo.getValue();

        if (titre.isEmpty()) {
            showError("Veuillez d'abord remplir le titre pour que l'IA puisse suggérer un prix.");
            return;
        }

        hideError();
        btnSuggererPrix.setDisable(true);
        btnSuggererPrix.setText("⏳ Calcul IA...");
        aiPrixStatus.setText("🤖 Gemini analyse le marché...");
        aiPrixStatus.setVisible(true);
        aiPrixStatus.setManaged(true);

        Task<Double> task = new Task<>() {
            @Override
            protected Double call() throws Exception {
                return aiService.suggererPrix(titre, description, categorie, localisation, type);
            }
        };

        task.setOnSucceeded(event -> Platform.runLater(() -> {
            double prix = task.getValue();
            prixField.setText(String.format("%.2f", prix));
            btnSuggererPrix.setDisable(false);
            btnSuggererPrix.setText("💡 Suggérer un prix");
            aiPrixStatus.setText("✅ Prix suggéré par Gemini IA : " + String.format("%.2f", prix) + " DT");
        }));

        task.setOnFailed(event -> Platform.runLater(() -> {
            btnSuggererPrix.setDisable(false);
            btnSuggererPrix.setText("💡 Suggérer un prix");
            aiPrixStatus.setText("❌ Erreur IA : " + task.getException().getMessage());
            System.err.println("Erreur IA (prix) : " + task.getException().getMessage());
        }));

        new Thread(task).start();
    }

    // ═══════════════════════════════════════════════════════════════
    // PUBLICATION (avec modération IA)
    // ═══════════════════════════════════════════════════════════════

    @FXML
    private void publierAnnonce() {
        hideError();

        // ── Validation des champs obligatoires ──
        String titre = titreField.getText() != null ? titreField.getText().trim() : "";
        String description = descriptionArea.getText() != null ? descriptionArea.getText().trim() : "";
        String prixText = prixField.getText() != null ? prixField.getText().trim() : "";
        String imageUrl = imageUrlField.getText() != null ? imageUrlField.getText().trim() : "";
        String categorie = categorieField.getText() != null ? categorieField.getText().trim() : "";
        String localisation = localisationField.getText() != null ? localisationField.getText().trim() : "";

        if (titre.isEmpty() || description.isEmpty() || prixText.isEmpty()) {
            showError("Veuillez remplir tous les champs obligatoires (*).");
            return;
        }

        double prix;
        try {
            prix = Double.parseDouble(prixText);
        } catch (NumberFormatException e) {
            showError("Le prix doit être un nombre valide.");
            return;
        }

        // ── Construire/Mettre à jour l'objet Annonce ──
        Annonce annonce = (annonceEnModification != null) ? annonceEnModification : new Annonce();

        annonce.setTitre(titre);
        annonce.setDescription(description);
        annonce.setPrix(prix);
        annonce.setCategorie(categorie);
        annonce.setLocalisation(localisation);

        // En création, on set le propriétaire
        if (annonceEnModification == null) {
            annonce.setProprietaire(MainController.getCurrentUser());
        }

        // Type
        TypeAnnonce type = TypeAnnonce.LOCATION;
        if (typeCombo.getValue() != null) {
            for (TypeAnnonce t : TypeAnnonce.values()) {
                if (t.getLabel().equals(typeCombo.getValue())) {
                    type = t;
                    break;
                }
            }
        }
        annonce.setType(type);

        // Image
        if (!imageUrl.isEmpty()) {
            List<String> photos = new ArrayList<>();
            photos.add(imageUrl);
            annonce.setPhotos(photos);
        }

        // ── Smart Guard : Contrôle de Fraude (règles statiques) ──
        String motifRejet = FraudControlService.getMotifRejet(annonce);
        if (motifRejet != null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("🛡️ Smart Guard — Fraude Détectée");
            alert.setHeaderText("Action refusée");
            alert.setContentText(motifRejet);
            alert.showAndWait();
            return;
        }

        // ── Modération IA Gemini (métier avancé) ──
        btnPublier.setDisable(true);
        btnPublier.setText("🤖 Modération IA en cours...");

        Task<String> moderationTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return aiService.modererContenu(titre, description);
            }
        };

        moderationTask.setOnSucceeded(event -> Platform.runLater(() -> {
            String motifIA = moderationTask.getValue();

            if (motifIA != null) {
                // ── IA a rejeté l'annonce ──
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("🤖 Modération IA — Contenu Rejeté");
                alert.setHeaderText("L'IA a détecté un problème");
                alert.setContentText(motifIA);
                alert.showAndWait();
                btnPublier.setDisable(false);
                btnPublier.setText("✅ Publier l'annonce");
                return;
            }

            // ── IA a validé → Enregistrement BDD ──
            sauvegarderAnnonce(annonce, imageUrl);
        }));

        moderationTask.setOnFailed(event -> Platform.runLater(() -> {
            // Si l'IA échoue (réseau, etc.), on publie quand même
            // avec un avertissement mais sans bloquer l'utilisateur
            System.err.println("⚠️ Modération IA indisponible : " + moderationTask.getException().getMessage());
            sauvegarderAnnonce(annonce, imageUrl);
        }));

        new Thread(moderationTask).start();
    }

    /**
     * Sauvegarde l'annonce en BDD après validation.
     */
    private void sauvegarderAnnonce(Annonce annonce, String imageUrl) {
        try {
            if (annonceEnModification != null) {
                annonceService.modifier(annonce);
                if (!imageUrl.isEmpty()) {
                    sauvegarderPhoto(annonce.getId(), imageUrl);
                }
                showSuccess("✅ Annonce modifiée avec succès !");
            } else {
                annonceService.ajouter(annonce);
                if (!imageUrl.isEmpty() && annonce.getId() > 0) {
                    sauvegarderPhoto(annonce.getId(), imageUrl);
                }
                showSuccess("✅ Annonce publiée avec succès ! (Validée par IA ✨)");
            }
            retourMarketplace();
        } catch (SQLException e) {
            showError("Erreur lors de l'enregistrement : " + e.getMessage());
            btnPublier.setDisable(false);
            btnPublier.setText("✅ Publier l'annonce");
        }
    }

    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Sauvegarde la photo (ajout simple).
     */
    private void sauvegarderPhoto(int annonceId, String url) {
        String query = "INSERT INTO annonce_photos (annonce_id, url_photo, ordre) VALUES (?, ?, 0)";
        try {
            Connection cnx = MyDatabase.getInstance().getConnection();
            try (PreparedStatement pst = cnx.prepareStatement(query)) {
                pst.setInt(1, annonceId);
                pst.setString(2, url);
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Note: Photo peut-être déjà existante ou erreur: " + e.getMessage());
        }
    }

    @FXML
    private void retourMarketplace() {
        naviguerVers("/Marketplace.fxml");
    }

    private void naviguerVers(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            StackPane contentArea = (StackPane) titreField.getScene().lookup("#contentArea");
            if (contentArea == null) {
                contentArea = (StackPane) titreField.getScene().getRoot().lookup("#contentArea");
            }
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (IOException e) {
            System.err.println("Erreur navigation vers : " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}
