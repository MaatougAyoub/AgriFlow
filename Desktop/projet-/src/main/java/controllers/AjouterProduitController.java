package controllers;

import entities.ProduitPhytosanitaire;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ProduitService;

import javax.swing.text.html.ImageView;
import java.io.IOException;

public class AjouterProduitController {

    @FXML private TextField nomField;
    @FXML private TextField dosageField;
    @FXML private TextField frequenceField;
    @FXML private TextArea remarquesField;

    private final ProduitService produitService = new ProduitService();


    @FXML
    private void ajouterProduit(ActionEvent event) {
        // Validation simple mais efficace
        if (nomField.getText().trim().isEmpty() || dosageField.getText().trim().isEmpty()) {
            nomField.setStyle("-fx-border-color: red; -fx-background-radius: 10;");
            showAlert(Alert.AlertType.WARNING, "Champs requis", "Le nom et le dosage sont obligatoires.");
            return;
        }

        try {
            ProduitPhytosanitaire p = new ProduitPhytosanitaire(
                    nomField.getText(),
                    dosageField.getText(),
                    frequenceField.getText(),
                    remarquesField.getText()
            );

            produitService.ajouterProduit(p);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit ajouté au catalogue expert.");

            // Retour automatique après succès
            goBack(event);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'enregistrer le produit.");
            e.printStackTrace();
        }
    }

    @FXML
    private void viderChamps() {
        nomField.clear();
        dosageField.clear();
        frequenceField.clear();
        remarquesField.clear();
        nomField.setStyle("-fx-background-radius: 10;");
    }

    @FXML
    private void goBack(ActionEvent event) {
        switchScene(event, "/listeProduits.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Erreur de navigation vers " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}