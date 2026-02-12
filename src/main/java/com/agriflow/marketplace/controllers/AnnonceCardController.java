package com.agriflow.marketplace.controllers;

import com.agriflow.marketplace.models.Annonce;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Contrôleur pour les cartes d'annonces dans la Marketplace.
 * Gère l'affichage de l'image avec fallback sur un placeholder coloré par
 * catégorie.
 */
public class AnnonceCardController implements Initializable {

    @FXML
    private VBox cardContainer;
    @FXML
    private ImageView annonceImageView;
    @FXML
    private VBox imagePlaceholder;
    @FXML
    private Label placeholderIcon;
    @FXML
    private Label placeholderText;
    @FXML
    private Label typeLabel;
    @FXML
    private Circle statutIndicator;
    @FXML
    private Label titreLabel;
    @FXML
    private Label categorieLabel;
    @FXML
    private Label localisationLabel;
    @FXML
    private Label prixLabel;
    @FXML
    private Label proprietaireLabel;
    @FXML
    private Button reserverBtn;

    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    private Annonce annonce;
    private Consumer<Annonce> onReserveClick;
    private Consumer<Annonce> onEditClick;
    private Consumer<Annonce> onDeleteClick;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setAnnonce(Annonce annonce) {
        this.annonce = annonce;
        updateCard();
    }

    public void setOnReserveClick(Consumer<Annonce> callback) {
        this.onReserveClick = callback;
    }

    public void setOnEditClick(Consumer<Annonce> callback) {
        this.onEditClick = callback;
    }

    public void setOnDeleteClick(Consumer<Annonce> callback) {
        this.onDeleteClick = callback;
    }

    private void updateCard() {
        if (annonce == null)
            return;

        // === IMAGE ===
        chargerImage();

        // === TYPE BADGE ===
        if (annonce.getType() != null) {
            typeLabel.setText(annonce.getType().getLabel().toUpperCase());
        }

        // === STATUT ===
        if (annonce.getStatut() != null) {
            statutIndicator.setFill(Color.web(annonce.getStatut().getCouleur()));
        }

        // === TITRE ===
        String titre = annonce.getTitre() != null ? annonce.getTitre() : "";
        if (titre.length() > 35)
            titre = titre.substring(0, 32) + "...";
        titreLabel.setText(titre);

        // === CATÉGORIE ===
        categorieLabel.setText(annonce.getCategorie() != null ? annonce.getCategorie() : "");

        // === LOCALISATION ===
        localisationLabel.setText(annonce.getLocalisation() != null ? annonce.getLocalisation() : "N/A");

        // === PRIX ===
        prixLabel.setText(annonce.getPrixFormate());

        // === PROPRIÉTAIRE & BOUTONS ===
        boolean isOwner = false;
        if (annonce.getProprietaire() != null) {
            proprietaireLabel.setText(annonce.getProprietaire().getNomComplet());
            // Vérifier si l'utilisateur courant est le propriétaire
            if (MainController.getCurrentUser() != null &&
                    MainController.getCurrentUser().getId() == annonce.getProprietaire().getId()) {
                isOwner = true;
            }
        } else {
            proprietaireLabel.setText("Inconnu");
        }

        // Afficher/Masquer boutons Edit/Delete
        if (isOwner) {
            btnEdit.setVisible(true);
            btnEdit.setManaged(true);
            btnDelete.setVisible(true);
            btnDelete.setManaged(true);
            reserverBtn.setVisible(false); // Pas de réservation pour son propre bien
            reserverBtn.setManaged(false);
        } else {
            btnEdit.setVisible(false);
            btnEdit.setManaged(false);
            btnDelete.setVisible(false);
            btnDelete.setManaged(false);
            reserverBtn.setVisible(true);
            reserverBtn.setManaged(true);
        }
    }

    /**
     * Charge l'image de l'annonce.
     */
    private void chargerImage() {
        String imagePath = annonce.getImage();

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            try {
                Image image;
                if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
                    image = new Image(imagePath, 280, 160, false, true, true);
                } else if (imagePath.startsWith("file:")) {
                    image = new Image(imagePath, 280, 160, false, true);
                } else {
                    image = new Image("file:///" + imagePath, 280, 160, false, true);
                }

                if (image.isError()) {
                    afficherPlaceholder();
                } else {
                    // Écouter les erreurs de chargement asynchrone (URLs HTTP)
                    image.errorProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal) afficherPlaceholder();
                    });
                    annonceImageView.setImage(image);
                    annonceImageView.setVisible(true);
                    imagePlaceholder.setVisible(false);
                    imagePlaceholder.setManaged(false);
                }
            } catch (Exception e) {
                afficherPlaceholder();
            }
        } else {
            afficherPlaceholder();
        }
    }

    /**
     * Affiche un placeholder coloré avec emoji.
     */
    private void afficherPlaceholder() {
        annonceImageView.setVisible(false);
        annonceImageView.setManaged(false);
        imagePlaceholder.setVisible(true);
        imagePlaceholder.setManaged(true);

        String categorie = (annonce.getCategorie() != null) ? annonce.getCategorie().toLowerCase() : "";
        String emoji = "🌾";
        String gradient = "linear-gradient(to bottom right, #3E7B36, #2D5A27)";

        if (categorie.contains("tracteur")) {
            emoji = "🚜";
            gradient = "linear-gradient(to bottom right, #3E7B36, #2D5A27)";
        } else if (categorie.contains("moisson")) {
            emoji = "🌾";
            gradient = "linear-gradient(to bottom right, #F9A825, #F57F17)";
        } else if (categorie.contains("eau") || categorie.contains("irriga")) {
            emoji = "💧";
            gradient = "linear-gradient(to bottom right, #42A5F5, #1565C0)";
        } else if (categorie.contains("outil")) {
            emoji = "⚙️";
            gradient = "linear-gradient(to bottom right, #8D6E63, #4E342E)";
        } else if (categorie.contains("fruit") || categorie.contains("pomme")) {
            emoji = "🍎";
            gradient = "linear-gradient(to bottom right, #e53935, #b71c1c)";
        }

        placeholderIcon.setText(emoji);
        placeholderText.setText(annonce.getCategorie() != null ? annonce.getCategorie() : "AgriFlow");
        imagePlaceholder.setStyle("-fx-background-color: " + gradient + "; -fx-background-radius: 12 12 0 0;");
    }

    @FXML
    private void onReserveClick() {
        if (onReserveClick != null && annonce != null)
            onReserveClick.accept(annonce);
    }

    @FXML
    private void onEditClick() {
        if (onEditClick != null && annonce != null)
            onEditClick.accept(annonce);
    }

    @FXML
    private void onDeleteClick() {
        if (onDeleteClick != null && annonce != null)
            onDeleteClick.accept(annonce);
    }
}
