package com.agriflow.marketplace.controllers;

import com.agriflow.marketplace.models.Annonce;
import com.agriflow.marketplace.models.Reservation;
import com.agriflow.marketplace.services.ServiceReservation;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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

        dateDebutPicker.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
        dateFinPicker.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
    }

    public void setAnnonce(Annonce annonce) {
        this.annonce = annonce;
        if (annonce != null) {
            titreAnnonceLabel.setText(annonce.getTitre());
            prixUnitaireLabel.setText("Prix: " + annonce.getPrixFormate());
            calculateTotal();
        }
    }

    private void calculateTotal() {
        if (annonce == null) {
            return;
        }

        if (dateDebutPicker.getValue() != null && dateFinPicker.getValue() != null) {
            long jours = ChronoUnit.DAYS.between(dateDebutPicker.getValue(), dateFinPicker.getValue()) + 1;
            if (jours > 0) {
                double total = annonce.getPrix() * jours;
                dureeLabel.setText("Duree: " + jours + " jour(s)");
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
            Reservation reservation = new Reservation();
            reservation.setAnnonce(annonce);
            reservation.setDemandeur(MainController.getCurrentUser());
            reservation.setProprietaire(annonce.getProprietaire());
            reservation.setDateDebut(dateDebutPicker.getValue());
            reservation.setDateFin(dateFinPicker.getValue());
            reservation.setCaution(annonce.getCaution());
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
