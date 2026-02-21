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
        if (MainController.getCurrentUser() == null) {User uu=
                new User(36, "Taaat", "ddd", "emaaail@test.com");uu.setRole("AGRICULTEUR");
            MainController.setCurrentUser(uu);

        }
        // Combo type
        typeFilterCombo.setItems(FXCollections.observableArrayList(
                "TOUT", "ARGILEUSE", "SABLEUSE", "LIMONEUSE", "CALCAIRE", "HUMIFERE", "SALINE", "MIXTE", "AUTRE"
        ));
        typeFilterCombo.setValue("TOUT");

        // Recherche en temps réel (comme Marketplace)
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldV, newV) -> rafraichir());
        }
        if (typeFilterCombo != null) {
            typeFilterCombo.setOnAction(e -> rafraichir());
        }

        // Déterminer rôle + afficher bonne UI
        User u = MainController.getCurrentUser();
        boolean admin = isAdmin(u);

        setVisibleManaged(adminPane, admin);
        setVisibleManaged(agriculteurPane, !admin);
        setVisibleManaged(btnAjouterParcelle, !admin);

        // Table ADMIN
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

        // ADMIN: فقط supprimer (pas modifier)
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Supprimer");
            {
                btnDelete.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white; -fx-padding:6 10; -fx-background-radius:8;");
                btnDelete.setOnAction(e -> {
                    Parcelle p = getTableView().getItems().get(getIndex());
                    supprimerParcelle(p);
                });
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

    // ===== Data loading =====

    private List<Parcelle> baseListByRole() throws SQLException {
        User u = MainController.getCurrentUser();
        if (u == null) return List.of();

        List<Parcelle> all = sp.recuperer();
        if (isAdmin(u)) return all;

        int uid = u.getId();
        return all.stream()
                .filter(p -> p.getAgriculteurId() == uid)
                .collect(Collectors.toList());
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
    void rechercher() {
        rafraichir();
    }

    private void render(List<Parcelle> list) {
        if (countLabel != null) countLabel.setText(list.size() + " parcelle(s)");

        User u = MainController.getCurrentUser();
        if (isAdmin(u)) {
            table.setItems(FXCollections.observableArrayList(list));
        } else {
            renderCards(list);
        }
    }

    // ===== ADMIN + AGRICULTEUR: delete (avec droits) =====

    private void supprimerParcelle(Parcelle p) {
        User u = MainController.getCurrentUser();
        if (u == null) {
            showError("Session vide.");
            return;
        }

        // AGRICULTEUR: فقط يقدر يحذف متاعو
        if (!isAdmin(u) && p.getAgriculteurId() != u.getId()) {
            showError("Accès refusé.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la parcelle ?");
        confirm.setContentText("Parcelle: " + p.getNom() + " (ID: " + p.getId() + ")");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    sp.supprimer(p);
                    rafraichir();
                } catch (SQLException ex) {
                    showError("Erreur suppression: " + ex.getMessage());
                }
            }
        });
    }

    // ===== AGRICULTEUR: cards =====

    private void renderCards(List<Parcelle> list) {
        cardsContainer.getChildren().clear();
        for (Parcelle p : list) {
            cardsContainer.getChildren().add(buildCard(p));
        }
    }

    private Node buildCard(Parcelle p) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: rgba(0,0,0,0.08);" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);"
        );

        Label title = new Label(p.getNom() == null ? "(Sans nom)" : p.getNom());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2D5A27;");

        Label l1 = new Label("Superficie: " + p.getSuperficie());
        Label l2 = new Label("Type: " + (p.getTypeTerre() == null ? "-" : p.getTypeTerre().name()));
        Label l3 = new Label("Localisation: " + (p.getLocalisation() == null ? "-" : p.getLocalisation()));
        Label l4 = new Label("Créée: " + (p.getDateCreation() == null ? "-" : p.getDateCreation().toString()));

        l1.setStyle("-fx-text-fill:#424242;");
        l2.setStyle("-fx-text-fill:#424242;");
        l3.setStyle("-fx-text-fill:#424242;");
        l4.setStyle("-fx-text-fill:#616161;");

        Button btnEdit = new Button("Modifier");
        btnEdit.setStyle("-fx-background-color:#2E7D32; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8;");

        Button btnDelete = new Button("Supprimer");
        btnDelete.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8;");

        btnEdit.setOnAction(e -> ouvrirPopupModificationAgriculteur(p));
        btnDelete.setOnAction(e -> supprimerParcelle(p));

        HBox actions = new HBox(10, btnEdit, btnDelete);

        card.getChildren().addAll(title, l1, l2, l3, l4, actions);
        return card;
    }

    private void ouvrirPopupModificationAgriculteur(Parcelle p) {
        User u = MainController.getCurrentUser();
        if (u == null) {
            showError("Session vide.");
            return;
        }
        if (isAdmin(u)) {
            showError("ADMIN ne peut pas modifier.");
            return;
        }
        if (p.getAgriculteurId() != u.getId()) {
            showError("Accès refusé.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Parcelle");
        dialog.setHeaderText("Modifier: " + (p.getNom() == null ? "" : p.getNom()));

        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField nomFieldEdit = new TextField(p.getNom());
        TextField superficieFieldEdit = new TextField(String.valueOf(p.getSuperficie()));
        ComboBox<Parcelle.TypeTerre> typeComboEdit = new ComboBox<>(FXCollections.observableArrayList(Parcelle.TypeTerre.values()));
        typeComboEdit.setValue(p.getTypeTerre());
        TextField localisationFieldEdit = new TextField(p.getLocalisation());

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Nom *"), 0, 0);
        grid.add(nomFieldEdit, 1, 0);

        grid.add(new Label("Superficie *"), 0, 1);
        grid.add(superficieFieldEdit, 1, 1);

        grid.add(new Label("Type Terre *"), 0, 2);
        grid.add(typeComboEdit, 1, 2);

        grid.add(new Label("Localisation *"), 0, 3);
        grid.add(localisationFieldEdit, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result == saveBtn) {
                try {
                    String nom = nomFieldEdit.getText().trim();
                    String loc = localisationFieldEdit.getText().trim();
                    Parcelle.TypeTerre type = typeComboEdit.getValue();
                    double superficie = Double.parseDouble(superficieFieldEdit.getText().trim());

                    if (nom.isEmpty() || loc.isEmpty() || type == null) {
                        showError("Veuillez remplir tous les champs obligatoires.");
                        return;
                    }

                    p.setNom(nom);
                    p.setLocalisation(loc);
                    p.setTypeTerre(type);
                    p.setSuperficie(superficie);

                    sp.modifier(p);
                    rafraichir();
                } catch (NumberFormatException ex) {
                    showError("Superficie doit être numérique.");
                } catch (SQLException ex) {
                    showError("Erreur modification: " + ex.getMessage());
                }
            }
        });
    }
    @FXML
    private void retourParcellesCultures() {
        naviguerVers("/ParcellesCultures.fxml");
    }

    private void naviguerVers(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // récupère contentArea depuis la Scene
            StackPane contentArea = null;

            if (table != null && table.getScene() != null) { // si tu as une table admin
                contentArea = (StackPane) table.getScene().lookup("#contentArea");
                if (contentArea == null) {
                    Parent sceneRoot = table.getScene().getRoot();
                    if (sceneRoot != null) contentArea = (StackPane) sceneRoot.lookup("#contentArea");
                }
            }

            if (contentArea == null && btnRafraichir != null && btnRafraichir.getScene() != null) { // fallback
                contentArea = (StackPane) btnRafraichir.getScene().lookup("#contentArea");
                if (contentArea == null) {
                    Parent sceneRoot = btnRafraichir.getScene().getRoot();
                    if (sceneRoot != null) contentArea = (StackPane) sceneRoot.lookup("#contentArea");
                }
            }

            if (contentArea == null) {
                System.err.println("[ListeParcelles] contentArea introuvable (#contentArea).");
                return;
            }

            contentArea.getChildren().setAll(view);

        } catch (Exception e) {
            System.err.println("[ListeParcelles] Erreur navigation vers " + fxmlPath + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== AGRICULTEUR: add (popup window) =====

    @FXML
    void ouvrirFenetreAjout() {
        hideError();

        User u = MainController.getCurrentUser();
        if (u == null) {
            showError("Session vide.");
            return;
        }
        if (isAdmin(u)) {
            showError("ADMIN ne peut pas ajouter.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter Parcelle");
        dialog.setHeaderText("Nouvelle parcelle");

        ButtonType saveBtn = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField nom = new TextField();
        nom.setPromptText("Nom de la parcelle");

        TextField superficie = new TextField();
        superficie.setPromptText("ex: 500");

        ComboBox<Parcelle.TypeTerre> type = new ComboBox<>(FXCollections.observableArrayList(Parcelle.TypeTerre.values()));
        type.setPromptText("Type Terre");

        TextField loc = new TextField();
        loc.setPromptText("ex: Tunis");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Nom *"), 0, 0);
        grid.add(nom, 1, 0);

        grid.add(new Label("Superficie *"), 0, 1);
        grid.add(superficie, 1, 1);

        grid.add(new Label("Type Terre *"), 0, 2);
        grid.add(type, 1, 2);

        grid.add(new Label("Localisation *"), 0, 3);
        grid.add(loc, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result == saveBtn) {
                try {
                    String n = nom.getText().trim();
                    String l = loc.getText().trim();
                    Parcelle.TypeTerre t = type.getValue();
                    double s = Double.parseDouble(superficie.getText().trim());

                    if (n.isEmpty() || l.isEmpty() || t == null) {
                        showError("Veuillez remplir tous les champs obligatoires.");
                        return;
                    }

                    Parcelle p = new Parcelle(u.getId(), n, s, t, l);
                    sp.ajouter(p);
                    rafraichir();

                } catch (NumberFormatException ex) {
                    showError("Superficie doit être numérique.");
                } catch (SQLException ex) {
                    showError("Erreur DB: " + ex.getMessage());
                }
            }
        });
    }
}
