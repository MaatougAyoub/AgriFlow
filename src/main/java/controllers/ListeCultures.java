package controllers;

import entities.Culture;
import entities.Parcelle;
import entities.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import services.ServiceCulture;
import services.ServiceParcelle;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ListeCultures {

    // Filters
    @FXML private ComboBox<String> etatFilterCombo;
    @FXML private ComboBox<String> typeFilterCombo;
    @FXML private TextField searchField;

    @FXML private Button btnRechercher;
    @FXML private Button btnRafraichir;
    @FXML private Button btnAjouterCulture;

    // ✅ bouton retour (ajoute-le dans ton FXML avec onAction="#retourParcellesCultures")
    @FXML private Button btnRetour;

    @FXML private Label countLabel;
    @FXML private Label errorLabel;

    // ADMIN
    @FXML private VBox adminPane;
    @FXML private TableView<Culture> table;
    @FXML private TableColumn<Culture, Integer> colId;
    @FXML private TableColumn<Culture, Integer> colParcelleId;
    @FXML private TableColumn<Culture, Integer> colProprietaireId;
    @FXML private TableColumn<Culture, String> colNom;
    @FXML private TableColumn<Culture, String> colType;
    @FXML private TableColumn<Culture, Double> colSuperficie;
    @FXML private TableColumn<Culture, String> colEtat;
    @FXML private TableColumn<Culture, Object> colDateRecolte;
    @FXML private TableColumn<Culture, Double> colRecolteEstime;
    @FXML private TableColumn<Culture, Object> colDateCreation;
    @FXML private TableColumn<Culture, Void> colActions;

    // AGRICULTEUR
    @FXML private VBox agriculteurPane;
    @FXML private ScrollPane cardsScroll;
    @FXML private VBox cardsContainer;

    private final ServiceCulture sc = new ServiceCulture();
    private final ServiceParcelle sp = new ServiceParcelle();

    // Pour afficher le NOM de la parcelle au lieu de l'ID
    private final Map<Integer, String> parcelleNameById = new HashMap<>();

    // Liste déroulante d'ajout/modif = parcelles du user connecté
    private List<Parcelle> parcellesDuUser = new ArrayList<>();

    // reste (m²) par parcelle
    private final Map<Integer, Double> parcelleResteById = new HashMap<>();

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    @FXML
    public void initialize() {

        // simulation session si vide
        if (MainController.getCurrentUser() == null) {
            User uu = new User(35, "Taaat", "ddd", "emaaail@test.com");
            uu.setRole("AGRICULTEUR");
            MainController.setCurrentUser(uu);
        }

        // combos filtres
        etatFilterCombo.setItems(FXCollections.observableArrayList(
                "TOUT", "EN_COURS", "RECOLTEE", "EN_VENTE", "VENDUE"
        ));
        etatFilterCombo.setValue("TOUT");

        typeFilterCombo.setItems(FXCollections.observableArrayList(
                "TOUT",
                "BLE","ORGE","MAIS","POMME_DE_TERRE","TOMATE","OLIVIER","AGRUMES","VIGNE","FRAISE","LEGUMES","AUTRE"
        ));
        typeFilterCombo.setValue("TOUT");

        // refresh auto
        if (searchField != null) searchField.textProperty().addListener((obs, o, n) -> rafraichir());
        if (etatFilterCombo != null) etatFilterCombo.setOnAction(e -> rafraichir());
        if (typeFilterCombo != null) typeFilterCombo.setOnAction(e -> rafraichir());

        // rôle
        User u = MainController.getCurrentUser();
        boolean admin = isAdmin(u);

        setVisibleManaged(adminPane, admin);
        setVisibleManaged(agriculteurPane, !admin);
        setVisibleManaged(btnAjouterCulture, !admin);

        if (admin) setupAdminTable();

        rafraichir();
    }

    private boolean isAdmin(User u) {
        return u != null && u.getRole() != null && "ADMIN".equalsIgnoreCase(u.getRole());
    }

    private void setVisibleManaged(Node n, boolean v) {
        if (n == null) return;
        n.setVisible(v);
        n.setManaged(v);
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

    // ===== Navigation retour =====
    @FXML
    private void retourParcellesCultures() {
        naviguerVers("/ParcellesCultures.fxml");
    }

    private void naviguerVers(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            Node anyNode = (searchField != null) ? searchField
                    : (table != null) ? table
                    : (cardsScroll != null) ? cardsScroll
                    : null;

            if (anyNode == null || anyNode.getScene() == null) {
                showError("Navigation impossible: scene introuvable.");
                return;
            }

            StackPane contentArea = (StackPane) anyNode.getScene().lookup("#contentArea");
            if (contentArea == null) {
                contentArea = (StackPane) anyNode.getScene().getRoot().lookup("#contentArea");
            }
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            } else {
                showError("Navigation impossible: contentArea introuvable (id=contentArea).");
            }
        } catch (IOException e) {
            showError("Erreur navigation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== ADMIN table =====
    private void setupAdminTable() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colParcelleId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getParcelleId()).asObject());
        colProprietaireId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getProprietaireId()).asObject());
        colNom.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNom()));
        colType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getTypeCulture() == null ? "-" : c.getValue().getTypeCulture().name()
        ));
        colSuperficie.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getSuperficie()).asObject());
        colEtat.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEtat() == null ? "-" : c.getValue().getEtat().name()
        ));
        colDateRecolte.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getDateRecolte()));
        colRecolteEstime.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getRecolteEstime()));
        colDateCreation.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getDateCreation()));

        // ADMIN: supprimer فقط
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Supprimer");
            {
                btnDelete.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white; -fx-padding:6 10; -fx-background-radius:8;");
                btnDelete.setOnAction(e -> {
                    Culture c = getTableView().getItems().get(getIndex());
                    supprimerCulture(c);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
    }

    // ===== Parcelles du user + map id->nom + reste =====
    // ✅ précision: on NE compte PAS les cultures RECOLTEE dans le "used"
    private void preloadParcelles() {
        parcelleNameById.clear();
        parcellesDuUser = new ArrayList<>();
        parcelleResteById.clear();

        try {
            List<Parcelle> allParcelles = sp.recuperer();

            // map id->nom
            for (Parcelle p : allParcelles) {
                parcelleNameById.put(p.getId(), p.getNom());
            }

            User u = MainController.getCurrentUser();
            if (u == null) return;

            if (!isAdmin(u)) {
                parcellesDuUser = allParcelles.stream()
                        .filter(p -> p.getAgriculteurId() == u.getId())
                        .collect(Collectors.toList());

                List<Culture> culturesUser = sc.recuperer().stream()
                        .filter(c -> c.getProprietaireId() == u.getId())
                        .collect(Collectors.toList());

                Map<Integer, Double> usedByParcelle = new HashMap<>();
                for (Culture c : culturesUser) {
                    // ✅ ignore RECOLTEE
                    if (c.getEtat() != null && c.getEtat() == Culture.Etat.RECOLTEE) continue;
                    usedByParcelle.merge(c.getParcelleId(), c.getSuperficie(), Double::sum);
                }

                for (Parcelle p : parcellesDuUser) {
                    double total = p.getSuperficie();
                    double used = usedByParcelle.getOrDefault(p.getId(), 0.0);
                    double reste = total - used;
                    if (reste < 0) reste = 0;
                    parcelleResteById.put(p.getId(), round2(reste));
                }
            }

        } catch (Exception ignored) {}
    }

    // ===== Chargement cultures =====
    private List<Culture> baseListByRole() throws SQLException {
        User u = MainController.getCurrentUser();
        if (u == null) return List.of();

        List<Culture> all = sc.recuperer();
        if (isAdmin(u)) return all;

        int uid = u.getId();
        return all.stream()
                .filter(c -> c.getProprietaireId() == uid)
                .collect(Collectors.toList());
    }

    private List<Culture> applyFilters(List<Culture> list) {
        String q = (searchField == null || searchField.getText() == null) ? "" : searchField.getText().trim().toLowerCase();
        String etat = etatFilterCombo != null ? etatFilterCombo.getValue() : "TOUT";
        String type = typeFilterCombo != null ? typeFilterCombo.getValue() : "TOUT";

        return list.stream()
                .filter(c -> q.isEmpty() || (c.getNom() != null && c.getNom().toLowerCase().contains(q)))
                .filter(c -> "TOUT".equals(etat) || (c.getEtat() != null && c.getEtat().name().equals(etat)))
                .filter(c -> "TOUT".equals(type) || (c.getTypeCulture() != null && c.getTypeCulture().name().equals(type)))
                .collect(Collectors.toList());
    }

    @FXML
    void rafraichir() {
        hideError();
        preloadParcelles();

        try {
            List<Culture> list = applyFilters(baseListByRole());
            if (countLabel != null) countLabel.setText(list.size() + " culture(s)");
            render(list);
        } catch (SQLException e) {
            showError("Erreur DB: " + e.getMessage());
        }
    }

    @FXML
    void rechercher() {
        rafraichir();
    }

    private void render(List<Culture> list) {
        User u = MainController.getCurrentUser();
        if (isAdmin(u)) {
            table.setItems(FXCollections.observableArrayList(list));
        } else {
            renderCards(list);
        }
    }

    // ===== Delete (droits) =====
    private void supprimerCulture(Culture c) {
        User u = MainController.getCurrentUser();
        if (u == null) { showError("Session vide."); return; }

        if (!isAdmin(u) && c.getProprietaireId() != u.getId()) {
            showError("Accès refusé.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la culture ?");
        confirm.setContentText("Culture: " + c.getNom() + " (ID: " + c.getId() + ")");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    sc.supprimer(c);
                    rafraichir();
                } catch (SQLException ex) {
                    showError("Erreur suppression: " + ex.getMessage());
                }
            }
        });
    }

    // ===== AGRICULTEUR cards =====
    private void renderCards(List<Culture> list) {
        cardsContainer.getChildren().clear();
        for (Culture c : list) {
            cardsContainer.getChildren().add(buildCard(c));
        }
    }

    private Node buildCard(Culture c) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: rgba(0,0,0,0.08);" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);"
        );

        String parcelleNom = parcelleNameById.getOrDefault(c.getParcelleId(), "Parcelle #" + c.getParcelleId());

        Label title = new Label(c.getNom() == null ? "(Sans nom)" : c.getNom());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2D5A27;");

        Label l1 = new Label("Parcelle: " + parcelleNom);
        Label l2 = new Label("Type: " + (c.getTypeCulture() == null ? "-" : c.getTypeCulture().name()));
        Label l3 = new Label("Superficie: " + c.getSuperficie() + " m²");
        Label l4 = new Label("Etat: " + (c.getEtat() == null ? "-" : c.getEtat().name()));
        Label l5 = new Label("Date récolte: " + (c.getDateRecolte() == null ? "-" : c.getDateRecolte().toString()));
        Label l6 = new Label("Récolte estimée: " + (c.getRecolteEstime() == null ? "-" : c.getRecolteEstime()) + " Kg");

        for (Label l : List.of(l1,l2,l3,l4,l5,l6)) l.setStyle("-fx-text-fill:#424242;");

        Button btnEdit = new Button("Modifier");
        btnEdit.setStyle("-fx-background-color:#2E7D32; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8;");

        Button btnDelete = new Button("Supprimer");
        btnDelete.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8;");

        btnEdit.setOnAction(e -> ouvrirPopupModificationAgriculteur(c));
        btnDelete.setOnAction(e -> supprimerCulture(c));

        HBox actions = new HBox(10, btnEdit, btnDelete);

        card.getChildren().addAll(title, l1, l2, l3, l4, l5, l6, actions);
        return card;
    }

    // ===== ComboBox Parcelle (nom + reste) =====
    private ComboBox<Parcelle> createParcelleCombo(Parcelle preselect) {
        ComboBox<Parcelle> cb = new ComboBox<>();
        cb.setItems(FXCollections.observableArrayList(parcellesDuUser));

        cb.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Parcelle item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                double reste = parcelleResteById.getOrDefault(item.getId(), 0.0);
                setText(item.getNom() + " (reste: " + reste + " m²)");
            }
        });

        cb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Parcelle item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                double reste = parcelleResteById.getOrDefault(item.getId(), 0.0);
                setText(item.getNom() + " (reste: " + reste + " m²)");
            }
        });

        if (preselect != null) cb.setValue(preselect);
        return cb;
    }

    private Parcelle findParcelleById(int id) {
        for (Parcelle p : parcellesDuUser) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    // ===== AGRICULTEUR: add popup =====
    @FXML
    void ouvrirFenetreAjout() {
        hideError();
        User u = MainController.getCurrentUser();
        if (u == null) { showError("Session vide."); return; }
        if (isAdmin(u)) { showError("ADMIN ne peut pas ajouter."); return; }

        if (parcellesDuUser.isEmpty()) {
            showError("Vous n'avez aucune parcelle. Ajoutez une parcelle avant de créer une culture.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter Culture");
        dialog.setHeaderText("Nouvelle culture");

        ButtonType saveBtn = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        ComboBox<Parcelle> parcelleCombo = createParcelleCombo(parcellesDuUser.get(0));

        TextField nom = new TextField();
        nom.setPromptText("Nom culture");

        ComboBox<Culture.TypeCulture> type = new ComboBox<>(FXCollections.observableArrayList(Culture.TypeCulture.values()));
        type.setPromptText("Type culture");

        TextField superficie = new TextField();
        superficie.setPromptText("ex: 120.5");

        DatePicker dateRecolte = new DatePicker();

        TextField recolteEstime = new TextField();
        recolteEstime.setPromptText("ex: 300");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Parcelle *"), 0, 0);                 grid.add(parcelleCombo, 1, 0);
        grid.add(new Label("Nom *"), 0, 1);                      grid.add(nom, 1, 1);
        grid.add(new Label("Type *"), 0, 2);                     grid.add(type, 1, 2);
        grid.add(new Label("Superficie (m²) *"), 0, 3);          grid.add(superficie, 1, 3);
        grid.add(new Label("Date récolte *"), 0, 4);             grid.add(dateRecolte, 1, 4);
        grid.add(new Label("Récolte estimée (Kg)"), 0, 5);       grid.add(recolteEstime, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result == saveBtn) {
                try {
                    Parcelle selectedParcelle = parcelleCombo.getValue();
                    if (selectedParcelle == null) { showError("Parcelle obligatoire."); return; }

                    String n = nom.getText().trim();
                    Culture.TypeCulture t = type.getValue();
                    double s = Double.parseDouble(superficie.getText().trim());

                    LocalDate ld = dateRecolte.getValue();
                    if (ld == null) { showError("Date récolte obligatoire."); return; }

                    // date création = maintenant
                    LocalDate creation = LocalDate.now();
                    if (!ld.isAfter(creation)) {
                        showError("La date de récolte doit être supérieure à la date de création (" + creation + ").");
                        return;
                    }

                    // superficie <= reste parcelle (reste calcule sans RECOLTEE)
                    double reste = parcelleResteById.getOrDefault(selectedParcelle.getId(), 0.0);
                    if (s <= 0) { showError("Superficie doit être > 0."); return; }
                    if (s > reste + 1e-9) {
                        showError("Superficie trop grande. Reste disponible: " + reste + " m².");
                        return;
                    }

                    Date dr = Date.valueOf(ld);

                    Double re = 0.0;
                    String r = recolteEstime.getText() == null ? "" : recolteEstime.getText().trim();
                    if (!r.isEmpty()) re = Double.parseDouble(r);

                    if (n.isEmpty() || t == null) {
                        showError("Veuillez remplir tous les champs obligatoires.");
                        return;
                    }

                    Culture.Etat etat = Culture.Etat.EN_COURS;

                    Culture c = new Culture(
                            selectedParcelle.getId(), // parcelleId
                            u.getId(),                // proprietaireId
                            n,
                            t,
                            s,
                            etat,
                            dr,
                            re
                    );

                    sc.ajouter(c);
                    rafraichir();

                } catch (NumberFormatException ex) {
                    showError("Superficie / récolte estimée doivent être numériques.");
                } catch (SQLException ex) {
                    showError("Erreur DB: " + ex.getMessage());
                }
            }
        });
    }

    // ===== AGRICULTEUR: edit popup =====
    private void ouvrirPopupModificationAgriculteur(Culture c) {
        User u = MainController.getCurrentUser();
        if (u == null) { showError("Session vide."); return; }
        if (isAdmin(u)) { showError("ADMIN ne peut pas modifier."); return; }
        if (c.getProprietaireId() != u.getId()) { showError("Accès refusé."); return; }

        if (parcellesDuUser.isEmpty()) {
            showError("Vous n'avez aucune parcelle.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Culture");
        dialog.setHeaderText("Modifier: " + (c.getNom() == null ? "" : c.getNom()));

        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        Parcelle current = findParcelleById(c.getParcelleId());
        ComboBox<Parcelle> parcelleCombo = createParcelleCombo(current != null ? current : parcellesDuUser.get(0));

        TextField nom = new TextField(c.getNom());

        ComboBox<Culture.TypeCulture> type = new ComboBox<>(FXCollections.observableArrayList(Culture.TypeCulture.values()));
        type.setValue(c.getTypeCulture());

        TextField superficie = new TextField(String.valueOf(c.getSuperficie()));

        ComboBox<Culture.Etat> etat = new ComboBox<>(FXCollections.observableArrayList(Culture.Etat.values()));
        etat.setValue(c.getEtat());

        DatePicker dateRecolte = new DatePicker(c.getDateRecolte() == null ? null : c.getDateRecolte().toLocalDate());

        TextField recolteEstime = new TextField(c.getRecolteEstime() == null ? "" : String.valueOf(c.getRecolteEstime()));

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Parcelle *"), 0, 0);                 grid.add(parcelleCombo, 1, 0);
        grid.add(new Label("Nom *"), 0, 1);                      grid.add(nom, 1, 1);
        grid.add(new Label("Type *"), 0, 2);                     grid.add(type, 1, 2);
        grid.add(new Label("Superficie (m²) *"), 0, 3);          grid.add(superficie, 1, 3);
        grid.add(new Label("Etat *"), 0, 4);                     grid.add(etat, 1, 4);
        grid.add(new Label("Date récolte *"), 0, 5);             grid.add(dateRecolte, 1, 5);
        grid.add(new Label("Récolte estimée (Kg)"), 0, 6);       grid.add(recolteEstime, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result == saveBtn) {
                try {
                    Parcelle selectedParcelle = parcelleCombo.getValue();
                    if (selectedParcelle == null) { showError("Parcelle obligatoire."); return; }

                    String n = nom.getText().trim();
                    Culture.TypeCulture t = type.getValue();
                    double s = Double.parseDouble(superficie.getText().trim());
                    Culture.Etat e = etat.getValue();

                    LocalDate ld = dateRecolte.getValue();
                    if (ld == null) { showError("Date récolte obligatoire."); return; }

                    // date création réelle (Timestamp -> LocalDate)
                    LocalDate creation = (c.getDateCreation() != null)
                            ? c.getDateCreation().toLocalDateTime().toLocalDate()
                            : LocalDate.now();

                    if (!ld.isAfter(creation)) {
                        showError("La date de récolte doit être supérieure à la date de création (" + creation + ").");
                        return;
                    }

                    // superficie restante (reste calcule sans RECOLTEE)
                    int oldParcelleId = c.getParcelleId();
                    double resteNewParcelle = parcelleResteById.getOrDefault(selectedParcelle.getId(), 0.0);
                    double maxAllowed = (selectedParcelle.getId() == oldParcelleId)
                            ? resteNewParcelle + c.getSuperficie()
                            : resteNewParcelle;

                    if (s <= 0) { showError("Superficie doit être > 0."); return; }
                    if (s > maxAllowed + 1e-9) {
                        showError("Superficie trop grande. Max autorisé: " + round2(maxAllowed) + " m².");
                        return;
                    }

                    Date dr = Date.valueOf(ld);

                    Double re = 0.0;
                    String r = recolteEstime.getText() == null ? "" : recolteEstime.getText().trim();
                    if (!r.isEmpty()) re = Double.parseDouble(r);

                    if (n.isEmpty() || t == null || e == null) {
                        showError("Veuillez remplir tous les champs obligatoires.");
                        return;
                    }

                    c.setParcelleId(selectedParcelle.getId());
                    c.setNom(n);
                    c.setTypeCulture(t);
                    c.setSuperficie(s);
                    c.setEtat(e);
                    c.setDateRecolte(dr);
                    c.setRecolteEstime(re);

                    sc.modifier(c);
                    rafraichir();

                } catch (NumberFormatException ex) {
                    showError("Superficie / récolte estimée doivent être numériques.");
                } catch (SQLException ex) {
                    showError("Erreur modification: " + ex.getMessage());
                }
            }
        });
    }
}
