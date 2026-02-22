package controllers;

import entities.Culture;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.IrrigationSmartService;
import services.ServicePlanIrrigationJour;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public class ContCultureDetails {

    private final ServicePlanIrrigationJour serviceJour = new ServicePlanIrrigationJour();
    private int planId;
    private Culture culture;
    private LocalDate dateReference = LocalDate.now();

    enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }
    enum Row { EAU, TIME, TEMP, HUMID, PLUIE }

    @FXML private Label titreLabel; // Utilisé aussi comme titleLabel
    @FXML private Label infoLabel;
    @FXML private Label labelSemaine;
    @FXML private GridPane planningGrid;
    @FXML private Label consoStandardLabel;
    @FXML private Label consoSmartLabel;
    @FXML private Label economieLabel;

    // Boutons d'action pour l'expert
    @FXML private Button btnEnregistrer;
    @FXML private Button btnGenererIA;
    @FXML private Button btnAnnuler;

    private final Map<Row, Map<Day, TextField>> fields = new EnumMap<>(Row.class);

    @FXML
    public void initialize() {
        for (Row r : Row.values()) {
            fields.put(r, new EnumMap<>(Day.class));
        }
        buildInputGrid();
        mettreAJourLabelSemaine();
    }

    // --- LOGIQUE IA & ACTIONS ---

    @FXML
    private void genererPlanAutomatique(ActionEvent event) {
        if (culture == null || planId <= 0) {
            showAlert("Erreur", "Données de culture manquantes.");
            return;
        }

        IrrigationSmartService smartService = new IrrigationSmartService();
        LocalDate lundi = getDebutSemaineActuelle();

        try {
            Map<String, float[]> planData = smartService.genererPlanIA(this.culture);

            for (Map.Entry<String, float[]> entry : planData.entrySet()) {
                float[] val = entry.getValue();
                serviceJour.saveDayOptimized(
                        this.planId, entry.getKey(), val[0], (int) val[1],
                        val[2], val[3], val[4], lundi
                );
            }

            reloadFromDbIfPossible();
            showAlert("Succès", "Plan optimisé via IA : La pluie a été déduite !");

        } catch (Exception e) {
            showAlert("Erreur API", "Détail : " + e.getMessage());
        }
    }

    @FXML
    private void enregistrerPlanning(ActionEvent event) {
        if (planId <= 0) return;
        try {
            LocalDate lundi = getDebutSemaineActuelle();
            for (Day day : Day.values()) {
                float eau = parseSafeFloat(fields.get(Row.EAU).get(day).getText());
                int time = (int) parseSafeFloat(fields.get(Row.TIME).get(day).getText());
                float temp = parseSafeFloat(fields.get(Row.TEMP).get(day).getText());
                serviceJour.saveDay(planId, day.name(), eau, time, temp, lundi);
            }
            showAlert("Succès", "Planning enregistré !");
        } catch (Exception e) {
            showAlert("Erreur", "Vérifiez les formats numériques.");
        }
    }

    // --- ACCÈS AUX DONNÉES ---

    public void setPlanId(int planId) {
        this.planId = planId;
        reloadFromDbIfPossible();
    }

    public void setCulture(Culture culture) {
        this.culture = culture;
        if (culture != null && titreLabel != null) {
            titreLabel.setText("Optimisation : " + culture.getNomCulture());
            infoLabel.setText("Parcelle N°" + culture.getIdParcelle() + " | Objectif : " + culture.getQuantiteEau() + " mm/sem");
        }
        reloadFromDbIfPossible();
    }

    // --- MODE LECTURE (AGRICULTEUR) ---

    public void setReadOnlyMode(boolean readOnly) {
        if (readOnly) {
            // 1. Verrouiller tous les champs de texte de la grille
            fields.values().forEach(rowMap ->
                    rowMap.values().forEach(tf -> {
                        tf.setEditable(false);
                        tf.setStyle(tf.getStyle() + "-fx-background-color: #f8f9fa; -fx-opacity: 0.9;");
                    })
            );

            // 2. Masquer les boutons d'édition (Expert)
            if (btnEnregistrer != null) { btnEnregistrer.setVisible(false); btnEnregistrer.setManaged(false); }
            if (btnGenererIA != null) { btnGenererIA.setVisible(false); btnGenererIA.setManaged(false); }

            // 3. Adapter les labels
            if (btnAnnuler != null) btnAnnuler.setText("Retour");
            if (titreLabel != null) titreLabel.setText("📖 Consultation : " + (culture != null ? culture.getNomCulture() : "Plan"));
        }
    }

    // --- MISE À JOUR DE L'INTERFACE ---

    private void reloadFromDbIfPossible() {
        clearFields();
        if (planId <= 0) return;
        try {
            LocalDate lundi = getDebutSemaineActuelle();
            Map<String, float[]> map = serviceJour.loadAll(planId, lundi);

            for (Day day : Day.values()) {
                float[] valeurs = map.get(day.name());
                if (valeurs != null) {
                    fields.get(Row.EAU).get(day).setText(String.format(Locale.US, "%.2f", valeurs[0]));
                    fields.get(Row.TIME).get(day).setText(String.valueOf((int) valeurs[1]));
                    fields.get(Row.TEMP).get(day).setText(String.format(Locale.US, "%.1f", valeurs[2]));
                    if (fields.get(Row.HUMID).get(day) != null)
                        fields.get(Row.HUMID).get(day).setText(String.format(Locale.US, "%.0f%%", valeurs[3]));
                    if (fields.get(Row.PLUIE).get(day) != null)
                        fields.get(Row.PLUIE).get(day).setText(String.format(Locale.US, "%.1f", valeurs[4]));
                }
            }
            updateConsommationStats(map);
            updateGridHeaders(lundi, map);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void buildInputGrid() {
        planningGrid.getChildren().clear();
        addSideLabel(0, 1, "EAU (mm)");
        addSideLabel(0, 2, "DURÉE (min)");
        addSideLabel(0, 3, "TEMP (°C)");
        addSideLabel(0, 4, "HUMIDITÉ (%)");
        addSideLabel(0, 5, "PLUIE (mm)");

        addRowInputs(Row.EAU, 1);
        addRowInputs(Row.TIME, 2);
        addRowInputs(Row.TEMP, 3);
        addRowInputs(Row.HUMID, 4);
        addRowInputs(Row.PLUIE, 5);
    }

    private void addRowInputs(Row row, int gridRow) {
        for (int i = 0; i < Day.values().length; i++) {
            Day day = Day.values()[i];
            TextField tf = new TextField();
            tf.setPrefWidth(85);
            tf.setStyle("-fx-alignment: center; -fx-background-radius: 8;");

            // Les données environnementales sont toujours en lecture seule
            if (row == Row.TEMP || row == Row.HUMID || row == Row.PLUIE) {
                tf.setEditable(false);
                tf.setFocusTraversable(false);
                tf.setStyle(tf.getStyle() + "-fx-background-color: #f4f4f4;");
            }

            fields.get(row).put(day, tf);
            planningGrid.add(tf, i + 1, gridRow);
        }
    }

    private void updateGridHeaders(LocalDate lundi, Map<String, float[]> data) {
        String[] noms = {"LUN", "MAR", "MER", "JEU", "VEN", "SAM", "DIM"};
        for (int i = 0; i < 7; i++) {
            Day dayEnum = Day.values()[i];
            LocalDate dateJour = lundi.plusDays(i);

            VBox headerBox = new VBox(2);
            headerBox.setAlignment(Pos.CENTER);
            headerBox.setStyle("-fx-padding: 5;");

            Label nameLabel = new Label(noms[i]);
            nameLabel.setStyle("-fx-font-weight: bold;");

            Label dateLabel = new Label(dateJour.format(DateTimeFormatter.ofPattern("dd/MM")));
            dateLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 10;");

            String icon = "☀️";
            if (data != null && data.containsKey(dayEnum.name())) {
                float[] v = data.get(dayEnum.name());
                float p = v.length > 4 ? v[4] : 0;
                if (p > 0.5) icon = "🌧️";
                else if (v[2] > 30) icon = "🔥";
            }

            Label iconLabel = new Label(icon);
            iconLabel.setStyle("-fx-font-size: 20;");

            headerBox.getChildren().addAll(nameLabel, dateLabel, iconLabel);
            planningGrid.add(headerBox, i + 1, 0);
        }
    }

    private void updateConsommationStats(Map<String, float[]> data) {
        if (culture == null || data == null) return;
        float totalSmart = 0;
        for (float[] vals : data.values()) { totalSmart += vals[0]; }
        float totalStandard = culture.getQuantiteEau();

        consoStandardLabel.setText(String.format("%.1f mm", totalStandard));
        consoSmartLabel.setText(String.format("%.1f mm", totalSmart));

        float economie = totalStandard - totalSmart;
        float pourcent = (totalStandard > 0) ? (economie / totalStandard) * 100 : 0;

        economieLabel.setText(String.format("%s : %.1f%%", (economie >= 0 ? "Économie" : "Surplus"), Math.abs(pourcent)));
        economieLabel.setStyle("-fx-text-fill: " + (economie >= 0 ? "#2ecc71" : "#e74c3c") + "; -fx-font-weight: bold;");
    }

    // --- NAVIGATION & UTILS ---

    private LocalDate getDebutSemaineActuelle() {
        return dateReference.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private void mettreAJourLabelSemaine() {
        LocalDate debut = getDebutSemaineActuelle();
        labelSemaine.setText(debut.format(DateTimeFormatter.ofPattern("dd/MM")) + " au " + debut.plusDays(6).format(DateTimeFormatter.ofPattern("dd/MM")));
    }

    @FXML void semainePrecedente(ActionEvent event) { dateReference = dateReference.minusWeeks(1); updateUI(); }
    @FXML void semaineSuivante(ActionEvent event) { dateReference = dateReference.plusWeeks(1); updateUI(); }

    private void updateUI() { mettreAJourLabelSemaine(); reloadFromDbIfPossible(); }

    @FXML
    private void retour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ExperpalnIrrigation.fxml"));
            ((Stage)((Node)event.getSource()).getScene().getWindow()).getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void addSideLabel(int col, int row, String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        GridPane.setHalignment(label, HPos.RIGHT);
        planningGrid.add(label, col, row);
    }

    private void clearFields() { fields.values().forEach(m -> m.values().forEach(TextField::clear)); }

    private float parseSafeFloat(String text) {
        try {
            if (text == null || text.trim().isEmpty()) return 0f;
            return Float.parseFloat(text.replace(",", ".").replace("%", ""));
        } catch (Exception e) { return 0f; }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}