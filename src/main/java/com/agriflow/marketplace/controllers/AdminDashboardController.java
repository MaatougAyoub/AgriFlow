package com.agriflow.marketplace.controllers;

import com.agriflow.marketplace.models.*;
import com.agriflow.marketplace.services.AnnonceService;
import com.agriflow.marketplace.services.ServiceReservation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Contrôleur Back-Office Administrateur.
 * Affiche toutes les annonces et réservations avec actions de modération.
 */
public class AdminDashboardController implements Initializable {

    // ─── Stats ────────────────────────────────────────────────
    @FXML private Label totalAnnoncesLabel;
    @FXML private Label totalReservationsLabel;
    @FXML private Label enAttenteLabel;

    // ─── Annonces Tab ─────────────────────────────────────────
    @FXML private TextField searchAnnoncesField;
    @FXML private TableView<Annonce> annoncesTable;
    @FXML private TableColumn<Annonce, Integer> colAnnonceId;
    @FXML private TableColumn<Annonce, String> colAnnonceTitre;
    @FXML private TableColumn<Annonce, String> colAnnonceType;
    @FXML private TableColumn<Annonce, String> colAnnoncePrix;
    @FXML private TableColumn<Annonce, String> colAnnonceStatut;
    @FXML private TableColumn<Annonce, String> colAnnonceProprietaire;
    @FXML private TableColumn<Annonce, String> colAnnonceDate;
    @FXML private TableColumn<Annonce, Void> colAnnonceActions;

    // ─── Réservations Tab ─────────────────────────────────────
    @FXML private TextField searchReservationsField;
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, Integer> colResId;
    @FXML private TableColumn<Reservation, String> colResAnnonce;
    @FXML private TableColumn<Reservation, String> colResDemandeur;
    @FXML private TableColumn<Reservation, String> colResProprietaire;
    @FXML private TableColumn<Reservation, String> colResStatut;
    @FXML private TableColumn<Reservation, String> colResDateDebut;
    @FXML private TableColumn<Reservation, String> colResDateFin;
    @FXML private TableColumn<Reservation, String> colResPrix;
    @FXML private TableColumn<Reservation, Void> colResActions;

    // ─── Services ─────────────────────────────────────────────
    private final AnnonceService annonceService = new AnnonceService();
    private final ServiceReservation reservationService = new ServiceReservation();

    private ObservableList<Annonce> annoncesData = FXCollections.observableArrayList();
    private ObservableList<Reservation> reservationsData = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupAnnoncesTable();
        setupReservationsTable();
        chargerAnnonces();
        chargerReservations();
    }

    // ═══════════════════════════════════════════════════════════
    //  ONGLET 1 : ANNONCES
    // ═══════════════════════════════════════════════════════════

    private void setupAnnoncesTable() {
        colAnnonceId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAnnonceTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));

        colAnnonceType.setCellValueFactory(cell -> {
            TypeAnnonce t = cell.getValue().getType();
            return new SimpleStringProperty(t != null ? t.name() : "—");
        });

        colAnnoncePrix.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getPrixFormate()));

        colAnnonceStatut.setCellValueFactory(cell -> {
            StatutAnnonce s = cell.getValue().getStatut();
            return new SimpleStringProperty(s != null ? s.getLabel() : "—");
        });

        colAnnonceProprietaire.setCellValueFactory(cell -> {
            User p = cell.getValue().getProprietaire();
            return new SimpleStringProperty(p != null ? p.getNomComplet() : "—");
        });

        colAnnonceDate.setCellValueFactory(cell -> {
            var d = cell.getValue().getDateCreation();
            return new SimpleStringProperty(d != null ? d.format(DATE_FMT) : "—");
        });

        // Colonne Actions : bouton Supprimer
        colAnnonceActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = createDeleteButton();

            {
                btnDelete.setOnAction(e -> {
                    Annonce annonce = getTableView().getItems().get(getIndex());
                    onSupprimerAnnonce(annonce);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(8, btnDelete));
            }
        });

        // Recherche temps réel
        searchAnnoncesField.textProperty().addListener((obs, oldVal, newVal) -> filtrerAnnonces(newVal));
    }

    @FXML
    public void chargerAnnonces() {
        try {
            annoncesData.setAll(annonceService.recuperer());
            annoncesTable.setItems(annoncesData);
            totalAnnoncesLabel.setText(String.valueOf(annoncesData.size()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les annonces : " + e.getMessage());
        }
    }

    private void filtrerAnnonces(String query) {
        if (query == null || query.isBlank()) {
            annoncesTable.setItems(annoncesData);
            return;
        }
        String lower = query.toLowerCase();
        FilteredList<Annonce> filtered = new FilteredList<>(annoncesData, a ->
            a.getTitre().toLowerCase().contains(lower)
            || (a.getProprietaire() != null && a.getProprietaire().getNomComplet().toLowerCase().contains(lower))
            || (a.getCategorie() != null && a.getCategorie().toLowerCase().contains(lower))
        );
        annoncesTable.setItems(filtered);
    }

    private void onSupprimerAnnonce(Annonce annonce) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Modération — Supprimer l'annonce");
        confirm.setHeaderText("Supprimer définitivement : " + annonce.getTitre() + " ?");
        confirm.setContentText("ID #" + annonce.getId() + " — Cette action est irréversible.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    annonceService.supprimer(annonce);
                    chargerAnnonces();
                    showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Annonce #" + annonce.getId() + " supprimée par l'administrateur.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Suppression échouée : " + e.getMessage());
                }
            }
        });
    }

    // ═══════════════════════════════════════════════════════════
    //  ONGLET 2 : RÉSERVATIONS
    // ═══════════════════════════════════════════════════════════

    private void setupReservationsTable() {
        colResId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colResAnnonce.setCellValueFactory(cell -> {
            Annonce a = cell.getValue().getAnnonce();
            return new SimpleStringProperty(a != null ? a.getTitre() : "—");
        });

        colResDemandeur.setCellValueFactory(cell -> {
            User d = cell.getValue().getDemandeur();
            return new SimpleStringProperty(d != null ? d.getNomComplet() : "—");
        });

        colResProprietaire.setCellValueFactory(cell -> {
            User p = cell.getValue().getProprietaire();
            return new SimpleStringProperty(p != null ? p.getNomComplet() : "—");
        });

        colResStatut.setCellValueFactory(cell -> {
            StatutReservation s = cell.getValue().getStatut();
            return new SimpleStringProperty(s != null ? s.getLabel() : "—");
        });

        // Colorisation du statut
        colResStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold;");
                    Reservation res = getTableView().getItems().get(getIndex());
                    if (res.getStatut() != null) {
                        setTextFill(Color.web(res.getStatut().getCouleur()));
                    }
                }
            }
        });

        colResDateDebut.setCellValueFactory(cell -> {
            var d = cell.getValue().getDateDebut();
            return new SimpleStringProperty(d != null ? d.format(DATE_FMT) : "—");
        });

        colResDateFin.setCellValueFactory(cell -> {
            var d = cell.getValue().getDateFin();
            return new SimpleStringProperty(d != null ? d.format(DATE_FMT) : "—");
        });

        colResPrix.setCellValueFactory(cell ->
            new SimpleStringProperty(String.format("%.2f DT", cell.getValue().getPrixTotal())));

        // Colonne Actions : bouton Annuler
        colResActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnCancel = createCancelButton();

            {
                btnCancel.setOnAction(e -> {
                    Reservation res = getTableView().getItems().get(getIndex());
                    onAnnulerReservation(res);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reservation res = getTableView().getItems().get(getIndex());
                    // Ne pas permettre d'annuler une réservation déjà annulée ou terminée
                    boolean canCancel = res.getStatut() != StatutReservation.ANNULEE
                                     && res.getStatut() != StatutReservation.TERMINEE;
                    setGraphic(canCancel ? new HBox(8, btnCancel) : null);
                }
            }
        });

        // Recherche temps réel
        searchReservationsField.textProperty().addListener((obs, oldVal, newVal) -> filtrerReservations(newVal));
    }

    @FXML
    public void chargerReservations() {
        try {
            reservationsData.setAll(reservationService.afficherTout());
            reservationsTable.setItems(reservationsData);
            totalReservationsLabel.setText(String.valueOf(reservationsData.size()));

            // Compter les "en attente"
            long enAttente = reservationsData.stream()
                .filter(r -> r.getStatut() == StatutReservation.EN_ATTENTE)
                .count();
            enAttenteLabel.setText(String.valueOf(enAttente));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les réservations : " + e.getMessage());
        }
    }

    private void filtrerReservations(String query) {
        if (query == null || query.isBlank()) {
            reservationsTable.setItems(reservationsData);
            return;
        }
        String lower = query.toLowerCase();
        FilteredList<Reservation> filtered = new FilteredList<>(reservationsData, r ->
            (r.getAnnonce() != null && r.getAnnonce().getTitre().toLowerCase().contains(lower))
            || (r.getDemandeur() != null && r.getDemandeur().getNomComplet().toLowerCase().contains(lower))
            || (r.getProprietaire() != null && r.getProprietaire().getNomComplet().toLowerCase().contains(lower))
            || (r.getStatut() != null && r.getStatut().getLabel().toLowerCase().contains(lower))
        );
        reservationsTable.setItems(filtered);
    }

    private void onAnnulerReservation(Reservation reservation) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Modération — Annuler la réservation");
        confirm.setHeaderText("Forcer l'annulation de la réservation #" + reservation.getId() + " ?");
        confirm.setContentText("Action administrative — le client et le propriétaire seront affectés.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    reservation.setStatut(StatutReservation.ANNULEE);
                    reservationService.modifier(reservation);
                    chargerReservations();
                    showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Réservation #" + reservation.getId() + " annulée par l'administrateur.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Annulation échouée : " + e.getMessage());
                }
            }
        });
    }

    // ═══════════════════════════════════════════════════════════
    //  NAVIGATION
    // ═══════════════════════════════════════════════════════════

    @FXML
    private void retourMarketplace() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/agriflow/marketplace/views/Marketplace.fxml"));
            Parent view = loader.load();
            StackPane parent = (StackPane) annoncesTable.getScene().lookup("#contentArea");
            if (parent != null) {
                parent.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════

    private Button createDeleteButton() {
        Button btn = new Button();
        btn.getStyleClass().add("btn-admin-delete");
        SVGPath icon = new SVGPath();
        icon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
        icon.setFill(Color.WHITE);
        icon.setScaleX(0.7);
        icon.setScaleY(0.7);
        btn.setGraphic(icon);
        btn.setText("Supprimer");
        return btn;
    }

    private Button createCancelButton() {
        Button btn = new Button();
        btn.getStyleClass().add("btn-admin-cancel");
        SVGPath icon = new SVGPath();
        icon.setContent("M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z");
        icon.setFill(Color.WHITE);
        icon.setScaleX(0.7);
        icon.setScaleY(0.7);
        btn.setGraphic(icon);
        btn.setText("Annuler");
        return btn;
    }

    private void showAlert(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
