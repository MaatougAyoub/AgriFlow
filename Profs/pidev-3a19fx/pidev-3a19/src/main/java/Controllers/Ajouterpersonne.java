package Controllers;

import Entites.personne;
import Services.PersonneService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Ajouterpersonne {

    @FXML
    private TextField ageid;

    @FXML
    private TextField nomid;

    @FXML
    private TextField prenomid;

    @FXML
    void ajouterp(ActionEvent event) {

        PersonneService ps=new PersonneService();
        personne p=new personne(nomid.getText(),prenomid.getText(),Integer.parseInt( ageid.getText()));


        ps.ajouterPersonne(p);
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("personner ajoute");
        alert.show();
    }

    @FXML
    void nextp(ActionEvent event) {

        try {
            Parent root= FXMLLoader.load(getClass().getResource("/afficherp.fxml"));
            ageid.getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
