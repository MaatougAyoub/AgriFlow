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

import java.io.IOException;

/**
 * Point d'entrée JavaFX avec navigation entre les 6 vues
 */
public class MainExpertFX extends Application {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }
//"Produits"
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("AgriFlow - Module Irrigations(Expert)");

        // Charger la vue d'accueil (Explore Collaborations)
        showExploreExpertHome();

        primaryStage.show();
    }

    /**
     * Affiche la vue Explore Collaborations
     */
    public static void showExploreExpertHome() {
        try {
            // Load the main layout (drawer + content area)
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/Main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1400, 900);
            // scene.getStylesheets().add(MainFX.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("AgriFlow - Explore Tableau de bord Expert");
            primaryStage.setFullScreen(true);

            // Restore userData to the loaded MainController so drawer/profile show
            // correctly
            Object mainControllerObj = loader.getController();
            if (mainControllerObj instanceof controllers.MainController mainCtrl) {
                mainCtrl.setUserData(controllers.MainController.getLastUserData());
            }

            // Load the ExploreCollaborations view and inject it into the main content area
            FXMLLoader exploreLoader = new FXMLLoader(MainFX.class.getResource("/ExpertHome.fxml"));
            Parent exploreRoot = exploreLoader.load();

            Node contentNode = root.lookup("#contentArea");
            if (contentNode instanceof Pane pane) {
                pane.getChildren().setAll(exploreRoot);
            } else {
                // Fallback: set the scene directly to the explore view
                primaryStage.setScene(new Scene(exploreRoot, 1200, 800));
            }
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de Main.fxml ou ExploreExperHome.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showExploreExpertPlan() {
        try {
            // Load the main layout (drawer + content area)
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/Main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1400, 900);
            // scene.getStylesheets().add(MainFX.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("AgriFlow - Plans d'Irrigations(Expert)");
            primaryStage.setFullScreen(true);

            // Restore userData to the loaded MainController so drawer/profile show
            // correctly
            Object mainControllerObj = loader.getController();
            if (mainControllerObj instanceof controllers.MainController mainCtrl) {
                mainCtrl.setUserData(controllers.MainController.getLastUserData());
            }

            // Load the ExploreCollaborations view and inject it into the main content area
            FXMLLoader exploreLoader = new FXMLLoader(MainFX.class.getResource("/ExperpalnIrrigation.fxml"));
            Parent exploreRoot = exploreLoader.load();

            Node contentNode = root.lookup("#contentArea");
            if (contentNode instanceof Pane pane) {
                pane.getChildren().setAll(exploreRoot);
            } else {
                // Fallback: set the scene directly to the explore view
                primaryStage.setScene(new Scene(exploreRoot, 1200, 800));
            }
        } catch (IOException e) {
            System.err.println(
                    "❌ Erreur lors du chargement de Main.fxml ou ExploreExploreExpertPlan.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showExploreDashboard() {
        try {
            // Load the main layout (drawer + content area)
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/Main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1400, 900);
            // scene.getStylesheets().add(MainFX.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("AgriFlow - Dashboard(Expert)");
            primaryStage.setFullScreen(true);

            // Restore userData to the loaded MainController so drawer/profile show
            // correctly
            Object mainControllerObj = loader.getController();
            if (mainControllerObj instanceof controllers.MainController mainCtrl) {
                mainCtrl.setUserData(controllers.MainController.getLastUserData());
            }

            // Load the ExploreCollaborations view and inject it into the main content area
            FXMLLoader exploreLoader = new FXMLLoader(MainFX.class.getResource("/dashboard.fxml"));
            Parent exploreRoot = exploreLoader.load();

            Node contentNode = root.lookup("#contentArea");
            if (contentNode instanceof Pane pane) {
                pane.getChildren().setAll(exploreRoot);
            } else {
                // Fallback: set the scene directly to the explore view
                primaryStage.setScene(new Scene(exploreRoot, 1200, 800));
            }
        } catch (IOException e) {
            System.err.println(
                    "❌ Erreur lors du chargement de Main.fxml  : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showExploreListeProduits() {
        try {
            // Load the main layout (drawer + content area)
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/Main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1400, 900);
            // scene.getStylesheets().add(MainFX.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("AgriFlow - Liste Produits(Expert)");
            primaryStage.setFullScreen(true);

            // Restore userData to the loaded MainController so drawer/profile show
            // correctly
            Object mainControllerObj = loader.getController();
            if (mainControllerObj instanceof controllers.MainController mainCtrl) {
                mainCtrl.setUserData(controllers.MainController.getLastUserData());
            }

            // Load the ExploreCollaborations view and inject it into the main content area
            FXMLLoader exploreLoader = new FXMLLoader(MainFX.class.getResource("/listeProduits.fxml"));
            Parent exploreRoot = exploreLoader.load();

            Node contentNode = root.lookup("#contentArea");
            if (contentNode instanceof Pane pane) {
                pane.getChildren().setAll(exploreRoot);
            } else {
                // Fallback: set the scene directly to the explore view
                primaryStage.setScene(new Scene(exploreRoot, 1200, 800));
            }
        } catch (IOException e) {
            System.err.println(
                    "❌ Erreur lors du chargement de Main.fxml  : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showExploreExpertDashboard() {
        try {
            // Load the main layout (drawer + content area)
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/Main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1400, 900);
            // scene.getStylesheets().add(MainFX.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("AgriFlow - Expert Dashboard(Expert)");
            primaryStage.setFullScreen(true);

            // Restore userData to the loaded MainController so drawer/profile show
            // correctly
            Object mainControllerObj = loader.getController();
            if (mainControllerObj instanceof controllers.MainController mainCtrl) {
                mainCtrl.setUserData(controllers.MainController.getLastUserData());
            }

            // Load the ExploreCollaborations view and inject it into the main content area
            FXMLLoader exploreLoader = new FXMLLoader(MainFX.class.getResource("/ExpertDashboard.fxml"));
            Parent exploreRoot = exploreLoader.load();

            Node contentNode = root.lookup("#contentArea");
            if (contentNode instanceof Pane pane) {
                pane.getChildren().setAll(exploreRoot);
            } else {
                // Fallback: set the scene directly to the explore view
                primaryStage.setScene(new Scene(exploreRoot, 1200, 800));
            }
        } catch (IOException e) {
            System.err.println(
                    "❌ Erreur lors du chargement de Main.fxml  : " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Retourne le stage principal pour navigation

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
