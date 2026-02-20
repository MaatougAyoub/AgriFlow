package controllers;

import entities.*;
import services.AnnonceService;
import services.ServiceReservation;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// Controleur mta3 Dashboard Admin - ytalla3 les statistiques w yger les annonces/reservations
// initialize() = yet5arjou les donnees, chargerAnnonces/chargerReservations = nbniw les cartes
public class AdminDashboardController implements Initializable {

    // Elements FXML
    @FXML private Label totalAnnoncesLabel;
    @FXML private Label totalReservationsLabel;
    @FXML private Label enAttenteLabel;
    @FXML private TextField searchAnnoncesField;
    @FXML private TextField searchReservationsField;
    @FXML private FlowPane annoncesContainer;      // conteneur des cartes annonces
    @FXML private FlowPane reservationsContainer;   // conteneur des cartes réservations

    // Services
    private final AnnonceService annonceService = new AnnonceService();
    private final ServiceReservation reservationService = new ServiceReservation();

    // Données en mémoire
    private ObservableList<Annonce> annoncesData = FXCollections.observableArrayList();
    private ObservableList<Reservation> reservationsData = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // INITIALISATION — se lance automatiquement au chargement

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Charger les données au démarrage
        chargerAnnonces();
        chargerReservations();

        // Recherche en temps réel : quand le texte change, on filtre
        searchAnnoncesField.textProperty().addListener((obs, old, val) -> filtrerAnnonces(val));
        searchReservationsField.textProperty().addListener((obs, old, val) -> filtrerReservations(val));
    }

    // ONGLET 1 : ANNONCES

    /** Charge TOUTES les annonces depuis la BDD et les affiche en cartes */
    @FXML
    public void chargerAnnonces() {
        try {
            annoncesData.setAll(annonceService.recuperer());
            afficherCartesAnnonces(annoncesData);
            totalAnnoncesLabel.setText(String.valueOf(annoncesData.size()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    /** Affiche une liste d'annonces sous forme de cartes dans le FlowPane */
    private void afficherCartesAnnonces(List<Annonce> annonces) {
        annoncesContainer.getChildren().clear();
        for (Annonce a : annonces) {
            annoncesContainer.getChildren().add(creerCarteAnnonce(a));
        }
    }

    /** Filtre les annonces par texte (titre ou propriétaire) */
    private void filtrerAnnonces(String query) {
        if (query == null || query.isBlank()) {
            afficherCartesAnnonces(annoncesData);
            return;
        }
        String q = query.toLowerCase();
        List<Annonce> filtered = annoncesData.stream()
            .filter(a -> a.getTitre().toLowerCase().contains(q)
                || (a.getProprietaire() != null && a.getProprietaire().getNomComplet().toLowerCase().contains(q)))
            .collect(Collectors.toList());
        afficherCartesAnnonces(filtered);
    }

    // Fabriquer UNE carte annonce

    /**
     * Crée une carte visuelle pour une annonce.
     */
    private VBox creerCarteAnnonce(Annonce annonce) {
        VBox card = new VBox(8);
        card.getStyleClass().add("admin-card");
        card.setPrefWidth(280);
        card.setPadding(new Insets(15));

        // Header : badge type + bouton supprimer
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        // Badge type (LOCATION = vert, VENTE = bleu)
        Label badge = new Label(annonce.getType() != null ? annonce.getType().name() : "—");
        badge.getStyleClass().add("admin-badge");
        String badgeColor = annonce.estEnLocation() ? "#2D5A27" : "#1565C0";
        badge.setStyle("-fx-background-color: " + badgeColor + "; -fx-text-fill: white;"
            + "-fx-padding: 3 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Bouton supprimer (icône poubelle rouge)
        Button btnDelete = creerBoutonIcone(
            "M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z",
            "#E53935", "#C62828"
        );
        btnDelete.setOnAction(e -> onSupprimerAnnonce(annonce));

        header.getChildren().addAll(badge, spacer, btnDelete);

        // Titre
        Label titre = new Label(annonce.getTitre());
        titre.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #212121;");
        titre.setWrapText(true);

        // Infos
        Label prix = creerInfoLabel("💰", annonce.getPrixFormate());
        Label proprio = creerInfoLabel("👤",
            annonce.getProprietaire() != null ? annonce.getProprietaire().getNomComplet() : "—");
        Label date = creerInfoLabel("📅",
            annonce.getDateCreation() != null ? annonce.getDateCreation().format(DATE_FMT) : "—");

        // Badge statut
        Label statut = new Label("● " + (annonce.getStatut() != null ? annonce.getStatut().getLabel() : "—"));
        String statutColor = annonce.getStatut() != null ? annonce.getStatut().getCouleur() : "#757575";
        statut.setStyle("-fx-text-fill: " + statutColor + "; -fx-font-weight: bold; -fx-font-size: 12px;");

        card.getChildren().addAll(header, titre, prix, proprio, date, statut);

        // ── Animation hover ──
        ajouterAnimationHover(card);

        return card;
    }

    /** Supprime une annonce après confirmation */
    private void onSupprimerAnnonce(Annonce annonce) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Modération");
        confirm.setHeaderText("Supprimer : " + annonce.getTitre() + " ?");
        confirm.setContentText("Cette action est irréversible.");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    annonceService.supprimer(annonce);
                    chargerAnnonces();    // Recharger les cartes
                    chargerReservations(); // Mettre à jour les stats
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
                }
            }
        });
    }

    // ONGLET 2 : RÉSERVATIONS

    /** Charge TOUTES les réservations depuis la BDD */
    @FXML
    public void chargerReservations() {
        try {
            reservationsData.setAll(reservationService.afficherTout());
            afficherCartesReservations(reservationsData);
            totalReservationsLabel.setText(String.valueOf(reservationsData.size()));

            long enAttente = reservationsData.stream()
                .filter(r -> r.getStatut() == StatutReservation.EN_ATTENTE).count();
            enAttenteLabel.setText(String.valueOf(enAttente));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    /** Affiche une liste de réservations sous forme de cartes */
    private void afficherCartesReservations(List<Reservation> reservations) {
        reservationsContainer.getChildren().clear();
        for (Reservation r : reservations) {
            reservationsContainer.getChildren().add(creerCarteReservation(r));
        }
    }

    /** Filtre les réservations */
    private void filtrerReservations(String query) {
        if (query == null || query.isBlank()) {
            afficherCartesReservations(reservationsData);
            return;
        }
        String q = query.toLowerCase();
        List<Reservation> filtered = reservationsData.stream()
            .filter(r -> (r.getAnnonce() != null && r.getAnnonce().getTitre().toLowerCase().contains(q))
                || (r.getDemandeur() != null && r.getDemandeur().getNomComplet().toLowerCase().contains(q))
                || (r.getStatut() != null && r.getStatut().getLabel().toLowerCase().contains(q)))
            .collect(Collectors.toList());
        afficherCartesReservations(filtered);
    }

    // Fabriquer UNE carte réservation

    /**
     * Crée une carte visuelle pour une réservation.
     */
    private VBox creerCarteReservation(Reservation res) {
        VBox card = new VBox(8);
        card.getStyleClass().add("admin-card");
        card.setPrefWidth(280);
        card.setPadding(new Insets(0));

        // Header coloré selon statut
        String statusColor = res.getStatut() != null ? res.getStatut().getCouleur() : "#757575";
        String statusLabel = res.getStatut() != null ? res.getStatut().getLabel() : "—";

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 15, 10, 15));
        header.setStyle("-fx-background-color: " + statusColor + ";"
            + "-fx-background-radius: 12 12 0 0;");

        Label statutLabel = new Label("● " + statusLabel);
        statutLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Bouton annuler (seulement si pas déjà annulée/terminée)
        boolean canCancel = res.getStatut() != StatutReservation.ANNULEE
                         && res.getStatut() != StatutReservation.TERMINEE;

        header.getChildren().addAll(statutLabel, spacer);

        if (canCancel) {
            Button btnCancel = creerBoutonIcone(
                "M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z",
                "white", "#FFCDD2"
            );
            // Rendre le bouton transparent sur fond coloré
            btnCancel.setStyle("-fx-background-color: rgba(255,255,255,0.25); -fx-background-radius: 50;"
                + "-fx-min-width: 32; -fx-min-height: 32; -fx-max-width: 32; -fx-max-height: 32;"
                + "-fx-padding: 0; -fx-cursor: hand;");
            btnCancel.setOnAction(e -> onAnnulerReservation(res));
            header.getChildren().add(btnCancel);
        }

        // ── Conten ──
        VBox content = new VBox(6);
        content.setPadding(new Insets(12, 15, 15, 15));

        // Titre annonce
        String annonceTitle = res.getAnnonce() != null ? res.getAnnonce().getTitre() : "—";
        Label titre = new Label(annonceTitle);
        titre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #212121;");
        titre.setWrapText(true);

        // Infos
        Label client = creerInfoLabel("👤",
            res.getDemandeur() != null ? res.getDemandeur().getNomComplet() : "—");
        Label proprio = creerInfoLabel("🏠",
            res.getProprietaire() != null ? res.getProprietaire().getNomComplet() : "—");

        String dateStr = (res.getDateDebut() != null ? res.getDateDebut().format(DATE_FMT) : "—")
                       + " → "
                       + (res.getDateFin() != null ? res.getDateFin().format(DATE_FMT) : "—");
        Label dates = creerInfoLabel("📅", dateStr);

        Label prix = creerInfoLabel("💰", String.format("%.2f DT", res.getPrixTotal()));

        content.getChildren().addAll(titre, client, proprio, dates, prix);
        card.getChildren().addAll(header, content);

        // ── Animation hover ──
        ajouterAnimationHover(card);

        return card;
    }

    /** Force l'annulation d'une réservation (admin) */
    private void onAnnulerReservation(Reservation res) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Modération");
        confirm.setHeaderText("Annuler la réservation #" + res.getId() + " ?");
        confirm.setContentText("Le client et le propriétaire seront affectés.");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    res.setStatut(StatutReservation.ANNULEE);
                    reservationService.modifier(res);
                    chargerReservations(); // Recharger
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
                }
            }
        });
    }

    // NAVIGATION

    @FXML
    private void retourMarketplace() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/Marketplace.fxml"));
            Parent view = loader.load();
            StackPane parent = (StackPane) annoncesContainer.getScene().lookup("#contentArea");
            if (parent != null) {
                parent.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // HELPERS — petites méthodes utilitaires réutilisables

    /**
     * Crée un bouton rond avec une icône SVG.
     */
    private Button creerBoutonIcone(String svgPath, String fillColor, String hoverColor) {
        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setFill(Color.web(fillColor));
        icon.setScaleX(0.75);
        icon.setScaleY(0.75);

        Button btn = new Button();
        btn.setGraphic(icon);
        btn.getStyleClass().add("admin-icon-btn");

        // Animation survol : changement de couleur
        btn.setOnMouseEntered(e -> icon.setFill(Color.web(hoverColor)));
        btn.setOnMouseExited(e -> icon.setFill(Color.web(fillColor)));

        return btn;
    }

    /** Crée un label d'information avec emoji + texte */
    private Label creerInfoLabel(String emoji, String text) {
        Label label = new Label(emoji + "  " + text);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: #616161;");
        label.setWrapText(true);
        return label;
    }

    /**
     * Ajoute une animation de zoom au survol d'une carte.
     * La carte grossit légèrement quand la souris passe dessus.
     */
    private void ajouterAnimationHover(VBox card) {
        // Animation d'entrée : agrandir à 1.03x
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), card);
        scaleUp.setToX(1.03);
        scaleUp.setToY(1.03);

        // Animation de sortie : revenir à 1.0x
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), card);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        card.setOnMouseEntered(e -> scaleUp.playFromStart());
        card.setOnMouseExited(e -> scaleDown.playFromStart());
    }

    /** Affiche une alerte (erreur ou info) */
    private void showAlert(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
