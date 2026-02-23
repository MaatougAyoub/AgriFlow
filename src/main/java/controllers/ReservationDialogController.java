package controllers;

import entities.Annonce;
import entities.Reservation;
import entities.TypeAnnonce;
import services.ServiceReservation;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import controllers.MainController;


// Controleur mta3 el dialog (popup) bech na3mlou reservation
// el user ykhayyer les dates, el quantite, yekteb message, w ychouf el prix total
public class ReservationDialogController {

    @FXML
    private Label titreAnnonceLabel;

    @FXML
    private Label prixUnitaireLabel;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private Spinner<Integer> quantiteSpinner;

    @FXML
    private Label stockLabel;

    @FXML
    private Label dureeLabel;

    @FXML
    private Label prixTotalLabel;

    @FXML
    private TextArea messageArea;

    @FXML
    private Label errorLabel;

    private Annonce annonce;
    private ServiceReservation reservationService;

    @FXML
    public void initialize() {
        reservationService = new ServiceReservation();
        dateDebutPicker.setValue(LocalDate.now());
        dateFinPicker.setValue(LocalDate.now().plusDays(1));

        // Spinner quantite : min 1, max sera mis a jour dans setAnnonce
        quantiteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 1));

        dateDebutPicker.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
        dateFinPicker.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
        quantiteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
    }

    public void setAnnonce(Annonce annonce) {
        this.annonce = annonce;
        if (annonce != null) {
            titreAnnonceLabel.setText(annonce.getTitre());
            prixUnitaireLabel.setText("Prix: " + annonce.getPrixFormate());

            // Afficher le stock disponible et limiter le spinner
            int stock = annonce.getQuantiteDisponible();
            String unite = annonce.getUniteQuantite() != null ? annonce.getUniteQuantite() : "unité";
            stockLabel.setText("Stock: " + stock + " " + unite);

            if (stock > 0) {
                quantiteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, stock, 1));
            }

            calculateTotal();
        }
    }

    private void calculateTotal() {
        if (annonce == null) {
            return;
        }

        if (dateDebutPicker.getValue() != null && dateFinPicker.getValue() != null) {
            long jours = ChronoUnit.DAYS.between(dateDebutPicker.getValue(), dateFinPicker.getValue()) + 1;
            int quantite = quantiteSpinner.getValue() != null ? quantiteSpinner.getValue() : 1;

            if (jours > 0) {
                double total;
                if (annonce.estEnLocation()) {
                    // LOCATION : prix/jour * nombre de jours * quantite
                    total = annonce.getPrix() * jours * quantite;
                    dureeLabel.setText("Durée: " + jours + " jour(s) × " + quantite + " unité(s)");
                } else {
                    // VENTE : prix unitaire * quantite
                    total = annonce.getPrix() * quantite;
                    dureeLabel.setText("Quantité: " + quantite + " " +
                            (annonce.getUniteQuantite() != null ? annonce.getUniteQuantite() : "unité(s)"));
                }
                prixTotalLabel.setText(String.format("Prix total: %.2f DT", total));
            }
        }
    }

    @FXML
    private void onConfirmer() {
        if (!validateForm()) {
            return;
        }

        try {
            int quantite = quantiteSpinner.getValue() != null ? quantiteSpinner.getValue() : 1;

            Reservation reservation = new Reservation();
            reservation.setAnnonce(annonce);
            reservation.setDemandeur(MainController.getCurrentUser());
            reservation.setProprietaire(annonce.getProprietaire());
            reservation.setDateDebut(dateDebutPicker.getValue());
            reservation.setDateFin(dateFinPicker.getValue());
            reservation.setCaution(annonce.getCaution());
            reservation.setQuantite(quantite);
            reservation.setMessageDemande(messageArea.getText().trim());
            reservation.calculerPrixTotal();

            reservationService.ajouter(reservation);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succes");
            alert.setContentText("Reservation envoyee!");
            alert.showAndWait();

            fermerFenetre();
        } catch (SQLException e) {
            errorLabel.setText("Erreur: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    private boolean validateForm() {
        if (annonce == null) {
            errorLabel.setText("Annonce invalide");
            errorLabel.setVisible(true);
            return false;
        }
        if (!MainController.isUserLoggedIn()) {
            errorLabel.setText("Utilisateur non connecte");
            errorLabel.setVisible(true);
            return false;
        }
        if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
            errorLabel.setText("Dates requises");
            errorLabel.setVisible(true);
            return false;
        }
        if (dateDebutPicker.getValue().isBefore(LocalDate.now())) {
            errorLabel.setText("Date debut invalide");
            errorLabel.setVisible(true);
            return false;
        }
        if (dateDebutPicker.getValue().isAfter(dateFinPicker.getValue())) {
            errorLabel.setText("Date debut doit etre avant date fin");
            errorLabel.setVisible(true);
            return false;
        }
        int quantite = quantiteSpinner.getValue() != null ? quantiteSpinner.getValue() : 1;
        if (quantite > annonce.getQuantiteDisponible()) {
            errorLabel.setText("Quantité demandée supérieure au stock disponible (" + annonce.getQuantiteDisponible() + ")");
            errorLabel.setVisible(true);
            return false;
        }
        return true;
    }

    @FXML
    private void onAnnuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) titreAnnonceLabel.getScene().getWindow();
        stage.close();
    }
}