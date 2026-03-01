package controllers;

import entities.Diagnostic;
import entities.ProduitPhytosanitaire;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mains.MainExpertFX;
import services.ExpertService;
import services.ProduitService;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DiagnosticDetailController {

    @FXML private Label lblCulture;
    @FXML private Label txtDescriptionDisplay; // Changez Text en Label
    @FXML private ImageView imgCulture;
    @FXML private TextArea txtReponseExpert;


    @FXML private ComboBox<ProduitPhytosanitaire> comboProduits;

    private Diagnostic diagnostic;
    private final ExpertService expertService = new ExpertService();
    private final ProduitService produitService = new ProduitService();

    @FXML
    public void initialize() {
        configurerComboBox();
        chargerProduits();

        // Optionnel : lier la largeur du texte à la fenêtre pour le mode plein écran
        if (txtDescriptionDisplay != null) {
            txtDescriptionDisplay.minWidthProperty().bind(
                    ((VBox)txtDescriptionDisplay.getParent()).widthProperty().subtract(20));
        }
    }


    private void configurerComboBox() {
        comboProduits.setCellFactory(lv -> new ListCell<ProduitPhytosanitaire>() {
            @Override
            protected void updateItem(ProduitPhytosanitaire item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNomProduit());
            }
        });
        comboProduits.setButtonCell(new ListCell<ProduitPhytosanitaire>() {
            @Override
            protected void updateItem(ProduitPhytosanitaire item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNomProduit());
            }
        });
    }

    private void chargerProduits() {
        // Supposant que ProduitService possède une méthode getAll()
        List<ProduitPhytosanitaire> liste = produitService.getAll();
        comboProduits.setItems(FXCollections.observableArrayList(liste));
    }

    public void setDiagnostic(Diagnostic diagnostic) {
        this.diagnostic = diagnostic;
        afficherDiagnostic();
    }

    private void afficherDiagnostic() {
        if (diagnostic != null) {
            lblCulture.setText(diagnostic.getNomCulture());

            txtDescriptionDisplay.setText(diagnostic.getDescription() != null ?
                    diagnostic.getDescription() : "Aucune description fournie.");

            String path = diagnostic.getImagePath();
            if (path != null && !path.isEmpty()) {
                File file = new File(path);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imgCulture.setImage(image);

                    // Correction du clip pour le rendu arrondi
                    double w = (imgCulture.getFitWidth() > 0) ? imgCulture.getFitWidth() : 280;
                    double h = (imgCulture.getFitHeight() > 0) ? imgCulture.getFitHeight() : 200;

                    Rectangle clip = new Rectangle(w, h);
                    clip.setArcWidth(20); clip.setArcHeight(20);
                    imgCulture.setClip(clip);
                }
            }

            if(diagnostic.getReponseExpert() != null) {
                txtReponseExpert.setText(diagnostic.getReponseExpert());
            }
        }
    }

    @FXML
    private void ajouterProduitAReponse(ActionEvent event) {
        ProduitPhytosanitaire produit = comboProduits.getValue();

        if (produit != null) {
            // Création d'un bloc de texte structuré avec les infos de l'entité
            StringBuilder sb = new StringBuilder();
            sb.append("\n--- PRODUIT RECOMMANDÉ ---\n");
            sb.append("Nom : ").append(produit.getNomProduit()).append("\n");
            sb.append("Dosage : ").append(produit.getDosage()).append("\n");
            sb.append("Fréquence : ").append(produit.getFrequenceApplication()).append("\n");
            if (produit.getRemarques() != null && !produit.getRemarques().isEmpty()) {
                sb.append("Note : ").append(produit.getRemarques()).append("\n");
            }
            sb.append("---------------------------\n");

            txtReponseExpert.appendText(sb.toString());
        } else {
            showAlert("Attention", "Veuillez d'abord sélectionner un produit dans la liste.");
        }
    }

    @FXML
    private void enregistrerReponse(ActionEvent event) {
        String reponse = txtReponseExpert.getText();
        if (reponse == null || reponse.trim().isEmpty()) {
            showAlert("Erreur", "La réponse ne peut pas être vide.");
            return;
        }

        expertService.repondreDiagnostic(diagnostic.getIdDiagnostic(), reponse);
        showAlert("Succès", "Réponse envoyée avec succès.");
        goBack();
    }

/*     @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ExpertDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */



    @FXML
    private void goBack() {
        MainExpertFX.showExploreExpertHome();
    }

        @FXML
    private void retour() {
        MainExpertFX.showExploreExpertHome();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}