package mains;

import utils.MyConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainPlanIrrigation extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        MyConnection.getInstance().getConnection();

        URL fxml = getClass().getResource("/palnIrrigation.fxml");
        if (fxml == null) {
            throw new IllegalStateException("FXML introuvable: /palnIrrigation.fxml");
        }
        FXMLLoader loader = new FXMLLoader(fxml);
        Scene scene = new Scene(loader.load());
        stage.setTitle("AGRIFLOW - Plan Irrigation");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}