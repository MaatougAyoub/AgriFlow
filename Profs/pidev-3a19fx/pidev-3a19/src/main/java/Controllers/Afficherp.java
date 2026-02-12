package Controllers;

import Entites.personne;
import Services.PersonneService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class Afficherp {

    @FXML
    private TableColumn<personne,Integer> age;

    @FXML
    private TableColumn<personne,String> nom;

    @FXML
    private TableView<personne> personnetable;

    @FXML
    private TableColumn<personne,String> prenom;


    @FXML
    void initialize(){

        PersonneService ps=new PersonneService();




        ObservableList<personne> observableList= FXCollections.observableList(ps.afficherPersonne());

        personnetable.setItems(observableList);

        nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        age.setCellValueFactory(new PropertyValueFactory<>("age"));

    }

}
