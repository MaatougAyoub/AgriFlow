package mains;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/SignIn.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("AgriFlow - Connexion");
        stage.setScene(scene);
        //stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.show();

    }

    public static void main(String[] args) {
        System.out.println(System.getenv("MAILERSEND_API_KEY"));
        System.out.println(System.getenv("MAILERSEND_FROM_EMAIL"));
        launch(args);
    }
}