package controllers;

import entities.CollabRequest;
import services.CollabRequestService;
import validators.CollabRequestValidator;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.LocalDate;

/**
 * Controller pour le formulaire de création de demande
 * Gère CollabRequestForm.fxml
 */
public class CollabRequestFormController {
    
    @FXML private TextField requesterIdField;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Spinner<Integer> neededPeopleSpinner;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    
    private CollabRequestService service;
    
    @FXML
    public void initialize() {
        service = new CollabRequestService();
        
        // Configurer le Spinner (1 à 50 personnes)
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
        neededPeopleSpinner.setValueFactory(valueFactory);
        
        System.out.println("✅ CollabRequestFormController initialisé");
    }
    
    /**
     * Gère la soumission du formulaire
     */
    @FXML
    private void handleSubmit() {
        // Masquer les messages précédents
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
        
        try {
            // 1. Récupérer et valider les valeurs
            if (requesterIdField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("L'ID du demandeur est obligatoire");
            }
            
            long requesterId = Long.parseLong(requesterIdField.getText().trim());
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            
            if (startDate == null) {
                throw new IllegalArgumentException("La date de début est obligatoire");
            }
            if (endDate == null) {
                throw new IllegalArgumentException("La date de fin est obligatoire");
            }
            
            int neededPeople = neededPeopleSpinner.getValue();
            
            // 2. Créer l'objet CollabRequest
            CollabRequest request = new CollabRequest(
                requesterId, 
                title, 
                description.isEmpty() ? null : description, 
                startDate, 
                endDate, 
                neededPeople,
                "PENDING"  // Statut par défaut
            );
            
            // 3. Valider avec le validateur
            CollabRequestValidator.validate(request);
            
            // 4. Sauvegarder dans la base
            long id = service.add(request);
            
            // 5. Afficher le succès
            showSuccess("✅ Demande créée avec succès !\n\nID de la demande : " + id + "\nTitre : " + title);
            
            // 6. Réinitialiser le formulaire après 2 secondes
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> handleReset());
            pause.play();
            
        } catch (NumberFormatException e) {
            showError("❌ L'ID du demandeur doit être un nombre valide (ex: 2)");
        } catch (IllegalArgumentException e) {
            showError("❌ " + e.getMessage());
        } catch (Exception e) {
            showError("❌ Erreur lors de la création : " + e.getMessage());
        }
    }
    
    /**
     * Réinitialise le formulaire
     */
    @FXML
    private void handleReset() {
        requesterIdField.clear();
        titleField.clear();
        descriptionArea.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        neededPeopleSpinner.getValueFactory().setValue(1);
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
        
        System.out.println("🔄 Formulaire réinitialisé");
    }
    
    /**
     * Affiche un message d'erreur
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }
    
    /**
     * Affiche un message de succès
     */
    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }
}
