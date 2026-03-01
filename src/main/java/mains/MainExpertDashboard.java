package mains;

//import utils.MyConnection;
import utils.MyDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainExpertDashboard extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Connexion à la base de données
        // MyConnection.getInstance().getConnection();
        MyDatabase.getInstance().getConnection();

        // Charger le FXML de l'Expert Dashboard
        URL fxml = getClass().getResource("/ExpertHome.fxml");
        if (fxml == null) {
            throw new IllegalStateException("FXML introuvable: /ExpertHome.fxml");
        }
        FXMLLoader loader = new FXMLLoader(fxml);
        Scene scene = new Scene(loader.load());

        // Configurer la fenêtre
        stage.setTitle("AGRIFLOW - Expert Dashboard");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
