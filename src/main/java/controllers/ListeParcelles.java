package controllers;

import entities.Parcelle;
import entities.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import services.ServiceParcelle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ListeParcelles {

    // Filters
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilterCombo;
    @FXML private Button btnRechercher;
    @FXML private Button btnRafraichir;
    @FXML private Button btnAjouterParcelle;

    @FXML private Label countLabel;
    @FXML private Label errorLabel;

    // ADMIN
    @FXML private VBox adminPane;
    @FXML private TableView<Parcelle> table;
    @FXML private TableColumn<Parcelle, Integer> colId;
    @FXML private TableColumn<Parcelle, Integer> colAgriculteurId;
    @FXML private TableColumn<Parcelle, String> colNom;
    @FXML private TableColumn<Parcelle, Double> colSuperficie;
    @FXML private TableColumn<Parcelle, String> colTypeTerre;
    @FXML private TableColumn<Parcelle, String> colLocalisation;
    @FXML private TableColumn<Parcelle, Object> colDateCreation;
    @FXML private TableColumn<Parcelle, Void> colActions;

    // AGRICULTEUR
    @FXML private VBox agriculteurPane;
    @FXML private ScrollPane cardsScroll;
    @FXML private VBox cardsContainer;

    private final ServiceParcelle sp = new ServiceParcelle();

    @FXML
    public void initialize() {
        if (MainController.getCurrentUser() == null) {
            User uu = new User(36, "Taaat", "ddd", "emaaail@test.com");
            uu.setRole("AGRICULTEUR");
            MainController.setCurrentUser(uu);
        }

        typeFilterCombo.setItems(FXCollections.observableArrayList(
                "TOUT", "ARGILEUSE", "SABLEUSE", "LIMONEUSE", "CALCAIRE", "HUMIFERE", "SALINE", "MIXTE", "AUTRE"
        ));
        typeFilterCombo.setValue("TOUT");

        if (searchField != null) searchField.textProperty().addListener((obs, o, n) -> rafraichir());
        if (typeFilterCombo != null) typeFilterCombo.setOnAction(e -> rafraichir());

        User u = MainController.getCurrentUser();
        boolean admin = isAdmin(u);

        setVisibleManaged(adminPane, admin);
        setVisibleManaged(agriculteurPane, !admin);
        setVisibleManaged(btnAjouterParcelle, !admin);

        if (admin) setupAdminTable();
        rafraichir();
    }

    private void setupAdminTable() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colAgriculteurId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getAgriculteurId()).asObject());
        colNom.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNom()));
        colSuperficie.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getSuperficie()).asObject());
        colTypeTerre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTypeTerre().name()));
        colLocalisation.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLocalisation()));
        colDateCreation.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getDateCreation()));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Supprimer");
            {
                btnDelete.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white; -fx-padding:6 10; -fx-background-radius:8;");
                btnDelete.setOnAction(e -> supprimerParcelle(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
    }

    private boolean isAdmin(User u) {
        return u != null && u.getRole() != null && "ADMIN".equalsIgnoreCase(u.getRole());
    }

    private void setVisibleManaged(Node n, boolean v) {
        if (n == null) return;
        n.setVisible(v); n.setManaged(v);
    }

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

    // ============================================================
    // HELPERS VALIDATION
    // ============================================================

    /** Retourne null si valide, sinon le message d'erreur. */
    private String validerChamps(String nom, String superficieTxt, Parcelle.TypeTerre type, String localisation) {

        // Nom
        if (nom == null || nom.trim().isEmpty())
            return "Le nom est obligatoire.";
        if (nom.trim().length() < 2)
            return "Le nom doit contenir au moins 2 caractères.";
        if (nom.trim().length() > 100)
            return "Le nom ne doit pas dépasser 100 caractères.";
        if (!nom.trim().matches("[\\p{L}0-9 '\\-_]+"))
            return "Le nom contient des caractères non autorisés.";

        // Superficie
        if (superficieTxt == null || superficieTxt.trim().isEmpty())
            return "La superficie est obligatoire.";
        double superficie;
        try { superficie = Double.parseDouble(superficieTxt.trim()); }
        catch (NumberFormatException e) { return "La superficie doit être un nombre décimal (ex: 500.5)."; }
        if (superficie <= 0)
            return "La superficie doit être supérieure à 0.";
        if (superficie > 10_000_000)
            return "La superficie semble trop grande (maximum 10 000 000 m²).";

        // Type
        if (type == null)
            return "Veuillez sélectionner un type de terre.";

        // Localisation
        if (localisation == null || localisation.trim().isEmpty())
            return "La localisation est obligatoire.";
        if (localisation.trim().length() < 2)
            return "La localisation doit contenir au moins 2 caractères.";
        if (localisation.trim().length() > 150)
            return "La localisation ne doit pas dépasser 150 caractères.";

        return null; // tout est valide ✓
    }

    private boolean isPositiveDouble(String txt) {
        if (txt == null || txt.trim().isEmpty()) return false;
        try { return Double.parseDouble(txt.trim()) > 0; }
        catch (NumberFormatException e) { return false; }
    }

    private void setFieldStyle(TextField tf, boolean valid) {
        tf.setStyle(valid
                ? "-fx-border-color: #A5D6A7; -fx-border-radius: 6; -fx-background-radius: 6;"
                : "-fx-border-color: #EF9A9A; -fx-border-radius: 6; -fx-background-radius: 6;");
    }

    private Label buildInlineErrorLabel() {
        Label lbl = new Label();
        lbl.setVisible(false);
        lbl.setManaged(false);
        lbl.setWrapText(true);
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setStyle(
                "-fx-text-fill: #B71C1C;" +
                        "-fx-background-color: #FFEBEE;" +
                        "-fx-padding: 8 12;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13px;"
        );
        return lbl;
    }

    private void showInlineError(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setVisible(true);
        lbl.setManaged(true);
    }

    // ============================================================
    // DATA
    // ============================================================

    private List<Parcelle> baseListByRole() throws SQLException {
        User u = MainController.getCurrentUser();
        if (u == null) return List.of();
        List<Parcelle> all = sp.recuperer();
        if (isAdmin(u)) return all;
        int uid = u.getId();
        return all.stream().filter(p -> p.getAgriculteurId() == uid).collect(Collectors.toList());
    }

    private List<Parcelle> applySearchAndType(List<Parcelle> list) {
        String q = (searchField == null || searchField.getText() == null) ? "" : searchField.getText().trim().toLowerCase();
        String type = typeFilterCombo != null ? typeFilterCombo.getValue() : "TOUT";
        return list.stream()
                .filter(p -> q.isEmpty()
                        || (p.getNom() != null && p.getNom().toLowerCase().contains(q))
                        || (p.getLocalisation() != null && p.getLocalisation().toLowerCase().contains(q)))
                .filter(p -> "TOUT".equals(type) || p.getTypeTerre().name().equals(type))
                .collect(Collectors.toList());
    }

    @FXML
    void rafraichir() {
        hideError();
        try {
            List<Parcelle> list = applySearchAndType(baseListByRole());
            render(list);
        } catch (SQLException e) {
            showError("Erreur DB: " + e.getMessage());
        }
    }

    @FXML
    void rechercher() { rafraichir(); }

    private void render(List<Parcelle> list) {
        if (countLabel != null) countLabel.setText(list.size() + " parcelle(s)");
        User u = MainController.getCurrentUser();
        if (isAdmin(u)) table.setItems(FXCollections.observableArrayList(list));
        else renderCards(list);
    }

    private void supprimerParcelle(Parcelle p) {
        User u = MainController.getCurrentUser();
        if (u == null) { showError("Session vide."); return; }
        if (!isAdmin(u) && p.getAgriculteurId() != u.getId()) { showError("Accès refusé."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la parcelle ?");
        confirm.setContentText("Parcelle: " + p.getNom() + " (ID: " + p.getId() + ")");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try { sp.supprimer(p); rafraichir(); }
                catch (SQLException ex) { showError("Erreur suppression: " + ex.getMessage()); }
            }
        });
    }

    // ============================================================
    // CARDS
    // ============================================================

    private void renderCards(List<Parcelle> list) {
        cardsContainer.getChildren().clear();
        for (Parcelle p : list) cardsContainer.getChildren().add(buildCard(p));
    }

    private Node buildCard(Parcelle p) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));

        final String baseStyle =
                "-fx-background-color: white; -fx-background-radius: 14; -fx-border-radius: 14;" +
                        "-fx-border-color: rgba(0,0,0,0.07); -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 14, 0, 0, 4);";
        final String hoverStyle =
                "-fx-background-color: white; -fx-background-radius: 14; -fx-border-radius: 14;" +
                        "-fx-border-color: rgba(46,125,50,0.25); -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 18, 0, 0, 6);";

        card.setStyle(baseStyle);
        card.setOnMouseClicked(e -> { if (e.getTarget() instanceof Button) return; openParcelleDetailsPage(p.getId()); });
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(baseStyle));

        Label title = new Label(p.getNom() == null ? "(Sans nom)" : p.getNom());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #2D5A27;");

        Label badgeType = createChip(p.getTypeTerre() == null ? "TYPE -" : p.getTypeTerre().name(), "#E8F5E9", "#2E7D32");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(10, title, spacer, badgeType);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox left  = new VBox(6, buildInfoRow("Superficie", formatSuperficie(p.getSuperficie()) + " m²"), buildInfoRow("Localisation", safe(p.getLocalisation())));
        VBox right = new VBox(6, buildInfoRow("Créée le", p.getDateCreation() == null ? "-" : p.getDateCreation().toString()), buildInfoRow("ID", String.valueOf(p.getId())));
        HBox infos = new HBox(24, left, right);
        infos.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        Button btnEdit   = new Button("Modifier");
        Button btnDelete = new Button("Supprimer");
        btnEdit.setStyle("-fx-background-color:#2E7D32; -fx-text-fill:white; -fx-padding:8 14; -fx-background-radius:10; -fx-font-weight:700;");
        btnDelete.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white; -fx-padding:8 14; -fx-background-radius:10; -fx-font-weight:700;");
        btnEdit.setOnAction(e -> ouvrirPopupModificationAgriculteur(p));
        btnDelete.setOnAction(e -> supprimerParcelle(p));

        Region spacer2 = new Region(); HBox.setHgrow(spacer2, Priority.ALWAYS);
        HBox actions = new HBox(10, spacer2, btnEdit, btnDelete);

        Separator sep = new Separator(); sep.setStyle("-fx-opacity: 0.35;");
        card.getChildren().addAll(header, infos, sep, actions);
        return card;
    }

    // ============================================================
    // AJOUT — popup avec validation inline
    // ============================================================
    @FXML
    void ouvrirFenetreAjout() {
        hideError();
        User u = MainController.getCurrentUser();
        if (u == null) { showError("Session vide."); return; }
        if (isAdmin(u)) { showError("ADMIN ne peut pas ajouter."); return; }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter Parcelle");
        dialog.setHeaderText("Nouvelle parcelle");
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefWidth(500);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        ButtonType saveBtn = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        Label inlineError = buildInlineErrorLabel();

        // Champs
        TextField nom       = new TextField(); nom.setPromptText("ex: Champ nord");
        TextField superficie = new TextField(); superficie.setPromptText("ex: 500");
        ComboBox<Parcelle.TypeTerre> type = new ComboBox<>(FXCollections.observableArrayList(Parcelle.TypeTerre.values()));
        type.setPromptText("Type Terre");
        type.setMaxWidth(Double.MAX_VALUE);
        TextField loc = new TextField(); loc.setPromptText("ex: Tunis");

        // Feedback visuel temps réel
        nom.textProperty().addListener((obs, o, n)        -> setFieldStyle(nom, n.trim().length() >= 2));
        superficie.textProperty().addListener((obs, o, n) -> setFieldStyle(superficie, isPositiveDouble(n)));
        loc.textProperty().addListener((obs, o, n)        -> setFieldStyle(loc, n.trim().length() >= 2));

        // Grille
        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(12));
        grid.setMaxWidth(Double.MAX_VALUE);
        ColumnConstraints c0 = new ColumnConstraints(140);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c0, c1);

        grid.add(new Label("Nom *"),         0, 0); grid.add(nom,        1, 0);
        grid.add(new Label("Superficie *"),  0, 1); grid.add(superficie, 1, 1);
        grid.add(new Label("Type Terre *"),  0, 2); grid.add(type,       1, 2);
        grid.add(new Label("Localisation *"),0, 3); grid.add(loc,        1, 3);

        VBox content = new VBox(10, inlineError, grid);
        content.setPadding(new Insets(4));
        dialog.getDialogPane().setContent(content);

        // Bloquer la fermeture si invalide
        Button okButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String err = validerChamps(nom.getText(), superficie.getText(), type.getValue(), loc.getText());
            if (err != null) {
                event.consume();
                showInlineError(inlineError, err);
            }
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == saveBtn) {
                try {
                    Parcelle p = new Parcelle(
                            u.getId(),
                            nom.getText().trim(),
                            Double.parseDouble(superficie.getText().trim()),
                            type.getValue(),
                            loc.getText().trim()
                    );
                    sp.ajouter(p);
                    rafraichir();
                } catch (NumberFormatException ex) {
                    showError("Erreur numérique inattendue: " + ex.getMessage());
                } catch (SQLException ex) {
                    showError("Erreur DB: " + ex.getMessage());
                }
            }
        });
    }

    // ============================================================
    // MODIFICATION — popup avec validation inline
    // ============================================================
    private void ouvrirPopupModificationAgriculteur(Parcelle p) {
        User u = MainController.getCurrentUser();
        if (u == null) { showError("Session vide."); return; }
        if (isAdmin(u)) { showError("ADMIN ne peut pas modifier."); return; }
        if (p.getAgriculteurId() != u.getId()) { showError("Accès refusé."); return; }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Parcelle");
        dialog.setHeaderText("Modifier : " + (p.getNom() == null ? "" : p.getNom()));
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefWidth(500);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        Label inlineError = buildInlineErrorLabel();

        // Champs pré-remplis
        TextField nomField        = new TextField(p.getNom());          nomField.setPromptText("ex: Champ nord");
        TextField superficieField = new TextField(String.valueOf(p.getSuperficie())); superficieField.setPromptText("ex: 500");
        ComboBox<Parcelle.TypeTerre> typeCombo = new ComboBox<>(FXCollections.observableArrayList(Parcelle.TypeTerre.values()));
        typeCombo.setValue(p.getTypeTerre());
        typeCombo.setMaxWidth(Double.MAX_VALUE);
        TextField locField = new TextField(p.getLocalisation()); locField.setPromptText("ex: Tunis");

        // Feedback visuel temps réel
        nomField.textProperty().addListener((obs, o, n)        -> setFieldStyle(nomField, n.trim().length() >= 2));
        superficieField.textProperty().addListener((obs, o, n) -> setFieldStyle(superficieField, isPositiveDouble(n)));
        locField.textProperty().addListener((obs, o, n)        -> setFieldStyle(locField, n.trim().length() >= 2));

        // Grille
        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(12));
        grid.setMaxWidth(Double.MAX_VALUE);
        ColumnConstraints c0 = new ColumnConstraints(140);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c0, c1);

        grid.add(new Label("Nom *"),         0, 0); grid.add(nomField,        1, 0);
        grid.add(new Label("Superficie *"),  0, 1); grid.add(superficieField, 1, 1);
        grid.add(new Label("Type Terre *"),  0, 2); grid.add(typeCombo,       1, 2);
        grid.add(new Label("Localisation *"),0, 3); grid.add(locField,        1, 3);

        VBox content = new VBox(10, inlineError, grid);
        content.setPadding(new Insets(4));
        dialog.getDialogPane().setContent(content);

        // Bloquer la fermeture si invalide
        Button okButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String err = validerChamps(nomField.getText(), superficieField.getText(), typeCombo.getValue(), locField.getText());
            if (err != null) {
                event.consume();
                showInlineError(inlineError, err);
            }
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == saveBtn) {
                try {
                    p.setNom(nomField.getText().trim());
                    p.setSuperficie(Double.parseDouble(superficieField.getText().trim()));
                    p.setTypeTerre(typeCombo.getValue());
                    p.setLocalisation(locField.getText().trim());
                    sp.modifier(p);
                    rafraichir();
                } catch (NumberFormatException ex) {
                    showError("Erreur numérique inattendue: " + ex.getMessage());
                } catch (SQLException ex) {
                    showError("Erreur modification: " + ex.getMessage());
                }
            }
        });
    }

    // ============================================================
    // NAVIGATION
    // ============================================================
    @FXML
    private void retourParcellesCultures() { naviguerVers("/ParcellesCultures.fxml"); }

    private void naviguerVers(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            StackPane contentArea = null;

            if (table != null && table.getScene() != null) {
                contentArea = (StackPane) table.getScene().lookup("#contentArea");
                if (contentArea == null && table.getScene().getRoot() != null)
                    contentArea = (StackPane) table.getScene().getRoot().lookup("#contentArea");
            }
            if (contentArea == null && btnRafraichir != null && btnRafraichir.getScene() != null) {
                contentArea = (StackPane) btnRafraichir.getScene().lookup("#contentArea");
                if (contentArea == null && btnRafraichir.getScene().getRoot() != null)
                    contentArea = (StackPane) btnRafraichir.getScene().getRoot().lookup("#contentArea");
            }
            if (contentArea != null) contentArea.getChildren().setAll(view);
            else System.err.println("[ListeParcelles] contentArea introuvable.");
        } catch (Exception e) {
            System.err.println("[ListeParcelles] Erreur navigation: " + e.getMessage());
        }
    }

    private void openParcelleDetailsPage(int parcelleId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParcelleDetails.fxml"));
            Parent view = loader.load();
            ParcelleDetails controller = loader.getController();
            controller.setParcelleId(parcelleId);

            StackPane contentArea = (StackPane) cardsScroll.getScene().lookup("#contentArea");
            if (cardsScroll != null && cardsScroll.getScene() != null) {
                contentArea = (StackPane) cardsScroll.getScene().lookup("#contentArea");
                if (contentArea == null && cardsScroll.getScene().getRoot() != null)
                    contentArea = (StackPane) cardsScroll.getScene().getRoot().lookup("#contentArea");
            }
            if (contentArea != null) contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            showError("Erreur ouverture détails parcelle: " + e.getMessage());
        }
    }

    // ============================================================
    // UI HELPERS
    // ============================================================
    private Label createChip(String text, String bg, String fg) {
        Label chip = new Label(text);
        chip.setStyle("-fx-background-color:" + bg + "; -fx-text-fill:" + fg + "; -fx-font-weight:800; -fx-padding:6 10; -fx-background-radius:999; -fx-border-radius:999; -fx-border-color: rgba(0,0,0,0.06);");
        return chip;
    }

    private HBox buildInfoRow(String key, String value) {
        Label k = new Label(key + ":"); k.setStyle("-fx-text-fill:#757575; -fx-font-weight:700;");
        Label v = new Label(value == null || value.isBlank() ? "-" : value); v.setStyle("-fx-text-fill:#212121; -fx-font-weight:800;");
        HBox row = new HBox(8, k, v); row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return row;
    }

    private String safe(String s) { return (s == null || s.isBlank()) ? "-" : s; }

    private String formatSuperficie(double s) {
        if (s == (long) s) return String.valueOf((long) s);
        return String.format(java.util.Locale.US, "%.2f", s);
    }
}