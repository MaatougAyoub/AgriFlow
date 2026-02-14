package mains;

import controllers.MainController;
import entities.User;
import utils.MyDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Test connexion
            MyDatabase.getInstance().getConnection();

            // Simuler utilisateur connecté (Amenallah Jerbi — lead Marketplace)
            User currentUser = new User();
            currentUser.setId(39);
            currentUser.setNom("Jerbi");
            currentUser.setPrenom("Amenallah");
            currentUser.setCin(12345678);
            currentUser.setEmail("amenallah@agriflow.tn");
            currentUser.setRole("AGRICULTEUR");
            MainController.setCurrentUser(currentUser);

            // Charger la vue
            URL fxmlLocation = getClass().getResource("/Main.fxml");

            if (fxmlLocation == null) {
                System.err.println("ERROR: FXML file not found at /Main.fxml");
                System.err.println("Make sure the file exists in src/main/resources/");
                return;
            }

            System.out.println("Loading FXML from: " + fxmlLocation);

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            try {
                String logoPath = "/images/logo.png";
                URL logoUrl = getClass().getResource(logoPath);
                if (logoUrl != null) {
                    javafx.scene.image.Image icon = new javafx.scene.image.Image(logoUrl.toExternalForm());
                    primaryStage.getIcons().add(icon);
                }
            } catch (Exception e) {
                System.err.println("Logo error: " + e.getMessage());
            }

            primaryStage.setTitle("AGRIFLOW - Marketplace");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(700);
            primaryStage.show();

            System.out.println("AGRIFLOW Marketplace started!");

        } catch (IOException e) {
            System.err.println("ERROR loading FXML: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERROR starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        MyDatabase.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
