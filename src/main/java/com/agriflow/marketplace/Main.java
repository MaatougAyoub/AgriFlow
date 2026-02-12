package com.agriflow.marketplace;

import com.agriflow.marketplace.controllers.MainController;
import com.agriflow.marketplace.models.User;
import com.agriflow.marketplace.utils.MyDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Test connexion
            MyDatabase.getInstance().getCnx();

            // Simuler utilisateur connecté
            User currentUser = new User();
            currentUser.setId(1);
            currentUser.setNom("Jerbi");
            currentUser.setPrenom("Amenallah");
            currentUser.setEmail("amenallah@agriflow.tn");
            currentUser.setTelephone("+216 20 123 456");
            currentUser.setRegion("Sousse");
            MainController.setCurrentUser(currentUser);

            // Charger la vue avec chemin absolu
            URL fxmlLocation = getClass().getResource("/com/agriflow/marketplace/views/Main.fxml");

            if (fxmlLocation == null) {
                System.err.println("ERROR: FXML file not found at /com/agriflow/marketplace/views/Main.fxml");
                System.err.println("Make sure the file exists in src/main/resources/com/agriflow/marketplace/views/");
                return;
            }

            System.out.println("Loading FXML from: " + fxmlLocation);

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            // Ajouter l'icône de l'application
            try {
                // Charge depuis /com/agriflow/marketplace/images/logo.png
                String logoPath = "/com/agriflow/marketplace/images/logo.png";
                URL logoUrl = getClass().getResource(logoPath);
                if (logoUrl != null) {
                    javafx.scene.image.Image icon = new javafx.scene.image.Image(logoUrl.toExternalForm());
                    primaryStage.getIcons().add(icon);
                } else {
                    System.err.println("⚠️ Logo introuvable : " + logoPath);
                }
            } catch (Exception e) {
                System.err.println("⚠️ Erreur chargement icône : " + e.getMessage());
            }

            primaryStage.setTitle("AGRIFLOW - Marketplace");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(700);
            primaryStage.show();

            System.out.println("AGRIFLOW Marketplace démarré!");

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
