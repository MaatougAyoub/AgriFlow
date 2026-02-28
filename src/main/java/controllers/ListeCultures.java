package controllers;

import entities.Culture;
import entities.Parcelle;
import entities.User;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import services.CulturePDF;
import services.ServiceCulture;
import services.ServiceParcelle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import javafx.beans.value.ChangeListener;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import services.UserService;

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
    private final UserService userService = new UserService();
    private final CulturePDF pdfService = new CulturePDF();

    private final Map<Integer, String> parcelleNameById = new HashMap<>();
    private List<Parcelle> parcellesDuUser = new ArrayList<>();
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
                contentArea.getChildren().setAll(view);
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
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colParcelleId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getParcelleId()).asObject());
        colProprietaireId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getProprietaireId()).asObject());
        colNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNom()));
        colType.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTypeCulture() == null ? "-" : c.getValue().getTypeCulture().name()
        ));
        colSuperficie.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSuperficie()).asObject());
        colEtat.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEtat() == null ? "-" : c.getValue().getEtat().name()
        ));
        colDateRecolte.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDateRecolte()));
        colRecolteEstime.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getRecolteEstime()));
        colDateCreation.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDateCreation()));

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
    private void imprimerContratPdf(int cultureId) {
        hideError();
        try {
            Culture cdb = sc.recupererParId(cultureId);
            if (cdb == null) { showError("Culture introuvable."); return; }

            if (cdb.getEtat() != Culture.Etat.VENDUE || cdb.getIdAcheteur() == null) {
                showError("Contrat impossible: la culture n'est pas vendue.");
                return;
            }

            User vendeur = userService.recupererParId(cdb.getProprietaireId());
            User acheteur = userService.recupererParId(cdb.getIdAcheteur());
            Parcelle parcelle = sp.recupererParId(cdb.getParcelleId());

            if (vendeur == null || acheteur == null || parcelle == null) {
                showError("Données manquantes (vendeur/acheteur/parcelle).");
                return;
            }

            java.io.File pdf = pdfService.genererContratVente(cdb, vendeur, acheteur, parcelle);

            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(pdf);
            } else {
                showError("Impossible d'ouvrir automatiquement le PDF. Chemin: " + pdf.getAbsolutePath());
            }

        } catch (Exception ex) {
            showError("Erreur PDF: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    // ===== Parcelles du user + map id->nom + reste =====
    // (reste: on ignore RECOLTEE)
    private void preloadParcelles() {
        parcelleNameById.clear();
        parcellesDuUser = new ArrayList<>();
        parcelleResteById.clear();

        try {
            List<Parcelle> allParcelles = sp.recuperer();

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
    private String imagePathForType(Culture.TypeCulture type) {
        if (type == null) return "/images/cultures/autre.jpg";

        return switch (type) {
            case BLE -> "/images/cultures/ble.jpg";
            case ORGE -> "/images/cultures/orge.jpg";
            case MAIS -> "/images/cultures/mais.jpg";
            case POMME_DE_TERRE -> "/images/cultures/patates.jpg";
            case TOMATE -> "/images/cultures/tomates.jpg";
            case OLIVIER -> "/images/cultures/olivier.jpg";
            case AGRUMES -> "/images/cultures/agrumes.jpg";
            case VIGNE -> "/images/cultures/vigne.jpg";
            case FRAISE -> "/images/cultures/fraise.jpg";
            case LEGUMES -> "/images/cultures/legume.jpg";
            case AUTRE -> "/images/cultures/autre.jpg";
        };
    }
    private ImageView createCultureImageViewSquare(Culture.TypeCulture type, double size) {
        String path = imagePathForType(type);

        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) is = getClass().getResourceAsStream("/images/cultures/autre.jpg");

        Image img = (is != null) ? new Image(is) : null;

        ImageView iv = new ImageView(img);

        // carré
        iv.setFitWidth(size);
        iv.setFitHeight(size);

        // on remplit le carré
        iv.setPreserveRatio(false);
        iv.setSmooth(true);

        // Crop centré
        if (img != null && img.getWidth() > 0 && img.getHeight() > 0) {
            double w = img.getWidth();
            double h = img.getHeight();
            double side = Math.min(w, h);
            double x = (w - side) / 2.0;
            double y = (h - side) / 2.0;
            iv.setViewport(new Rectangle2D(x, y, side, side));
        }

        // Clip (coins arrondis)
        Rectangle clip = new Rectangle(size, size);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        iv.setClip(clip);

        return iv;
    }

    private ImageView createCultureImageView(Culture.TypeCulture type, double w, double h) {
        String path = imagePathForType(type);

        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            // fallback si path/nom incorrect
            is = getClass().getResourceAsStream("/images/cultures/autre.jpg");
        }

        Image img = (is != null) ? new Image(is) : null;

        ImageView iv = new ImageView(img);
        iv.setFitWidth(w);
        iv.setFitHeight(h);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        return iv;
    }

    // ===== Chargement cultures =====
    // IMPORTANT: pour AGRICULTEUR -> (propriétaire OU acheteur)
    private List<Culture> baseListByRole() throws SQLException {
        User u = MainController.getCurrentUser();
        if (u == null) return List.of();

        List<Culture> all = sc.recuperer();
        if (isAdmin(u)) return all;

        int uid = u.getId();
        return all.stream()
                .filter(c -> c.getProprietaireId() == uid || (c.getIdAcheteur() != null && c.getIdAcheteur() == uid))
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
    // ADMIN ou PROPRIETAIRE ou ACHETEUR
    private void supprimerCulture(Culture c) {
        User u = MainController.getCurrentUser();
        if (u == null) { showError("Session vide."); return; }

        boolean admin = isAdmin(u);
        boolean owner = c.getProprietaireId() == u.getId();
        boolean buyer = (c.getIdAcheteur() != null && c.getIdAcheteur() == u.getId());

        if (!admin && !owner && !buyer) {
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

        // ====== Card container ======
        VBox card = new VBox(12);
        card.setPadding(new Insets(14));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: rgba(0,0,0,0.08);" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);"
        );

        User me = MainController.getCurrentUser();
        int myId = me != null ? me.getId() : -1;

        boolean iAmOwner = (myId != -1 && c.getProprietaireId() == myId);
        boolean iAmBuyer = (myId != -1 && c.getIdAcheteur() != null && c.getIdAcheteur() == myId) && !iAmOwner;

        String parcelleNom = parcelleNameById.getOrDefault(
                c.getParcelleId(),
                "Parcelle #" + c.getParcelleId()
        );

        // ====== Image à gauche (carrée) ======
        ImageView iv = createCultureImageViewSquare(c.getTypeCulture(), 200); // taille carré
        VBox imageBox = new VBox(iv);
        imageBox.setPadding(new Insets(6));
        imageBox.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: rgba(0,0,0,0.10);"
        );

        // ====== Texte à droite ======
        VBox info = new VBox(6);

        Label title = new Label(c.getNom() == null ? "(Sans nom)" : c.getNom());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2D5A27;");

        Label l1 = new Label("Parcelle: " + parcelleNom);
        Label l2 = new Label("Type: " + (c.getTypeCulture() == null ? "-" : c.getTypeCulture().name()));
        Label l3 = new Label("Superficie: " + c.getSuperficie() + " m²");
        Label l4 = new Label("Etat: " + (c.getEtat() == null ? "-" : c.getEtat().name()));
        Label l5 = new Label("Date récolte: " + (c.getDateRecolte() == null ? "-" : c.getDateRecolte().toString()));
        Label l6 = new Label("Récolte estimée: " + (c.getRecolteEstime() == null ? "-" : c.getRecolteEstime()) + " Kg");

        Label l7 = null;
        Label l8 = null;
        if (c.getEtat() == Culture.Etat.EN_VENTE) {
            l7 = new Label("Prix: " + (c.getPrixVente() == null ? "-" : (c.getPrixVente() + " DT")));
            l8 = new Label("Publié le: " + (c.getDatePublication() == null ? "-" : c.getDatePublication().toString()));
        }

        Label l9 = null;
        if (iAmBuyer) {
            l9 = new Label("Achetée le: " + (c.getDateVente() == null ? "-" : c.getDateVente().toString()));
        }

        ArrayList<Label> labs = new ArrayList<>();
        labs.add(l1); labs.add(l2); labs.add(l3); labs.add(l4); labs.add(l5); labs.add(l6);
        if (l7 != null) labs.add(l7);
        if (l8 != null) labs.add(l8);
        if (l9 != null) labs.add(l9);

        for (Label l : labs) {
            l.setStyle("-fx-text-fill:#424242; -fx-font-size: 13px;");
        }

        // ====== Actions ======
        HBox actions = new HBox(10);

        Button btnEdit = new Button("Modifier");
        btnEdit.setStyle("-fx-background-color:#2E7D32; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8;");

        Button btnDelete = new Button("Supprimer");
        btnDelete.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8;");

        Button btnPrint = new Button("Imprimer contrat");
        btnPrint.setStyle("-fx-background-color:#737373; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8;");
        btnPrint.setOnAction(e -> imprimerContratPdf(c.getId()));

        // règles demandées:
        // - acheteur -> ACHETEE -> فقط imprimer
        // - propriétaire + VENDUE -> VENDUE -> modifier + supprimer + imprimer
        // - propriétaire autres états -> modifier + supprimer
        if (iAmBuyer && c.getEtat() == Culture.Etat.VENDUE) {
            actions.getChildren().add(btnPrint);
        } else if (iAmOwner && c.getEtat() == Culture.Etat.VENDUE) {
            btnEdit.setOnAction(e -> ouvrirPopupModificationAgriculteur(c));
            btnDelete.setOnAction(e -> supprimerCulture(c));
            actions.getChildren().addAll(btnEdit, btnDelete, btnPrint);
        } else if (iAmOwner) {
            btnEdit.setOnAction(e -> ouvrirPopupModificationAgriculteur(c));
            btnDelete.setOnAction(e -> supprimerCulture(c));
            actions.getChildren().addAll(btnEdit, btnDelete);
        } else {
            // si tu veux rien afficher pour un user qui n'est ni owner ni buyer:
            // actions.getChildren().clear();
            // ou laisser supprimer (comme ton code précédent):
            btnDelete.setOnAction(e -> supprimerCulture(c));
            actions.getChildren().add(btnDelete);
        }

        info.getChildren().add(title);
        info.getChildren().addAll(labs);
        info.getChildren().add(actions);

        // ====== Ligne principale : image gauche + texte droite ======
        HBox mainRow = new HBox(18, imageBox, info);
        mainRow.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        card.getChildren().add(mainRow);

        // ====== Badge (sticker) en haut à droite ======
        Label badge = null;
        if (iAmBuyer && c.getEtat() == Culture.Etat.VENDUE) {
            badge = makeBadge("ACHETÉE", "#0B7A33");
        } else if (iAmOwner && c.getEtat() == Culture.Etat.VENDUE) {
            badge = makeBadge("VENDUE", "#0B7A33");
        }

        StackPane wrapper = new StackPane(card);
        if (badge != null) {
            StackPane.setAlignment(badge, javafx.geometry.Pos.TOP_RIGHT);
            StackPane.setMargin(badge, new Insets(10, 10, 0, 0));
            wrapper.getChildren().add(badge);
        }

        return wrapper;
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
    private Label makeBadge(String text, String bgColor) {
        Label badge = new Label(text);
        badge.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 6 10;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;"
        );
        return badge;
    }

    private Parcelle findParcelleById(int id) {
        for (Parcelle p : parcellesDuUser) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    // ===== Helpers UI EN_VENTE =====
    private void setVisibleManaged(Control n, boolean v) {
        if (n == null) return;
        n.setVisible(v);
        n.setManaged(v);
    }
    private void setVisibleManaged(Label n, boolean v) {
        if (n == null) return;
        n.setVisible(v);
        n.setManaged(v);
    }

    private boolean isEnVente(Culture.Etat e) {
        return e == Culture.Etat.EN_VENTE;
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

        // ✅ IMPORTANT pour ton problème
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefWidth(650);
        dialog.getDialogPane().setPrefHeight(520);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

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

        // ✅ Etat + prix vente (affiché seulement si EN_VENTE)
        ComboBox<Culture.Etat> etat = new ComboBox<>(FXCollections.observableArrayList(Culture.Etat.values()));
        etat.setValue(Culture.Etat.EN_COURS);

        TextField prixVente = new TextField();
        prixVente.setPromptText("ex: 250");
        prixVente.setDisable(true);
        prixVente.setManaged(false);
        prixVente.setVisible(false);

        // ✅ petite preview image (optionnel mais pratique)
        ImageView preview = createCultureImageView(null, 120, 90);
        type.valueProperty().addListener((obs, oldV, newV) -> {
            ImageView iv = createCultureImageView(newV, 120, 90);
            preview.setImage(iv.getImage());
        });

        etat.valueProperty().addListener((obs, oldV, newV) -> {
            boolean enVente = (newV == Culture.Etat.EN_VENTE);
            prixVente.setDisable(!enVente);
            prixVente.setManaged(enVente);
            prixVente.setVisible(enVente);
            if (!enVente) prixVente.clear();
        });

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(12));
        grid.setMaxWidth(Double.MAX_VALUE);

        int r = 0;
        grid.add(new Label("Parcelle *"), 0, r);                 grid.add(parcelleCombo, 1, r++);

        grid.add(new Label("Nom *"), 0, r);                      grid.add(nom, 1, r++);

        HBox typeRow = new HBox(12, type, preview);
        grid.add(new Label("Type *"), 0, r);                     grid.add(typeRow, 1, r++);

        grid.add(new Label("Superficie (m²) *"), 0, r);          grid.add(superficie, 1, r++);
        grid.add(new Label("Date récolte *"), 0, r);             grid.add(dateRecolte, 1, r++);
        grid.add(new Label("Récolte estimée (Kg)"), 0, r);       grid.add(recolteEstime, 1, r++);

        grid.add(new Label("Etat *"), 0, r);                     grid.add(etat, 1, r++);
        grid.add(new Label("Prix vente (DT) * (si EN_VENTE)"), 0, r);  grid.add(prixVente, 1, r++);

        ScrollPane sp = new ScrollPane(grid);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        dialog.getDialogPane().setContent(sp);

        dialog.showAndWait().ifPresent(result -> {
            if (result == saveBtn) {
                try {
                    Parcelle selectedParcelle = parcelleCombo.getValue();
                    if (selectedParcelle == null) { showError("Parcelle obligatoire."); return; }

                    String n = nom.getText() == null ? "" : nom.getText().trim();
                    Culture.TypeCulture t = type.getValue();
                    if (n.isEmpty() || t == null) { showError("Nom et type sont obligatoires."); return; }

                    double s = Double.parseDouble(superficie.getText().trim());
                    if (s <= 0) { showError("Superficie doit être > 0."); return; }

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
                    if (s > reste + 1e-9) {
                        showError("Superficie trop grande. Reste disponible: " + reste + " m².");
                        return;
                    }

                    Double re = null;
                    String rtxt = recolteEstime.getText() == null ? "" : recolteEstime.getText().trim();
                    if (!rtxt.isEmpty()) re = Double.parseDouble(rtxt);

                    Culture.Etat e = etat.getValue();
                    if (e == null) { showError("Etat obligatoire."); return; }

                    // prix vente si EN_VENTE
                    Double pv = null;
                    java.sql.Date dp = null;
                    if (e == Culture.Etat.EN_VENTE) {
                        String ptxt = prixVente.getText() == null ? "" : prixVente.getText().trim();
                        if (ptxt.isEmpty()) { showError("Prix obligatoire si EN_VENTE."); return; }
                        pv = Double.parseDouble(ptxt);
                        if (pv <= 0) { showError("Prix doit être > 0."); return; }
                        dp = java.sql.Date.valueOf(LocalDate.now()); // publication = maintenant
                    }

                    Culture c = new Culture(
                            selectedParcelle.getId(),
                            u.getId(),
                            n,
                            t,
                            s,
                            e,
                            java.sql.Date.valueOf(ld),
                            re
                    );

                    // champs vente intégrés
                    c.setPrixVente(pv);
                    c.setDatePublication(dp);

                    sc.ajouter(c);
                    rafraichir();

                } catch (NumberFormatException ex) {
                    showError("Superficie / récolte estimée / prix doivent être numériques.");
                } catch (SQLException ex) {
                    showError("Erreur DB: " + ex.getMessage());
                }
            }
        });
    }

    // ===== AGRICULTEUR: edit popup =====
    private void ouvrirPopupModificationAgriculteur(Culture c) {
        User u = MainController.getCurrentUser();
        if (u == null) {
            showError("Session vide.");
            return;
        }
        if (isAdmin(u)) {
            showError("ADMIN ne peut pas modifier.");
            return;
        }
        if (c.getProprietaireId() != u.getId()) {
            showError("Accès refusé.");
            return;
        }

        if (parcellesDuUser.isEmpty()) {
            showError("Vous n'avez aucune parcelle.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Culture");
        dialog.setHeaderText("Modifier: " + (c.getNom() == null ? "" : c.getNom()));

        // ✅ IMPORTANT pour ton problème
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefWidth(650);
        dialog.getDialogPane().setPrefHeight(560);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        Parcelle current = findParcelleById(c.getParcelleId());
        ComboBox<Parcelle> parcelleCombo = createParcelleCombo(current != null ? current : parcellesDuUser.get(0));

        TextField nom = new TextField(c.getNom());

        ComboBox<Culture.TypeCulture> type = new ComboBox<>(FXCollections.observableArrayList(Culture.TypeCulture.values()));
        type.setValue(c.getTypeCulture());

        TextField superficie = new TextField(String.valueOf(c.getSuperficie()));
        ComboBox<Culture.Etat> etat = new ComboBox<>();
        Culture.Etat currentEtat = c.getEtat();

        if (currentEtat == Culture.Etat.VENDUE) {
            etat.setItems(FXCollections.observableArrayList(Culture.Etat.VENDUE, Culture.Etat.RECOLTEE));
            etat.setValue(Culture.Etat.VENDUE);
        } else if (currentEtat == Culture.Etat.RECOLTEE) {
            etat.setItems(FXCollections.observableArrayList(Culture.Etat.RECOLTEE));
            etat.setValue(Culture.Etat.RECOLTEE);
            etat.setDisable(true);
        } else {
            etat.setItems(FXCollections.observableArrayList(Culture.Etat.values()));
            etat.setValue(currentEtat);
        }
        DatePicker dateRecolte = new DatePicker(c.getDateRecolte() == null ? null : c.getDateRecolte().toLocalDate());

        TextField recolteEstime = new TextField(c.getRecolteEstime() == null ? "" : String.valueOf(c.getRecolteEstime()));

        // ✅ prix vente visible seulement si EN_VENTE
        TextField prixVente = new TextField(c.getPrixVente() == null ? "" : String.valueOf(c.getPrixVente()));
        boolean initEnVente = (c.getEtat() == Culture.Etat.EN_VENTE);
        prixVente.setDisable(!initEnVente);
        prixVente.setManaged(initEnVente);
        prixVente.setVisible(initEnVente);

        // ✅ preview image
        ImageView preview = createCultureImageView(c.getTypeCulture(), 120, 90);
        type.valueProperty().addListener((obs, oldV, newV) -> {
            ImageView iv = createCultureImageView(newV, 120, 90);
            preview.setImage(iv.getImage());
        });

        etat.valueProperty().addListener((obs, oldV, newV) -> {
            boolean enVente = (newV == Culture.Etat.EN_VENTE);
            prixVente.setDisable(!enVente);
            prixVente.setManaged(enVente);
            prixVente.setVisible(enVente);
            if (!enVente) prixVente.clear();
        });

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(12));
        grid.setMaxWidth(Double.MAX_VALUE);

        int r = 0;
        grid.add(new Label("Parcelle *"), 0, r);
        grid.add(parcelleCombo, 1, r++);

        grid.add(new Label("Nom *"), 0, r);
        grid.add(nom, 1, r++);

        HBox typeRow = new HBox(12, type, preview);
        grid.add(new Label("Type *"), 0, r);
        grid.add(typeRow, 1, r++);

        grid.add(new Label("Superficie (m²) *"), 0, r);
        grid.add(superficie, 1, r++);
        grid.add(new Label("Etat *"), 0, r);
        grid.add(etat, 1, r++);
        grid.add(new Label("Date récolte *"), 0, r);
        grid.add(dateRecolte, 1, r++);
        grid.add(new Label("Récolte estimée (Kg)"), 0, r);
        grid.add(recolteEstime, 1, r++);

        grid.add(new Label("Prix vente (DT) * (si EN_VENTE)"), 0, r);
        grid.add(prixVente, 1, r++);

        ScrollPane spScroll = new ScrollPane(grid);
        spScroll.setFitToWidth(true);
        spScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        spScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        dialog.getDialogPane().setContent(spScroll);

        dialog.showAndWait().ifPresent(result -> {
            if (result == saveBtn) {
                try {
                    Parcelle selectedParcelle = parcelleCombo.getValue();
                    if (selectedParcelle == null) {
                        showError("Parcelle obligatoire.");
                        return;
                    }

                    String n = nom.getText() == null ? "" : nom.getText().trim();
                    Culture.TypeCulture t = type.getValue();
                    if (n.isEmpty() || t == null) {
                        showError("Nom et type obligatoires.");
                        return;
                    }

                    double s = Double.parseDouble(superficie.getText().trim());
                    if (s <= 0) {
                        showError("Superficie doit être > 0.");
                        return;
                    }

                    Culture.Etat e = etat.getValue();
                    if (e == null) {
                        showError("Etat obligatoire.");
                        return;
                    }

                    LocalDate ld = dateRecolte.getValue();
                    if (ld == null) {
                        showError("Date récolte obligatoire.");
                        return;
                    }

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

                    if (s > maxAllowed + 1e-9) {
                        showError("Superficie trop grande. Max autorisé: " + round2(maxAllowed) + " m².");
                        return;
                    }

                    Double re = null;
                    String rtxt = recolteEstime.getText() == null ? "" : recolteEstime.getText().trim();
                    if (!rtxt.isEmpty()) re = Double.parseDouble(rtxt);

                    // vente
                    Double pv = null;
                    java.sql.Date dp = null;
                    if (e == Culture.Etat.EN_VENTE) {
                        String ptxt = prixVente.getText() == null ? "" : prixVente.getText().trim();
                        if (ptxt.isEmpty()) {
                            showError("Prix obligatoire si EN_VENTE.");
                            return;
                        }
                        pv = Double.parseDouble(ptxt);
                        if (pv <= 0) {
                            showError("Prix doit être > 0.");
                            return;
                        }

                        // si elle n’était pas déjà publiée, on met publication = maintenant
                        if (c.getDatePublication() != null) dp = c.getDatePublication();
                        else dp = java.sql.Date.valueOf(LocalDate.now());
                    } else {
                        // si tu veux reset quand on sort de la vente
                        pv = null;
                        dp = null;
                        c.setIdAcheteur(null);
                        c.setDateVente(null);
                    }

                    c.setParcelleId(selectedParcelle.getId());
                    c.setNom(n);
                    c.setTypeCulture(t);
                    c.setSuperficie(s);
                    c.setEtat(e);
                    c.setDateRecolte(java.sql.Date.valueOf(ld));
                    c.setRecolteEstime(re);

                    c.setPrixVente(pv);
                    c.setDatePublication(dp);

                    sc.modifier(c);
                    rafraichir();

                } catch (NumberFormatException ex) {
                    showError("Superficie / récolte estimée / prix doivent être numériques.");
                } catch (SQLException ex) {
                    showError("Erreur modification: " + ex.getMessage());
                }
            }
        });
    }
}