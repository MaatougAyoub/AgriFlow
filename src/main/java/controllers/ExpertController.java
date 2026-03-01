package controllers;

import entities.Diagnostic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import services.ExpertService;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExpertController {

    @FXML private VBox listContainer;
    private final ExpertService expertService = new ExpertService();
    private Diagnostic selectedDiag = null;
    private HBox selectedRow = null;

    @FXML
    public void initialize() {
        loadDiagnostics();
    }

    private void loadDiagnostics() {
        listContainer.getChildren().clear();
        List<Diagnostic> list = expertService.getAllDiagnostics();

        for (Diagnostic d : list) {
            listContainer.getChildren().add(createModernCard(d));
        }
    }

    private HBox createModernCard(Diagnostic d) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 20, 10, 0));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-cursor: hand;");

        // Ombre portée pour l'effet "carte"
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#00000012"));
        ds.setRadius(8); ds.setOffsetY(3);
        card.setEffect(ds);

        // Barre décorative gauche
        Rectangle decor = new Rectangle(5, 50, Color.web("#2D5A27"));
        decor.setArcWidth(10); decor.setArcHeight(10);

        // 1. Image Arrondie
        ImageView iv = new ImageView();
        iv.setFitHeight(50); iv.setFitWidth(65);
        Rectangle clip = new Rectangle(65, 50);
        clip.setArcWidth(12); clip.setArcHeight(12);
        iv.setClip(clip);

        String path = d.getImagePath();
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            if (file.exists()) iv.setImage(new Image(file.toURI().toString()));
        }

        // 2. Culture
        Label lblCulture = new Label(d.getNomCulture());
        lblCulture.setPrefWidth(180);
        lblCulture.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 14;");

        // 3. Description (LIMITÉE À 2 LIGNES)
        Label lblDesc = new Label(d.getDescription());
        lblDesc.setPrefWidth(380);
        lblDesc.setWrapText(true);
        lblDesc.setMaxHeight(40); // Hauteur pour 2 lignes environ
        lblDesc.setMinHeight(40);
        lblDesc.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13;");

        // 4. Date
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        String dateStr = (d.getDateEnvoi() != null) ? d.getDateEnvoi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-";
        Label lblDate = new Label(dateStr);
        lblDate.setPrefWidth(150);
        lblDate.setAlignment(Pos.CENTER_RIGHT);
        lblDate.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12;");

        card.getChildren().addAll(decor, iv, lblCulture, lblDesc, spacer, lblDate);

        // --- ÉVÉNEMENTS ---
        card.setOnMouseClicked(event -> {
            // Reset style de l'ancienne sélection
            if (selectedRow != null) {
                selectedRow.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
                selectedRow.setEffect(ds);
            }
            // Appliquer nouvelle sélection
            selectedDiag = d;
            selectedRow = card;
            card.setStyle("-fx-background-color: #E8F5E9; -fx-background-radius: 12; -fx-border-color: #2D5A27; -fx-border-width: 1; -fx-border-radius: 12;");
            card.setEffect(null); // Enlever l'ombre quand sélectionné pour un look "pressé"

            if (event.getClickCount() == 2) {
                ouvrirDetail(d, (Stage) card.getScene().getWindow());
            }
        });

        // Effet Hover simple
        card.setOnMouseEntered(e -> {
            if (selectedRow != card) card.setStyle("-fx-background-color: #F1F4F1; -fx-background-radius: 12;");
        });
        card.setOnMouseExited(e -> {
            if (selectedRow != card) card.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        });

        return card;
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        if (selectedDiag == null) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une réclamation.");
            a.show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cette réclamation ?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().get() == ButtonType.YES) {
            expertService.supprimerDiagnostic(selectedDiag.getIdDiagnostic());
            loadDiagnostics();
            selectedDiag = null;
        }
    }

    // --- NAVIGATION (Inchangée pour vos boutons existants) ---
    private void navigateTo(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void ouvrirDetail(Diagnostic diag, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/diagnostic_detail.fxml"));
            Parent root = loader.load();
            DiagnosticDetailController controller = loader.getController();
            if (controller != null) controller.setDiagnostic(diag);
            stage.getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- NAVIGATION (délégation à ExpertHomeController) ---
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