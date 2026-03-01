package controllers;

import entities.ProduitPhytosanitaire;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import services.ProduitService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ListeProduitsController {

    @FXML private VBox produitsContainer;
    private final ProduitService service = new ProduitService();

    @FXML
    public void initialize() {
        loadProduits();
    }

    private void loadProduits() {
        produitsContainer.getChildren().clear();
        List<ProduitPhytosanitaire> produits = service.afficherProduits();

        for (ProduitPhytosanitaire p : produits) {
            produitsContainer.getChildren().add(creerCarteProduit(p));
        }
    }

    private HBox creerCarteProduit(ProduitPhytosanitaire p) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 0));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        // Effet d'ombre sur la carte
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#00000010"));
        shadow.setRadius(10);
        shadow.setOffsetY(4);
        card.setEffect(shadow);

        // Barre décorative sur le côté gauche
        Rectangle decor = new Rectangle(5, 50, Color.web("#2D5A27"));
        decor.setArcHeight(10);
        decor.setArcWidth(10);

        // Données du produit
        Label lblNom = new Label(p.getNomProduit());
        lblNom.setPrefWidth(200);
        lblNom.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-text-fill: #2c3e50;");

        Label lblDosage = new Label(p.getDosage());
        lblDosage.setPrefWidth(120);
        lblDosage.setStyle("-fx-text-fill: #7f8c8d;");

        Label lblFreq = new Label(p.getFrequenceApplication());
        lblFreq.setPrefWidth(120);
        lblFreq.setStyle("-fx-background-color: #E8F5E9; -fx-text-fill: #2D5A27; -fx-padding: 5 10; -fx-background-radius: 15; -fx-alignment: center;");

        Label lblRemarque = new Label(p.getRemarques());
        lblRemarque.setPrefWidth(250);
        lblRemarque.setWrapText(true);
        lblRemarque.setMaxHeight(40); // Limitation 2 lignes
        lblRemarque.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Bouton supprimer moderne
        Button btnDelete = new Button("🗑");
        btnDelete.setStyle("-fx-background-color: #FDEDEC; -fx-text-fill: #E74C3C; -fx-font-size: 16; -fx-background-radius: 8; -fx-cursor: hand;");
        btnDelete.setPrefSize(40, 40);
        btnDelete.setOnAction(e -> handleSuppression(p));

        card.getChildren().addAll(decor, lblNom, lblDosage, lblFreq, lblRemarque, spacer, btnDelete);

        // Animation au survol
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f1f4f1; -fx-background-radius: 12; -fx-border-color: #2D5A27; -fx-border-radius: 12;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 12;"));

        return card;
    }

    private void handleSuppression(ProduitPhytosanitaire p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Suppression du produit");
        alert.setContentText("Voulez-vous vraiment supprimer " + p.getNomProduit() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (service.supprimerProduit(p.getIdProduit())) {
                loadProduits();
            }
        }
    }

    /** Ouvre le formulaire d'ajout d'un produit dans la zone centrale */
    @FXML
    private void ouvrirAjoutProduit(ActionEvent event) {
        ExpertHomeController ctrl = ExpertHomeController.getInstance();
        if (ctrl != null) {
            ctrl.loadCenterPage("/ajouterProduit.fxml");
        }
    }

    // --- Navigation (délégation à ExpertHomeController) ---
    @FXML
    public void goToHome(ActionEvent event) {
        ExpertHomeController ctrl = ExpertHomeController.getInstance();
        if (ctrl != null) ctrl.goToHome(null);
    }

    @FXML
    public void goToIrrigationPlan(ActionEvent event) {
        ExpertHomeController ctrl = ExpertHomeController.getInstance();
        if (ctrl != null) ctrl.goToIrrigationPlan(null);
    }

    @FXML
    public void goToDashboard(ActionEvent event) {
        ExpertHomeController ctrl = ExpertHomeController.getInstance();
        if (ctrl != null) ctrl.goToDashboard(null);
    }

    @FXML
    public void goToAjouterProduit(ActionEvent event) {
        ExpertHomeController ctrl = ExpertHomeController.getInstance();
        if (ctrl != null) ctrl.goToAjouterProduit(null);
    }

    @FXML
    public void goToReclamations(ActionEvent event) {
        ExpertHomeController ctrl = ExpertHomeController.getInstance();
        if (ctrl != null) ctrl.goToReclamations(null);
    }
}