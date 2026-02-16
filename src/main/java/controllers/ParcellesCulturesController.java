package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class ParcellesCulturesController {

    // IMPORTANT: root DOIT être l'élément racine du FXML (fx:id="root")
    @FXML private BorderPane root;

    @FXML
    private void openGestionParcelles() {
        naviguerVers("/ListeParcelles.fxml");
    }

    @FXML
    private void openGestionCultures() {
        naviguerVers("/ListeCultures.fxml");
    }

    @FXML
    private void openCulturesEnVente() {
        naviguerVers("/CulturesEnVente.fxml");
    }

    @FXML
    private void retourHome() {
        // si ton home = Marketplace
        naviguerVers("/Marketplace.fxml");
    }

    private void naviguerVers(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Récupérer contentArea (StackPane) depuis la Scene principale
            if (root == null || root.getScene() == null) {
                System.err.println("[ParcellesCultures] root ou scene null -> vue pas attachée");
                return;
            }

            StackPane contentArea = (StackPane) root.getScene().lookup("#contentArea");
            if (contentArea == null) {
                // fallback: si contentArea est sur root principal d'une autre manière
                Parent sceneRoot = root.getScene().getRoot();
                if (sceneRoot != null) {
                    contentArea = (StackPane) sceneRoot.lookup("#contentArea");
                }
            }

            if (contentArea == null) {
                System.err.println("[ParcellesCultures] contentArea introuvable (#contentArea).");
                return;
            }

            contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            System.err.println("[ParcellesCultures] Erreur navigation vers " + fxmlPath + " : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
