package mains;
import services.CollabRequestService;
import entities.CollabRequest;
import javafx.scene.control.Alert;
import controllers.CollabRequestDetailsController;
import controllers.MainController;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Point d'entrée JavaFX avec navigation entre les 6 vues
 */
public class MainCollabFX extends Application {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

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
        try {
            // Load the main layout (drawer + content area)
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/Main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1400, 900);
            scene.getStylesheets().add(MainFX.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("AgriFlow - Explore Collaborations");
            primaryStage.setFullScreen(true);

            // Restore userData to the loaded MainController so drawer/profile show correctly
            Object mainControllerObj = loader.getController();
            if (mainControllerObj instanceof controllers.MainController mainCtrl) {
                mainCtrl.setUserData(controllers.MainController.getLastUserData());
            }

            // Load the ExploreCollaborations view and inject it into the main content area
            FXMLLoader exploreLoader = new FXMLLoader(MainFX.class.getResource("/fxml/ExploreCollaborations.fxml"));
            Parent exploreRoot = exploreLoader.load();

            Node contentNode = root.lookup("#contentArea");
            if (contentNode instanceof Pane pane) {
                pane.getChildren().setAll(exploreRoot);
            } else {
                // Fallback: set the scene directly to the explore view
                primaryStage.setScene(new Scene(exploreRoot, 1200, 800));
            }
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de Main.fxml ou ExploreCollaborations.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Affiche les détails d'une demande
     */
    /**
     * Afficher les détails d'une demande de collaboration
     */
    public static void showCollabRequestDetails(long requestId) {
        try {
            // Récupérer la demande depuis la base de données
            CollabRequestService service = new CollabRequestService();
            CollabRequest request = service.findById(requestId);

            if (request == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Demande introuvable");
                alert.setContentText("La demande avec l'ID " + requestId + " n'existe pas.");
                alert.showAndWait();
                return;
            }

            // Charger le FXML
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/fxml/CollabRequestDetails.fxml"));
            Parent root = loader.load();

            // Passer l'objet complet au controller
            CollabRequestDetailsController controller = loader.getController();
            controller.setRequestData(request);

            // Créer et afficher la scène
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(MainFX.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Détails de la demande");

        } catch (Exception e) {
            System.err.println("❌ Erreur de chargement de la vue : /fxml/CollabRequestDetails.fxml");
            e.printStackTrace();
        }
    }
    public static void showCollabRequestDetails(Long requestId) {
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
     * Affiche la vue des candidatures reçues pour une demande
     */
    public static void showViewApplications() {
        loadView("/fxml/ViewApplications.fxml", "Candidatures reçues");
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
            primaryStage.setFullScreen(true);
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
