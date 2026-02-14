package mains;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Point d'entrée JavaFX avec navigation entre les 6 vues
 */
public class MainFX extends Application {

    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("AgriFlow - Module Collaborations");
        
        // Charger la vue d'accueil (Explore Collaborations)
        showExploreCollaborations();
        
        primaryStage.show();
    }

    /**
     * Affiche la vue Explore Collaborations
     */
    public static void showExploreCollaborations() {
        loadView("/fxml/ExploreCollaborations.fxml", "Explore Collaborations");
    }

    /**
     * Affiche les détails d'une demande
     */
    public static void showCollabRequestDetails() {
        loadView("/fxml/CollabRequestDetails.fxml", "Détails de la demande");
    }

    /**
     * Affiche le formulaire de publication
     */
    public static void showPublishRequest() {
        loadView("/fxml/PublishRequest.fxml", "Publier une demande");
    }

    /**
     * Affiche mes demandes
     */
    public static void showMyRequests() {
        loadView("/fxml/MyRequests.fxml", "Mes demandes");
    }

    /**
     * Affiche mes candidatures
     */
    public static void showMyApplications() {
        loadView("/fxml/MyApplications.fxml", "Mes candidatures");
    }

    /**
     * Affiche l'interface admin
     */
    public static void showAdminValidation() {
        loadView("/fxml/AdminValidation.fxml", "Admin Validation");
    }

    /**
     * Charge une vue FXML
     */
    private static void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 800);
            
            // Charger le CSS
            scene.getStylesheets().add(MainFX.class.getResource("/css/styles.css").toExternalForm());
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("AgriFlow - " + title);
        } catch (IOException e) {
            System.err.println("❌ Erreur de chargement de la vue : " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Retourne le stage principal pour navigation
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
