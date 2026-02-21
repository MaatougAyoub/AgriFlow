package controllers;

import entities.Culture;
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
import services.UserService;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CulturesEnVente {

    // Root (IMPORTANT pour éviter getScene()==null)
    @FXML private BorderPane root; // mets fx:id="root" dans ton FXML
    @FXML private Label errorLabel;
    @FXML private Label countLabel;

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
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        User u = MainController.getCurrentUser();
        if (MainController.getCurrentUser() == null) {User uu=
                new User(36, "Taaat", "ddd", "emaaail@test.com");uu.setRole("AGRICULTEUR");
            MainController.setCurrentUser(uu);

        }
        boolean admin = isAdmin(u);

        setVisibleManaged(adminPane, admin);
        setVisibleManaged(agriculteurPane, !admin);

        if (admin) setupAdminTable();

        rafraichir();
    }

    // ================== ACTIONS NAV ==================
    @FXML
    private void retourParcellesCultures() {
        naviguerVers("/ParcellesCultures.fxml");
    }

    private void naviguerVers(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Cherche contentArea dans la scène (MainController l’a)
            StackPane contentArea = null;
            if (root != null && root.getScene() != null) {
                contentArea = (StackPane) root.getScene().lookup("#contentArea");
            }
            if (contentArea == null && root != null) {
                // fallback: si root est déjà dans contentArea
                Parent sceneRoot = root.getScene() != null ? root.getScene().getRoot() : null;
                if (sceneRoot != null) contentArea = (StackPane) sceneRoot.lookup("#contentArea");
            }

            if (contentArea != null) {
                contentArea.getChildren().setAll(view);
            } else {
                // fallback extrême: remplacer le center si root est un BorderPane
                if (root != null) root.setCenter(view);
            }

        } catch (IOException e) {
            showError("Erreur navigation vers " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================== REFRESH ==================
    @FXML
    private void rafraichir() {
        hideError();
        try {
            List<Culture> list = sc.recuperer().stream()
                    .filter(c -> c.getEtat() != null && "EN_VENTE".equalsIgnoreCase(c.getEtat().name()))
                    .collect(Collectors.toList());

            if (countLabel != null) countLabel.setText(list.size() + " culture(s) en vente");

            User u = MainController.getCurrentUser();
            if (isAdmin(u)) {
                table.setItems(FXCollections.observableArrayList(list));
            } else {
                renderCards(list);
            }

        } catch (SQLException e) {
            showError("Erreur DB: " + e.getMessage());
        }
    }

    // ================== ADMIN TABLE ==================
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

        // Admin: supprimer uniquement
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Supprimer");
            {
                btnDelete.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white; -fx-padding:6 10; -fx-background-radius:8;");
                btnDelete.setOnAction(e -> {
                    Culture c = getTableView().getItems().get(getIndex());
                    supprimer(c);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
    }

    // ================== AGRICULTEUR CARDS ==================
    private void renderCards(List<Culture> list) {
        if (cardsContainer == null) return;
        cardsContainer.getChildren().clear();

        User me = MainController.getCurrentUser();
        int myId = me != null ? me.getId() : -1;

        for (Culture c : list) {
            boolean mine = (myId != -1 && c.getProprietaireId() == myId);
            cardsContainer.getChildren().add(buildCard(c, mine));
        }
    }

    private Node buildCard(Culture c, boolean mine) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setStyle(
                "-fx-background-color: " + (mine ? "#E8F5E9" : "white") + ";" +   // verte si c'est la sienne
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: rgba(0,0,0,0.10);" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);"
        );

        Label title = new Label(c.getNom() == null ? "(Sans nom)" : c.getNom());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2D5A27;");

        Label l1 = new Label("Type: " + (c.getTypeCulture() == null ? "-" : c.getTypeCulture().name()));
        Label l2 = new Label("Superficie: " + c.getSuperficie() + " m²");
        Label l3 = new Label("Date récolte: " + (c.getDateRecolte() == null ? "-" : c.getDateRecolte().toString()));
        Label l4 = new Label("Récolte estimée: " + (c.getRecolteEstime() == null ? "-" : c.getRecolteEstime()) + " Kg");

        for (Label l : List.of(l1, l2, l3, l4)) l.setStyle("-fx-text-fill:#424242;");

        Button primary = new Button(mine ? "Modifier" : "Contacter");
        primary.setStyle("-fx-background-color:#2E7D32; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8;");

        Button delete = new Button("Supprimer");
        delete.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8;");

        // Droits:
        // - Agricultueur: s'il est propriétaire => Modifier + Supprimer
        // - Sinon => Contacter seulement
        if (mine) {
            primary.setOnAction(e -> ouvrirPopupModification(c));
            delete.setOnAction(e -> supprimer(c));
            card.getChildren().addAll(title, l1, l2, l3, l4, new HBox(10, primary, delete));
        } else {
            primary.setOnAction(e -> contacterProprietaire(c));
            card.getChildren().addAll(title, l1, l2, l3, l4, primary);
        }

        return card;
    }

    // ================== CONTACT EMAIL (Option B: recupererParId) ==================
    private void contacterProprietaire(Culture c) {
        hideError();
        int ownerId = c.getProprietaireId();

        String email = getOwnerEmail(ownerId);
        if (email == null) {
            showError("Email du propriétaire introuvable (ID=" + ownerId + ")");
            return;
        }

        String subject = "Demande concernant votre culture: " + (c.getNom() == null ? "" : c.getNom());
        String body = "Bonjour,\n\nJe vous contacte via AgriFlow à propos de votre culture en vente.\n" +
                "Culture: " + (c.getNom() == null ? "-" : c.getNom()) + "\n" +
                "Superficie: " + c.getSuperficie() + " m²\n\n" +
                "Merci.";

        ouvrirEmailClient(email, subject, body);
    }

    private String getOwnerEmail(int ownerId) {
        try {
            User u = userService.recupererParId(ownerId);
            if (u == null) return null;
            String email = u.getEmail();
            return (email == null || email.isBlank()) ? null : email.trim();
        } catch (SQLException e) {
            showError("Erreur DB (email propriétaire): " + e.getMessage());
            return null;
        }
    }

    private void ouvrirEmailClient(String to, String subject, String body) {
        try {
            String mailto = "mailto:" + URLEncoder.encode(to, StandardCharsets.UTF_8)
                    + "?subject=" + URLEncoder.encode(subject, StandardCharsets.UTF_8)
                    + "&body=" + URLEncoder.encode(body, StandardCharsets.UTF_8);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().mail(new URI(mailto));
            } else {
                showError("Ouverture email non supportée sur ce système.");
            }
        } catch (Exception e) {
            showError("Impossible d'ouvrir le client mail: " + e.getMessage());
        }
    }

    // ================== MODIFIER / SUPPRIMER ==================
    private void supprimer(Culture c) {
        User me = MainController.getCurrentUser();
        if (me == null) { showError("Session vide."); return; }

        boolean admin = isAdmin(me);
        boolean mine = c.getProprietaireId() == me.getId();

        if (!admin && !mine) {
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

    // Popup simple pour modifier (si c'est la sienne)
    private void ouvrirPopupModification(Culture c) {
        User me = MainController.getCurrentUser();
        if (me == null) { showError("Session vide."); return; }
        if (isAdmin(me)) { showError("ADMIN ne peut pas modifier."); return; }
        if (c.getProprietaireId() != me.getId()) { showError("Accès refusé."); return; }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier culture en vente");
        dialog.setHeaderText("Modifier: " + (c.getNom() == null ? "" : c.getNom()));

        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField nom = new TextField(c.getNom());
        TextField superficie = new TextField(String.valueOf(c.getSuperficie()));
        TextField recolteEstime = new TextField(c.getRecolteEstime() == null ? "" : String.valueOf(c.getRecolteEstime()));

        DatePicker dateRecolte = new DatePicker(
                c.getDateRecolte() == null ? null : c.getDateRecolte().toLocalDate()
        );

        ComboBox<Culture.Etat> etat = new ComboBox<>(FXCollections.observableArrayList(Culture.Etat.values()));
        etat.setValue(c.getEtat());

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Nom *"), 0, 0);                      grid.add(nom, 1, 0);
        grid.add(new Label("Superficie (m²) *"), 0, 1);          grid.add(superficie, 1, 1);
        grid.add(new Label("Date récolte *"), 0, 2);             grid.add(dateRecolte, 1, 2);
        grid.add(new Label("Récolte estimée (Kg)"), 0, 3);       grid.add(recolteEstime, 1, 3);
        grid.add(new Label("État *"), 0, 4);                     grid.add(etat, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result == saveBtn) {
                try {
                    String n = nom.getText() == null ? "" : nom.getText().trim();
                    if (n.isEmpty()) { showError("Nom obligatoire."); return; }

                    double s = Double.parseDouble(superficie.getText().trim());
                    if (s <= 0) { showError("Superficie doit être > 0."); return; }

                    LocalDate dr = dateRecolte.getValue();
                    if (dr == null) { showError("Date récolte obligatoire."); return; }

                    // contrôle date récolte > date création (si connue)
                    LocalDate creation = (c.getDateCreation() != null)
                            ? c.getDateCreation().toLocalDateTime().toLocalDate()
                            : LocalDate.now();

                    if (!dr.isAfter(creation)) {
                        showError("La date de récolte doit être supérieure à la date de création (" + creation + ").");
                        return;
                    }

                    Double re = 0.0;
                    String r = recolteEstime.getText() == null ? "" : recolteEstime.getText().trim();
                    if (!r.isEmpty()) re = Double.parseDouble(r);

                    Culture.Etat et = etat.getValue();
                    if (et == null) { showError("État obligatoire."); return; }

                    c.setNom(n);
                    c.setSuperficie(s);
                    c.setDateRecolte(java.sql.Date.valueOf(dr));
                    c.setRecolteEstime(re);
                    c.setEtat(et);

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

    // ================== UTILS ==================
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
        } else {
            System.err.println(msg);
        }
    }

    private void hideError() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }
}
