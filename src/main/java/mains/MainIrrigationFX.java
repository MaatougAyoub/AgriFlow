package mains;
import services.*;
import entities.*;
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
import javafx.stage.Window;

import java.io.IOException;

/**
 * Point d'entrée JavaFX avec navigation entre les 6 vues
 */
public class MainIrrigationFX extends Application {

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
        primaryStage.setTitle("AgriFlow - Module Irrigations");

        // Charger la vue d'accueil (Explore Collaborations)
        showExploreIrrigations();

        primaryStage.show();
    }

    /**
     * Affiche la vue Explore Collaborations
     */
    public static void showExploreIrrigations() {
        try {
            // Get current stage dynamically if primaryStage is null
            Stage stage = primaryStage;
            if (stage == null) {
                stage = (Stage) Window.getWindows().stream()
                    .filter(Window::isShowing)
                    .findFirst()
                    .orElse(null);
            }
            if (stage == null) {
                System.err.println("No stage available for navigation");
                return;
            }
            
            // Load the main layout (drawer + content area)
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/Main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1400, 900);
            //scene.getStylesheets().add(MainFX.class.getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("AgriFlow - Explore Plans d'Irrigations");
            stage.setFullScreen(true);

            // Restore userData to the loaded MainController so drawer/profile show correctly
            Object mainControllerObj = loader.getController();
            if (mainControllerObj instanceof controllers.MainController mainCtrl) {
                mainCtrl.setUserData(controllers.MainController.getLastUserData());
            }

            // Load the ExploreCollaborations view and inject it into the main content area
            FXMLLoader exploreLoader = new FXMLLoader(MainFX.class.getResource("/palnIrrigation.fxml"));
            Parent exploreRoot = exploreLoader.load();

            Node contentNode = root.lookup("#contentArea");
            if (contentNode instanceof Pane pane) {
                pane.getChildren().setAll(exploreRoot);
            } else {
                // Fallback: set the scene directly to the explore view
                stage.setScene(new Scene(exploreRoot, 1200, 800));
            }
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de Main.fxml ou ExploreCollaborations.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

        public static void showExploreDiagnostics() {
        try {
            // Load the main layout (drawer + content area)
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/Main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1400, 900);
            //scene.getStylesheets().add(MainFX.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("AgriFlow - Explore Diagnosrics d'Irrigations");
            primaryStage.setFullScreen(true);

            // Restore userData to the loaded MainController so drawer/profile show correctly
            Object mainControllerObj = loader.getController();
            if (mainControllerObj instanceof controllers.MainController mainCtrl) {
                mainCtrl.setUserData(controllers.MainController.getLastUserData());
            }

            // Load the ExploreCollaborations view and inject it into the main content area
            FXMLLoader exploreLoader = new FXMLLoader(MainFX.class.getResource("/AgriculteurDiagnostics.fxml"));
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
   
    //Retourne le stage principal pour navigation
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
