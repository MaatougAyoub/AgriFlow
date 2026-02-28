package controllers;

import entities.Culture;
import entities.Parcelle;
import entities.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import services.ServiceCulture;
import services.ServiceParcelle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import java.io.IOException;
import java.io.InputStream;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ParcelleDetails {

    @FXML private BorderPane root;

    // header error
    @FXML private Label errorLabel;

    // fiche parcelle
    @FXML private Label lblNom;
    @FXML private Label lblSuperficie;
    @FXML private Label lblType;
    @FXML private Label lblLocalisation;
    @FXML private Label lblDateCreation;
    @FXML private Label lblId;

    // badge
    @FXML private Label badgeType;

    // mockup 3D
    @FXML private StackPane mockupPane;

    // table cultures
    @FXML private TableView<Culture> tableCultures;
    @FXML private TableColumn<Culture, Integer> colCultureId;
    @FXML private TableColumn<Culture, String> colCultureNom;
    @FXML private TableColumn<Culture, String> colCultureType;
    @FXML private TableColumn<Culture, Double> colCultureSuperficie;
    @FXML private TableColumn<Culture, String> colCultureEtat;
    @FXML private TableColumn<Culture, Object> colCultureDateRecolte;
    @FXML private TableColumn<Culture, Void> colCultureActions;

    @FXML private Label lblCountCultures;

    private final ServiceParcelle sp = new ServiceParcelle();
    private final ServiceCulture sc = new ServiceCulture();

    private int parcelleId = -1;
    private Parcelle parcelle;

    private boolean mockupBuilt = false;
    private List<Culture> lastCultures = List.of();

    // Cache images (évite reload en boucle)
    private final Map<String, Image> imageCache = new HashMap<>();

    @FXML
    public void initialize() {
        try {
            if (root != null) root.setDepthTest(DepthTest.ENABLE);

            setupCultureTable();

            if (mockupPane != null) {
                mockupPane.setMinHeight(260);
                mockupPane.setMaxHeight(420);
                mockupPane.setAlignment(Pos.CENTER);
                installBackground3D();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setParcelleId(int id) {
        this.parcelleId = id;
        charger();
    }

    private void charger() {
        hideError();
        try {
            parcelle = sp.recupererParId(parcelleId);
            if (parcelle == null) {
                showError("Parcelle introuvable (id=" + parcelleId + ")");
                return;
            }

            lblNom.setText(parcelle.getNom() == null ? "(Sans nom)" : parcelle.getNom());
            lblId.setText(String.valueOf(parcelle.getId()));
            lblSuperficie.setText(formatSuperficie(parcelle.getSuperficie()) + " m²");
            lblType.setText(parcelle.getTypeTerre() == null ? "-" : parcelle.getTypeTerre().name());
            lblLocalisation.setText(parcelle.getLocalisation() == null ? "-" : parcelle.getLocalisation());
            lblDateCreation.setText(parcelle.getDateCreation() == null ? "-" : parcelle.getDateCreation().toString());

            if (badgeType != null) {
                String t = parcelle.getTypeTerre() == null ? "-" : parcelle.getTypeTerre().name();
                badgeType.setText(t);
                badgeType.setVisible(true);
                badgeType.setManaged(true);
            }

            List<Culture> cultures = sc.recuperer().stream()
                    .filter(c -> c.getParcelleId() == parcelleId)
                    .collect(Collectors.toList());

            lastCultures = cultures;

            tableCultures.setItems(FXCollections.observableArrayList(cultures));
            if (lblCountCultures != null) lblCountCultures.setText(cultures.size() + " culture(s)");

            mockupBuilt = false;
            Platform.runLater(this::tryBuildMockup);

        } catch (SQLException e) {
            showError("Erreur DB: " + e.getMessage());
        }
    }

    private void tryBuildMockup() {
        if (mockupBuilt) return;
        if (mockupPane == null || parcelle == null) return;

        double w = mockupPane.getWidth();
        double h = mockupPane.getHeight();
        if (w <= 5 || h <= 5) {
            Platform.runLater(this::tryBuildMockup);
            return;
        }

        build3DMockup(parcelle, lastCultures);
        mockupBuilt = true;
    }

    private void supprimerCulture(Culture c) {
        hideError();
        try {
            sc.supprimer(c);
            charger();
        } catch (SQLException e) {
            showError("Erreur suppression culture: " + e.getMessage());
        }
    }

    // ============================================================
    // MODIFIER (popup avec validation inline)
    // ============================================================
    private void ouvrirPopupModificationCulture(Culture c) {
        hideError();
        if (c == null) return;

        // Calcul du max autorisé : superficie parcelle - somme des autres cultures (hors RECOLTEE)
        double superficieParcelle = (parcelle != null) ? parcelle.getSuperficie() : Double.MAX_VALUE;
        double sommeAutres = lastCultures.stream()
                .filter(other -> other.getId() != c.getId())
                .filter(other -> other.getEtat() != Culture.Etat.RECOLTEE)
                .mapToDouble(Culture::getSuperficie)
                .sum();
        final double maxSuperficie = Math.max(0, superficieParcelle - sommeAutres);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Culture");
        dialog.setHeaderText("Modifier : " + (c.getNom() == null ? "" : c.getNom()));
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefWidth(560);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        // ── Label d'erreur inline (dans le popup) ──────────────────────────
        Label inlineError = new Label();
        inlineError.setVisible(false);
        inlineError.setManaged(false);
        inlineError.setWrapText(true);
        inlineError.setMaxWidth(Double.MAX_VALUE);
        inlineError.setStyle(
                "-fx-text-fill: #B71C1C;" +
                        "-fx-background-color: #FFEBEE;" +
                        "-fx-padding: 8 12;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13px;"
        );

        // ── Champs ──────────────────────────────────────────────────────────
        TextField tfNom = new TextField(c.getNom() == null ? "" : c.getNom());
        tfNom.setPromptText("ex: Blé nord");

        ComboBox<Culture.TypeCulture> cbType = new ComboBox<>(
                FXCollections.observableArrayList(Culture.TypeCulture.values()));
        cbType.setValue(c.getTypeCulture());
        cbType.setMaxWidth(Double.MAX_VALUE);

        TextField tfSuperficie = new TextField(String.valueOf(c.getSuperficie()));
        tfSuperficie.setPromptText("max: " + String.format("%.2f", maxSuperficie) + " m²");

        ComboBox<Culture.Etat> cbEtat = new ComboBox<>(
                FXCollections.observableArrayList(Culture.Etat.values()));
        cbEtat.setValue(c.getEtat());
        cbEtat.setMaxWidth(Double.MAX_VALUE);

        DatePicker dpRecolte = new DatePicker(
                c.getDateRecolte() == null ? null : c.getDateRecolte().toLocalDate());
        dpRecolte.setMaxWidth(Double.MAX_VALUE);

        TextField tfRecolteEstime = new TextField(
                c.getRecolteEstime() == null ? "" : String.valueOf(c.getRecolteEstime()));
        tfRecolteEstime.setPromptText("ex: 300 (optionnel)");

        // Prix vente : visible seulement si EN_VENTE
        TextField tfPrixVente = new TextField(
                c.getPrixVente() == null ? "" : String.valueOf(c.getPrixVente()));
        tfPrixVente.setPromptText("ex: 250");
        boolean initEnVente = c.getEtat() == Culture.Etat.EN_VENTE;
        tfPrixVente.setDisable(!initEnVente);
        tfPrixVente.setVisible(initEnVente);
        tfPrixVente.setManaged(initEnVente);

        Label lblPrix = new Label("Prix vente (DT) *");
        lblPrix.setVisible(initEnVente);
        lblPrix.setManaged(initEnVente);

        cbEtat.valueProperty().addListener((obs, oldV, newV) -> {
            boolean ev = newV == Culture.Etat.EN_VENTE;
            tfPrixVente.setDisable(!ev);
            tfPrixVente.setVisible(ev);
            tfPrixVente.setManaged(ev);
            lblPrix.setVisible(ev);
            lblPrix.setManaged(ev);
            if (!ev) tfPrixVente.clear();
        });

        // ── Feedback visuel en temps réel (bordure rouge/verte) ─────────────
        tfNom.textProperty().addListener((obs, o, n) ->
                setFieldStyle(tfNom, !n.trim().isEmpty()));
        tfSuperficie.textProperty().addListener((obs, o, n) ->
                setFieldStyle(tfSuperficie, isPositiveDouble(n)));
        tfRecolteEstime.textProperty().addListener((obs, o, n) ->
                setFieldStyle(tfRecolteEstime, n.trim().isEmpty() || isPositiveDouble(n)));
        tfPrixVente.textProperty().addListener((obs, o, n) ->
                setFieldStyle(tfPrixVente, isPositiveDouble(n)));

        // ── Grille ──────────────────────────────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        grid.setMaxWidth(Double.MAX_VALUE);
        ColumnConstraints col0 = new ColumnConstraints(170);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col0, col1);

        int row = 0;
        grid.add(new Label("Nom *"),                    0, row); grid.add(tfNom,            1, row++);
        grid.add(new Label("Type *"),                   0, row); grid.add(cbType,           1, row++);
        grid.add(new Label("Superficie (m²) *"),        0, row); grid.add(tfSuperficie,     1, row++);
        grid.add(new Label("État *"),                   0, row); grid.add(cbEtat,           1, row++);
        grid.add(new Label("Date récolte *"),           0, row); grid.add(dpRecolte,        1, row++);
        grid.add(new Label("Récolte estimée (Kg)"),     0, row); grid.add(tfRecolteEstime,  1, row++);
        grid.add(lblPrix,                               0, row); grid.add(tfPrixVente,      1, row++);

        VBox content = new VBox(10, inlineError, grid);
        content.setPadding(new Insets(4));
        dialog.getDialogPane().setContent(content);

        // ── Bloquer la fermeture si validation échoue ──────────────────────
        Button okButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String errMsg = validerChamps(tfNom, cbType, tfSuperficie, cbEtat,
                    dpRecolte, tfRecolteEstime, tfPrixVente, c, maxSuperficie);
            if (errMsg != null) {
                event.consume(); // empêche la fermeture du dialog
                inlineError.setText(errMsg);
                inlineError.setVisible(true);
                inlineError.setManaged(true);
            }
        });

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isEmpty() || res.get() != saveBtn) return;

        // ── Sauvegarde (validation déjà passée dans le filtre) ────────────
        try {
            c.setNom(tfNom.getText().trim());
            c.setTypeCulture(cbType.getValue());
            c.setSuperficie(Double.parseDouble(tfSuperficie.getText().trim()));
            c.setEtat(cbEtat.getValue());
            c.setDateRecolte(java.sql.Date.valueOf(dpRecolte.getValue()));

            String rtxt = tfRecolteEstime.getText() == null ? "" : tfRecolteEstime.getText().trim();
            c.setRecolteEstime(rtxt.isEmpty() ? null : Double.parseDouble(rtxt));

            if (cbEtat.getValue() == Culture.Etat.EN_VENTE) {
                double pv = Double.parseDouble(tfPrixVente.getText().trim());
                c.setPrixVente(pv);
                if (c.getDatePublication() == null)
                    c.setDatePublication(java.sql.Date.valueOf(LocalDate.now()));
            } else {
                c.setPrixVente(null);
                c.setDatePublication(null);
                c.setIdAcheteur(null);
                c.setDateVente(null);
            }

            sc.modifier(c);
            rafraichir();

        } catch (NumberFormatException ex) {
            showError("Erreur numérique inattendue : " + ex.getMessage());
        } catch (SQLException ex) {
            showError("Erreur modification : " + ex.getMessage());
        }
    }

    // ── Validation centralisée — retourne null si OK, message sinon ───────
    private String validerChamps(TextField tfNom,
                                 ComboBox<Culture.TypeCulture> cbType,
                                 TextField tfSuperficie,
                                 ComboBox<Culture.Etat> cbEtat,
                                 DatePicker dpRecolte,
                                 TextField tfRecolteEstime,
                                 TextField tfPrixVente,
                                 Culture original,
                                 double maxSuperficie) {
        // Nom
        String nom = tfNom.getText() == null ? "" : tfNom.getText().trim();
        if (nom.isEmpty())
            return "Le nom est obligatoire.";
        if (nom.length() < 2)
            return "Le nom doit contenir au moins 2 caractères.";
        if (nom.length() > 100)
            return "Le nom ne doit pas dépasser 100 caractères.";
        if (!nom.matches("[\\p{L}0-9 '\\-_]+"))
            return "Le nom contient des caractères non autorisés.";

        // Type
        if (cbType.getValue() == null)
            return "Veuillez sélectionner un type de culture.";

        // Superficie
        String sTxt = tfSuperficie.getText() == null ? "" : tfSuperficie.getText().trim();
        if (sTxt.isEmpty())
            return "La superficie est obligatoire.";
        double superficie;
        try {
            superficie = Double.parseDouble(sTxt);
        } catch (NumberFormatException e) {
            return "La superficie doit être un nombre décimal (ex: 120.5).";
        }
        if (superficie <= 0)
            return "La superficie doit être supérieure à 0.";
        if (superficie > maxSuperficie + 1e-9)
            return String.format("Superficie trop grande. Maximum disponible sur cette parcelle : %.2f m².", maxSuperficie);
        if (superficie > 100_000)
            return "La superficie semble trop grande (maximum 100 000 m²).";

        // État
        if (cbEtat.getValue() == null)
            return "Veuillez sélectionner un état.";

        // Date récolte
        LocalDate dateRecolte = dpRecolte.getValue();
        if (dateRecolte == null)
            return "La date de récolte est obligatoire.";

        LocalDate dateCreation = (original.getDateCreation() != null)
                ? original.getDateCreation().toLocalDateTime().toLocalDate()
                : LocalDate.now();
        if (!dateRecolte.isAfter(dateCreation))
            return "La date de récolte doit être postérieure à la date de création (" + dateCreation + ").";
        if (dateRecolte.isAfter(LocalDate.now().plusYears(10)))
            return "La date de récolte semble trop lointaine (maximum +10 ans).";

        // Récolte estimée (optionnelle mais validée si renseignée)
        String rtxt = tfRecolteEstime.getText() == null ? "" : tfRecolteEstime.getText().trim();
        if (!rtxt.isEmpty()) {
            try {
                double re = Double.parseDouble(rtxt);
                if (re <= 0) return "La récolte estimée doit être supérieure à 0.";
                if (re > 10_000_000) return "La récolte estimée semble trop grande.";
            } catch (NumberFormatException e) {
                return "La récolte estimée doit être un nombre (ex: 300).";
            }
        }

        // Prix vente (obligatoire si EN_VENTE)
        if (cbEtat.getValue() == Culture.Etat.EN_VENTE) {
            String ptxt = tfPrixVente.getText() == null ? "" : tfPrixVente.getText().trim();
            if (ptxt.isEmpty())
                return "Le prix de vente est obligatoire quand l'état est EN_VENTE.";
            try {
                double pv = Double.parseDouble(ptxt);
                if (pv <= 0)  return "Le prix de vente doit être supérieur à 0.";
                if (pv > 100_000_000) return "Le prix de vente semble trop élevé.";
            } catch (NumberFormatException e) {
                return "Le prix de vente doit être un nombre décimal (ex: 250).";
            }
        }

        return null; // tout est valide ✓
    }

    // ── Helper : vérifie qu'une chaîne est un double > 0 ──────────────────
    private boolean isPositiveDouble(String txt) {
        if (txt == null || txt.trim().isEmpty()) return false;
        try { return Double.parseDouble(txt.trim()) > 0; }
        catch (NumberFormatException e) { return false; }
    }

    // ── Helper : bordure verte/rouge sur un TextField ──────────────────────
    private void setFieldStyle(TextField tf, boolean valid) {
        tf.setStyle(valid
                ? "-fx-border-color: #A5D6A7; -fx-border-radius: 6; -fx-background-radius: 6;"
                : "-fx-border-color: #EF9A9A; -fx-border-radius: 6; -fx-background-radius: 6;");
    }

    // ============================================================
    // RAFRAICHIR
    // ============================================================
    private void rafraichir() {
        charger();
    }

    // ============================================================
    // TABLE CULTURES
    // ============================================================
    private void setupCultureTable() {
        colCultureActions.setCellValueFactory(param -> new javafx.beans.property.ReadOnlyObjectWrapper<>(null));

        colCultureId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colCultureNom.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNom()));
        colCultureType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getTypeCulture() == null ? "-" : c.getValue().getTypeCulture().name()
        ));
        colCultureSuperficie.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getSuperficie()).asObject());
        colCultureEtat.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEtat() == null ? "-" : c.getValue().getEtat().name()
        ));
        colCultureDateRecolte.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getDateRecolte()));

        colCultureActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("Modifier");
            private final Button btnDelete = new Button("Supprimer");
            private final HBox   box       = new HBox(8, btnEdit, btnDelete);

            {
                box.setAlignment(Pos.CENTER_LEFT);
                btnEdit.setStyle("-fx-background-color:#2E7D32; -fx-text-fill:white; -fx-padding:6 10; -fx-background-radius:8;");
                btnDelete.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white; -fx-padding:6 10; -fx-background-radius:8;");

                btnEdit.setOnAction(e -> {
                    int i = getIndex();
                    if (i < 0 || i >= getTableView().getItems().size()) return;
                    ouvrirPopupModificationCulture(getTableView().getItems().get(i));
                });

                btnDelete.setOnAction(e -> {
                    int i = getIndex();
                    if (i < 0 || i >= getTableView().getItems().size()) return;
                    supprimerCulture(getTableView().getItems().get(i));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    // ============================================================
    // 3D MOCKUP
    // ============================================================
    private void build3DMockup(Parcelle p, List<Culture> cultures) {
        if (mockupPane == null) return;

        Node bg = (mockupPane.getChildren().isEmpty()) ? null : mockupPane.getChildren().get(0);
        mockupPane.getChildren().clear();
        if (bg != null) mockupPane.getChildren().add(bg);

        double W = Math.min(740, Math.max(520, mockupPane.getWidth() - 40));
        double D = 300;
        double soilThickness = 150;
        double cropThickness = 30;
        double topGap = 1.0;

        double total = Math.max(1.0, p.getSuperficie());

        List<Culture> valid = cultures.stream()
                .filter(c -> c.getTypeCulture() != null && c.getSuperficie() > 0)
                .collect(Collectors.toList());

        double sumCult = valid.stream().mapToDouble(Culture::getSuperficie).sum();
        double scale = (sumCult > total && sumCult > 0) ? (total / sumCult) : 1.0;

        List<Zone> zones = new ArrayList<>();
        for (Culture c : valid) {
            double s = c.getSuperficie() * scale;
            double r = s / total;
            if (r > 0.0001) zones.add(Zone.crop(r, c.getTypeCulture()));
        }

        double usedRatio = zones.stream().mapToDouble(z -> z.ratio).sum();
        double emptyRatio = Math.max(0.0, 1.0 - usedRatio);
        if (emptyRatio > 0.0001) zones.add(Zone.empty(emptyRatio));

        Group world = new Group();

        PhongMaterial soilMat = soilMaterial(p.getTypeTerre());
        Box soil = new Box(W, soilThickness, D);
        soil.setMaterial(soilMat);
        world.getChildren().add(soil);

        Box soilTopCover = new Box(W - 6, 2.4, D - 6);
        soilTopCover.setMaterial(soilMat);
        soilTopCover.setTranslateY(-(soilThickness / 2.0) - 1.2);
        world.getChildren().add(soilTopCover);

        double xLeft = -W / 2.0;

        for (int i = 0; i < zones.size(); i++) {
            Zone z = zones.get(i);
            double wZone = Math.max(30, W * z.ratio);

            if (!z.isEmpty) {
                PhongMaterial cropMat = cultureMaterial(z.type);

                Box cropBase = new Box(wZone, cropThickness, D - 18);
                cropBase.setMaterial(cropMat);
                cropBase.setTranslateX(xLeft + wZone / 2.0);
                cropBase.setTranslateY(-(soilThickness / 2.0) - (cropThickness / 2.0) - topGap);
                world.getChildren().add(cropBase);

                Box cropTop = new Box(wZone - 3, 2.2, (D - 18) - 3);
                cropTop.setMaterial(cropMat);
                cropTop.setTranslateX(cropBase.getTranslateX());
                cropTop.setTranslateY(cropBase.getTranslateY() - (cropThickness / 2.0) - 1.1);
                world.getChildren().add(cropTop);
            }

            if (i < zones.size() - 1) {
                Box sep = new Box(2.0, 3.5, D - 20);
                sep.setMaterial(new PhongMaterial(Color.rgb(255, 255, 255, 0.55)));
                sep.setTranslateX(xLeft + wZone);
                sep.setTranslateY(soilTopCover.getTranslateY() - 2.0);
                world.getChildren().add(sep);
            }

            xLeft += wZone;
        }

        double borderH = 10;
        double borderT = 10;
        PhongMaterial frameMat = new PhongMaterial(Color.WHITE);
        double topY = -(soilThickness / 2.0) - 1.2;

        Box bN = new Box(W + 14, borderH, borderT);
        bN.setMaterial(frameMat);
        bN.setTranslateY(topY - borderH / 2.0);
        bN.setTranslateZ(-(D / 2.0) - (borderT / 2.0));

        Box bS = new Box(W + 14, borderH, borderT);
        bS.setMaterial(frameMat);
        bS.setTranslateY(topY - borderH / 2.0);
        bS.setTranslateZ((D / 2.0) + (borderT / 2.0));

        Box bW = new Box(borderT, borderH, D + 14);
        bW.setMaterial(frameMat);
        bW.setTranslateY(topY - borderH / 2.0);
        bW.setTranslateX(-(W / 2.0) - (borderT / 2.0));

        Box bE = new Box(borderT, borderH, D + 14);
        bE.setMaterial(frameMat);
        bE.setTranslateY(topY - borderH / 2.0);
        bE.setTranslateX((W / 2.0) + (borderT / 2.0));

        AmbientLight amb = new AmbientLight(Color.rgb(255, 255, 255, 0.75));
        PointLight key = new PointLight(Color.WHITE);
        key.setTranslateX(-520); key.setTranslateY(-380); key.setTranslateZ(-820);

        PointLight fill = new PointLight(Color.rgb(255, 255, 255, 0.55));
        fill.setTranslateX(520); fill.setTranslateY(-260); fill.setTranslateZ(-620);

        world.getChildren().addAll(amb, key, fill);

        PerspectiveCamera cam = new PerspectiveCamera(true);
        cam.setNearClip(0.1);
        cam.setFarClip(20000);
        cam.setTranslateX(0);
        cam.setTranslateY(-270);
        cam.setTranslateZ(-1250);
        cam.getTransforms().addAll(
                new Rotate(-24, Rotate.X_AXIS),
                new Rotate(-22, Rotate.Y_AXIS)
        );

        SubScene sub = new SubScene(world, mockupPane.getWidth(), mockupPane.getHeight(), true, SceneAntialiasing.BALANCED);
        sub.setFill(Color.TRANSPARENT);
        sub.setCamera(cam);
        sub.setDepthTest(DepthTest.ENABLE);
        sub.widthProperty().bind(mockupPane.widthProperty());
        sub.heightProperty().bind(mockupPane.heightProperty());

        mockupPane.getChildren().add(sub);

        Platform.runLater(() -> centerWorld(world));
        enableDragRotate(world, mockupPane);
    }

    private static class Zone {
        final double ratio;
        final boolean isEmpty;
        final Culture.TypeCulture type;

        private Zone(double ratio, boolean isEmpty, Culture.TypeCulture type) {
            this.ratio = ratio;
            this.isEmpty = isEmpty;
            this.type = type;
        }

        static Zone crop(double ratio, Culture.TypeCulture type) { return new Zone(ratio, false, type); }
        static Zone empty(double ratio) { return new Zone(ratio, true, null); }
    }

    private void enableDragRotate(Group world, StackPane pane) {
        Rotate ry = new Rotate(-18, Rotate.Y_AXIS);
        Rotate rx = new Rotate(-10, Rotate.X_AXIS);
        world.getTransforms().setAll(ry, rx);

        final double[] anchor = new double[2];
        final double[] angle  = new double[]{ry.getAngle(), rx.getAngle()};

        pane.setOnMousePressed(e -> {
            anchor[0] = e.getSceneX(); anchor[1] = e.getSceneY();
            angle[0]  = ry.getAngle(); angle[1]  = rx.getAngle();
        });

        pane.setOnMouseDragged(e -> {
            ry.setAngle(angle[0] + (e.getSceneX() - anchor[0]) * 0.22);
            rx.setAngle(angle[1] - (e.getSceneY() - anchor[1]) * 0.16);
        });
    }

    private void centerWorld(Group world) {
        Bounds b = world.getBoundsInParent();
        world.setTranslateX(world.getTranslateX() - (b.getMinX() + b.getMaxX()) / 2.0);
        world.setTranslateY(world.getTranslateY() - (b.getMinY() + b.getMaxY()) / 2.0);
        world.setTranslateZ(world.getTranslateZ() - (b.getMinZ() + b.getMaxZ()) / 2.0);
    }

    // ============================================================
    // Background image
    // ============================================================
    private void installBackground3D() {
        if (mockupPane == null) return;

        Image bgImg = loadImageCached("/images/background3d.jpg");
        if (bgImg == null) { System.err.println("[ParcelleDetails] /images/background3d.jpg introuvable"); return; }

        ImageView bgView = new ImageView(bgImg);
        bgView.setPreserveRatio(true);
        bgView.setSmooth(true);
        bgView.setMouseTransparent(true);
        bgView.fitWidthProperty().bind(mockupPane.widthProperty());
        bgView.fitHeightProperty().bind(mockupPane.heightProperty());

        Runnable updateViewport = () -> {
            double paneW = mockupPane.getWidth(), paneH = mockupPane.getHeight();
            if (paneW <= 0 || paneH <= 0) return;
            double imgW = bgImg.getWidth(), imgH = bgImg.getHeight();
            double paneRatio = paneW / paneH, imgRatio = imgW / imgH;
            double vw, vh, vx, vy;
            if (paneRatio > imgRatio) { vw = imgW; vh = imgW / paneRatio; vx = 0;                vy = (imgH - vh) / 2.0; }
            else                      { vh = imgH; vw = imgH * paneRatio; vx = (imgW - vw) / 2.0; vy = 0; }
            bgView.setViewport(new javafx.geometry.Rectangle2D(vx, vy, vw, vh));
        };

        mockupPane.widthProperty().addListener((o, a, b) -> updateViewport.run());
        mockupPane.heightProperty().addListener((o, a, b) -> updateViewport.run());
        Platform.runLater(updateViewport);

        if (!mockupPane.getChildren().isEmpty() && mockupPane.getChildren().get(0) instanceof ImageView)
            mockupPane.getChildren().set(0, bgView);
        else
            mockupPane.getChildren().add(0, bgView);
    }

    // ============================================================
    // Materials / Textures
    // ============================================================
    private PhongMaterial materialWithTextureTiled(String resourcePath, Color fallback, int tileSize, int outW, int outH) {
        PhongMaterial m = new PhongMaterial(fallback);
        Image src = loadImageCached(resourcePath);
        if (src == null) return m;
        Canvas canvas = new Canvas(outW, outH);
        GraphicsContext g = canvas.getGraphicsContext2D();
        double sw = src.getWidth(), sh = src.getHeight();
        double stepX = (tileSize > 0) ? tileSize : sw;
        double stepY = (tileSize > 0) ? tileSize : sh;
        for (double y = 0; y < outH; y += stepY)
            for (double x = 0; x < outW; x += stepX)
                g.drawImage(src, x, y, stepX, stepY);
        WritableImage out = new WritableImage(outW, outH);
        canvas.snapshot(null, out);
        m.setDiffuseMap(out);
        return m;
    }

    private PhongMaterial cultureMaterial(Culture.TypeCulture type) {
        if (type == null) return materialWithTexture("/textures/crops/LEGUMES.png", Color.web("#66BB6A"));
        return switch (type) {
            case BLE          -> materialWithTexture("/textures/crops/BLE.png",           Color.web("#D4B04F"));
            case ORGE         -> materialWithTexture("/textures/crops/ORGE.png",          Color.web("#C8A34A"));
            case MAIS         -> materialWithTexture("/textures/crops/MAIS.png",          Color.web("#F2C94C"));
            case POMME_DE_TERRE -> materialWithTexture("/textures/crops/POMME_DE_TERRE.png", Color.web("#C2A383"));
            case TOMATE       -> materialWithTexture("/textures/crops/TOMATE.png",        Color.web("#C94B4B"));
            case OLIVIER      -> materialWithTexture("/textures/crops/OLIVIER.png",       Color.web("#2E7D32"));
            case AGRUMES      -> materialWithTexture("/textures/crops/AGRUME.png",        Color.web("#F2994A"));
            case VIGNE        -> materialWithTexture("/textures/crops/VIGNE.png",         Color.web("#7B61FF"));
            case FRAISE       -> materialWithTexture("/textures/crops/FRAISE.png",        Color.web("#D81B60"));
            case LEGUMES      -> materialWithTexture("/textures/crops/LEGUMES.png",       Color.web("#66BB6A"));
            default           -> materialWithTexture("/textures/crops/LEGUMES.png",       Color.web("#66BB6A"));
        };
    }

    private PhongMaterial soilMaterial(Parcelle.TypeTerre t) {
        if (t == null) return materialWithTexture("/textures/soil/MIXTE.jpg", Color.web("#C9B48A"));
        return switch (t) {
            case SABLEUSE  -> materialWithTexture("/textures/soil/SABLEUSE.jpg",  Color.web("#E6D3A3"));
            case ARGILEUSE -> materialWithTexture("/textures/soil/ARGILEUSE.jpg", Color.web("#8D6E63"));
            case LIMONEUSE -> materialWithTexture("/textures/soil/LIMONEUSE.jpg", Color.web("#BCAAA4"));
            case HUMIFERE  -> materialWithTexture("/textures/soil/HUMIFERE.jpg",  Color.web("#5D4037"));
            case CALCAIRE  -> materialWithTexture("/textures/soil/CALCAIRE.jpg",  Color.web("#D7CCC8"));
            case SALINE    -> materialWithTexture("/textures/soil/SALINE.jpg",    Color.web("#E0E0E0"));
            case MIXTE     -> materialWithTexture("/textures/soil/MIXTE.jpg",     Color.web("#C9B48A"));
            default        -> materialWithTexture("/textures/soil/MIXTE.jpg",     Color.web("#C9B48A"));
        };
    }

    private PhongMaterial materialWithTexture(String resourcePath, Color fallback) {
        PhongMaterial m = new PhongMaterial(fallback);
        Image img = loadImageCached(resourcePath);
        if (img != null) m.setDiffuseMap(img);
        return m;
    }

    private Image loadImageCached(String resourcePath) {
        if (resourcePath == null) return null;
        if (imageCache.containsKey(resourcePath)) return imageCache.get(resourcePath);
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) { imageCache.put(resourcePath, null); return null; }
            Image img = new Image(is);
            imageCache.put(resourcePath, img);
            return img;
        } catch (Exception e) { imageCache.put(resourcePath, null); return null; }
    }

    // ============================================================
    // NAV
    // ============================================================
    @FXML
    private void retour() {
        naviguerVers("/ListeParcelles.fxml");
    }

    private void naviguerVers(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            StackPane contentArea = null;
            if (root != null && root.getScene() != null) {
                contentArea = (StackPane) root.getScene().lookup("#contentArea");
                if (contentArea == null && root.getScene().getRoot() != null)
                    contentArea = (StackPane) root.getScene().getRoot().lookup("#contentArea");
            }

            if (contentArea != null) contentArea.getChildren().setAll(view);
            else root.setCenter(view);

        } catch (IOException e) {
            showError("Erreur navigation: " + e.getMessage());
        }
    }

    // ============================================================
    // UI helpers
    // ============================================================
    private void showError(String msg) {
        if (errorLabel != null) {
            errorLabel.setText(msg);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    private void hideError() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }

    private String formatSuperficie(double s) {
        if (s == (long) s) return String.valueOf((long) s);
        return String.format(java.util.Locale.US, "%.2f", s);
    }
}