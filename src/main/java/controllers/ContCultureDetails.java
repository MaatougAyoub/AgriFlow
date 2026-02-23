package controllers;

import entities.Culture;
import services.ServiceGenerateurPlan;
import services.ServicePlanIrrigationJour;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumMap;
import java.util.Map;

public class ContCultureDetails {

    private final ServicePlanIrrigationJour serviceJour = new ServicePlanIrrigationJour();
    private final ServiceGenerateurPlan serviceGenerateur = new ServiceGenerateurPlan();

    private int planId;
    private Culture culture;

    private LocalDate dateReference = LocalDate.now();

    enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }
    enum Row { EAU, TIME, TEMP }

    @FXML private Label titreLabel;
    @FXML private Label infoLabel;
    @FXML private Label labelSemaine;
    @FXML private GridPane planningGrid;

    private final Map<Row, Map<Day, TextField>> fields = new EnumMap<>(Row.class);


    @FXML
    public void initialize() {

        for (Row r : Row.values()) {
            fields.put(r, new EnumMap<>(Day.class));
        }

        buildInputGrid();
        mettreAJourLabelSemaine();
    }

    @FXML
    private void semainePrecedente(ActionEvent event) {
        dateReference = dateReference.minusWeeks(1);
        mettreAJourLabelSemaine();
        reloadFromDbIfPossible();
    }

    @FXML
    private void semaineSuivante(ActionEvent event) {
        dateReference = dateReference.plusWeeks(1);
        mettreAJourLabelSemaine();
        reloadFromDbIfPossible();
    }

    private LocalDate getDebutSemaineActuelle() {
        return dateReference.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private void mettreAJourLabelSemaine() {

        LocalDate debut = getDebutSemaineActuelle();
        LocalDate fin = debut.plusDays(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        labelSemaine.setText(debut.format(formatter) + " au " + fin.format(formatter));

        updateGridHeaders(debut);
    }

    private void updateGridHeaders(LocalDate lundi) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        String[] nomsJours = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        for (int i = 0; i < 7; i++) {
            LocalDate dateJour = lundi.plusDays(i);
            String texte = nomsJours[i] + "\n" + dateJour.format(formatter);
            addHeader(i + 1, 0, texte);
        }
    }

    //DONNÉES

    public void setPlanId(int planId) {
        this.planId = planId;
        reloadFromDbIfPossible();
    }

    public void setCulture(Culture culture) {
        this.culture = culture;

        if (culture != null) {
            titreLabel.setText("Plan : " + culture.getNom());
            infoLabel.setText("Parcelle : " + culture.getParcelleId()
                    + " | Eau recommandée : " + culture.getQuantiteEau() + " mm");
        }

        reloadFromDbIfPossible();
    }

    // CHARGEMENT BD

    private void reloadFromDbIfPossible() {

        clearFields();

        if (planId <= 0) return;

        try {

            LocalDate lundi = getDebutSemaineActuelle();
            Map<String, float[]> map = serviceJour.loadAll(planId, lundi);

            for (Day day : Day.values()) {

                float[] valeurs = map.get(day.name());

                if (valeurs != null) {
                    fields.get(Row.EAU).get(day).setText(String.valueOf(valeurs[0]));
                    fields.get(Row.TIME).get(day).setText(String.valueOf((int) valeurs[1]));
                    fields.get(Row.TEMP).get(day).setText(String.valueOf(valeurs[2]));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        for (Row row : Row.values()) {
            for (Day day : Day.values()) {
                fields.get(row).get(day).clear();
            }
        }
    }

    // GÉNÉRATION AUTOMATIQUE

    @FXML
    private void genererPlanAutomatique(ActionEvent event) {

        if (culture == null) {
            showAlert("Erreur", "Aucune culture sélectionnée.");
            return;
        }

        float quantiteTotale = culture.getQuantiteEau();
        Map<String, float[]> planGenere = serviceGenerateur.genererPlanHebdo(quantiteTotale);

        for (Day day : Day.values()) {

            float[] valeurs = planGenere.get(day.name());

            if (valeurs != null) {
                fields.get(Row.EAU).get(day).setText(String.valueOf(valeurs[0]));
                fields.get(Row.TIME).get(day).setText(String.valueOf((int) valeurs[1]));
                fields.get(Row.TEMP).get(day).setText(String.valueOf(valeurs[2]));
            }
        }

        showAlert("Succès", "Plan généré (non enregistré).");
    }

    // ENREGISTREMENT

    @FXML
    private void enregistrerPlanning(ActionEvent event) {

        try {

            if (planId <= 0) return;

            LocalDate lundi = getDebutSemaineActuelle();

            for (Day day : Day.values()) {

                String eauStr = fields.get(Row.EAU).get(day).getText().trim();
                String timeStr = fields.get(Row.TIME).get(day).getText().trim();
                String tempStr = fields.get(Row.TEMP).get(day).getText().trim();

                float eau = eauStr.isEmpty() ? 0f : Float.parseFloat(eauStr);
                int time = timeStr.isEmpty() ? 0 : Integer.parseInt(timeStr);
                float temp = tempStr.isEmpty() ? 0f : Float.parseFloat(tempStr);

                serviceJour.saveDay(planId, day.name(), eau, time, temp, lundi);
            }

            showAlert("Succès", "Planning enregistré pour la semaine du " + lundi);

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Vérifiez les valeurs numériques.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= UI =================

    @FXML
    private void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/palnIrrigation.fxml"));
            Parent root = loader.load();
            ((Node) event.getSource()).getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildInputGrid() {

        planningGrid.getChildren().clear();

        addHeader(0, 1, "Total eau (mm)");
        addHeader(0, 2, "Time (min)");
        addHeader(0, 3, "Temp (°C)");

        addRowInputs(Row.EAU, 1);
        addRowInputs(Row.TIME, 2);
        addRowInputs(Row.TEMP, 3);
    }

    private void addRowInputs(Row row, int gridRow) {

        for (int i = 0; i < Day.values().length; i++) {

            Day day = Day.values()[i];

            TextField tf = new TextField();
            tf.setPrefWidth(110);
            tf.setPrefHeight(50);
            tf.setStyle("-fx-background-color:white; -fx-border-color:#2d5a27; -fx-alignment:center;");

            fields.get(row).put(day, tf);
            planningGrid.add(tf, i + 1, gridRow);
        }
    }

    private void addHeader(int col, int row, String text) {

        Label label = new Label(text);
        label.setStyle("-fx-font-weight:bold; -fx-text-alignment:center;");
        GridPane.setHalignment(label, HPos.CENTER);
        planningGrid.add(label, col, row);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
