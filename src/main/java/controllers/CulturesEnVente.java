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
import services.UserService;
import services.CulturePDF;
import services.ServiceParcelle;
import java.io.File;
import java.awt.Desktop;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Rectangle2D;
import java.util.stream.Collectors;

public class CulturesEnVente {

    // Root (pour navigation)
    @FXML private BorderPane root;

    @FXML private Label errorLabel;
    @FXML private Label countLabel;

    @FXML private TextField searchField;

    // ADMIN
    @FXML private VBox adminPane;
    @FXML private TableView<Culture> table;


    @FXML private TableColumn<Culture, Integer> colId;
    @FXML private TableColumn<Culture, Integer> colProprietaireId;
    @FXML private TableColumn<Culture, String> colNom;
    @FXML private TableColumn<Culture, String> colType;
    @FXML private TableColumn<Culture, Double> colSuperficie;
    @FXML private TableColumn<Culture, Double> colRecolteEstime;
    @FXML private TableColumn<Culture, Object> colDateRecolte;
    @FXML private TableColumn<Culture, Void> colActions;

    // AGRICULTEUR
    @FXML private VBox agriculteurPane;
    @FXML private ScrollPane cardsScroll;
    @FXML private VBox cardsContainer;

    private final ServiceCulture sc = new ServiceCulture();
    private final UserService userService = new UserService();
    private final CulturePDF pdfService = new CulturePDF();
    private final ServiceParcelle sp = new ServiceParcelle();

    @FXML
    public void initialize() {

        // simulation session si vide (comme chez toi)
        if (MainController.getCurrentUser() == null) {
            User uu = new User(36, "Taaat", "ddd", "emaaail@test.com");
            uu.setRole("AGRICULTEUR");
            MainController.setCurrentUser(uu);
        }

        // refresh auto search
        if (searchField != null) {
            searchField.textProperty().addListener((obs, o, n) -> rafraichir());
        }

        User u = MainController.getCurrentUser();
        boolean admin = isAdmin(u);

        setVisibleManaged(adminPane, admin);
        setVisibleManaged(agriculteurPane, !admin);

        if (admin) setupAdminTable();

        rafraichir();
    }

    // ================== NAV ==================
    @FXML
    private void retourParcellesCultures() {
        naviguerVers("/ParcellesCultures.fxml");
    }

    private void naviguerVers(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            StackPane contentArea = null;

            if (root != null && root.getScene() != null) {
                contentArea = (StackPane) root.getScene().lookup("#contentArea");
                if (contentArea == null && root.getScene().getRoot() != null) {
                    contentArea = (StackPane) root.getScene().getRoot().lookup("#contentArea");
                }
            }

            if (contentArea != null) {
                contentArea.getChildren().setAll(view);
            } else {
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
            String q = (searchField == null || searchField.getText() == null)
                    ? ""
                    : searchField.getText().trim().toLowerCase();

            // IMPORTANT: ici on affiche uniquement EN_VENTE
            List<Culture> list = sc.recuperer().stream()
                    .filter(c -> c.getEtat() == Culture.Etat.EN_VENTE)
                    .filter(c -> q.isEmpty() || (c.getNom() != null && c.getNom().toLowerCase().contains(q)))
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
        colProprietaireId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getProprietaireId()).asObject());
        colNom.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNom()));
        colType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getTypeCulture() == null ? "-" : c.getValue().getTypeCulture().name()
        ));
        colSuperficie.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getSuperficie()).asObject());
        colRecolteEstime.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getRecolteEstime()));
        colDateRecolte.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getDateRecolte()));

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

    private ImageView createCultureImageView(Culture.TypeCulture type, double size) {
        String path = imagePathForType(type);

        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) is = getClass().getResourceAsStream("/images/cultures/autre.jpg");

        Image img = (is != null) ? new Image(is) : null;

        ImageView iv = new ImageView(img);

        // carré
        iv.setFitWidth(size);
        iv.setFitHeight(size);

        // IMPORTANT: pour remplir le carré
        iv.setPreserveRatio(false); // on va "remplir" + crop via viewport si besoin
        iv.setSmooth(true);

        // Crop centré (évite déformation si l'image est rectangulaire)
        if (img != null && img.getWidth() > 0 && img.getHeight() > 0) {
            double w = img.getWidth();
            double h = img.getHeight();
            double side = Math.min(w, h);
            double x = (w - side) / 2.0;
            double y = (h - side) / 2.0;
            iv.setViewport(new Rectangle2D(x, y, side, side));
        }

        // Clip pour que l'image prenne exactement la forme du cadre
        Rectangle clip = new Rectangle(size, size);
        clip.setArcWidth(0);   // mets 16 si tu veux coins arrondis
        clip.setArcHeight(0);  // mets 16 si tu veux coins arrondis
        iv.setClip(clip);

        return iv;
    }
    private Node buildCard(Culture c, boolean mine) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(14));
        card.setStyle(
                "-fx-background-color: " + (mine ? "#E8F5E9" : "white") + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: rgba(0,0,0,0.10);" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);"
        );

        // ====== IMAGE (comme ListeCultures) ======
        ImageView iv = createCultureImageView(c.getTypeCulture(), 200); // carré 140x140 par ex
        VBox imageBox = new VBox(iv);
        imageBox.setPadding(new Insets(6));
        imageBox.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: rgba(0,0,0,0.10);"
        );

        // ====== TEXT ======
        VBox info = new VBox(6);

        Label title = new Label(c.getNom() == null ? "(Sans nom)" : c.getNom());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2D5A27;");

        Label l1 = new Label("Type: " + (c.getTypeCulture() == null ? "-" : c.getTypeCulture().name()));
        Label l2 = new Label("Superficie: " + c.getSuperficie() + " m²");
        Label l3 = new Label("Date récolte: " + (c.getDateRecolte() == null ? "-" : c.getDateRecolte().toString()));
        Label l4 = new Label("Récolte estimée: " + (c.getRecolteEstime() == null ? "-" : c.getRecolteEstime()) + " Kg");
        Label l5 = new Label("Prix: " + (c.getPrixVente() == null ? "-" : (c.getPrixVente() + " DT")));
        Label l6 = new Label("Publié le: " + (c.getDatePublication() == null ? "-" : c.getDatePublication().toString()));

        for (Label l : List.of(l1, l2, l3, l4, l5, l6)) {
            l.setStyle("-fx-text-fill:#424242; -fx-font-size: 13px;");
        }

        // ====== ACTIONS ======
        if (mine) {
            Button modifier = new Button("Modifier");
            modifier.setStyle("-fx-background-color:#2E7D32; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8;");
            modifier.setOnAction(e -> ouvrirPopupModification(c));

            Button supprimer = new Button("Supprimer");
            supprimer.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8;");
            supprimer.setOnAction(e -> supprimer(c));

            info.getChildren().addAll(title, l1, l2, l3, l4, l5, l6, new HBox(10, modifier, supprimer));
        } else {
            Button contacter = new Button("Contacter");
            contacter.setStyle("-fx-background-color:#2E7D32; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8;");
            contacter.setOnAction(e -> contacterProprietaire(c));

            Button acheter = new Button("Acheter");
            acheter.setStyle("-fx-background-color:#1565C0; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8;");
            acheter.setOnAction(e -> acheterCulture(c));

            info.getChildren().addAll(title, l1, l2, l3, l4, l5, l6, new HBox(10, contacter, acheter));
        }

        HBox mainRow = new HBox(18, imageBox, info);
        mainRow.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        card.getChildren().add(mainRow);
        return card;
    }

    // ================== ACHAT ==================
    private void acheterCulture(Culture c) {
        hideError();

        User me = MainController.getCurrentUser();
        if (me == null) { showError("Session vide."); return; }

        // sécurité: empêcher d’acheter sa propre culture
        if (c.getProprietaireId() == me.getId()) {
            showError("Vous ne pouvez pas acheter votre propre culture.");
            return;
        }

        // sécurité: si plus en vente (cas refresh lent)
        if (c.getEtat() != Culture.Etat.EN_VENTE) {
            showError("Cette culture n'est plus en vente.");
            rafraichir();
            return;
        }

        // confirmation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation achat");
        confirm.setHeaderText("Confirmer l'achat ?");
        confirm.setContentText(
                "Culture: " + (c.getNom()) + "\n" +
                        "Prix: " + (c.getPrixVente() == null ? "-" : (c.getPrixVente() + " DT"))
        );

        confirm.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;

            try {
                Date today = Date.valueOf(LocalDate.now());

                // 1) Mise à jour DB: etat=VENDUE, id_acheteur, date_vente
                sc.marquerVendue(c.getId(), me.getId(), today);

                // 2) Mise à jour objet (pour UI)
                c.setEtat(Culture.Etat.VENDUE);
                c.setIdAcheteur(me.getId());
                c.setDateVente(today);

                // 3) Demander impression contrat
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Achat effectué");
                dialog.setHeaderText("Achat effectué avec succès.");
                ButtonType printBtn = new ButtonType("Imprimer contrat PDF", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(printBtn, ButtonType.CLOSE);
                dialog.setContentText("Voulez-vous imprimer le contrat maintenant ?");
                dialog.showAndWait().ifPresent(res -> {
                    if (res == printBtn) {
                        imprimerContratPdf(c.getId()); // recharge depuis DB + génère PDF
                    }
                });

                // 4) refresh: comme l’écran affiche uniquement EN_VENTE, l’item va disparaître
                rafraichir();

            } catch (SQLException ex) {
                showError("Erreur achat: " + ex.getMessage());
            }
        });
    }
    private void imprimerContratPdf(int cultureId) {
        hideError();
        try {
            // 1) Reload culture depuis DB (important)
            Culture cdb = sc.recupererParId(cultureId);
            if (cdb == null) { showError("Culture introuvable."); return; }

            if (cdb.getEtat() != Culture.Etat.VENDUE || cdb.getIdAcheteur() == null) {
                showError("Contrat impossible: la culture n'est pas vendue.");
                return;
            }

            // 2) Charger vendeur / acheteur / parcelle
            User vendeur = userService.recupererParId(cdb.getProprietaireId());
            User acheteur = userService.recupererParId(cdb.getIdAcheteur());
            Parcelle parcelle = sp.recupererParId(cdb.getParcelleId());

            if (vendeur == null || acheteur == null || parcelle == null) {
                showError("Données manquantes (vendeur/acheteur/parcelle).");
                return;
            }

            // 3) Générer PDF
            File pdf = pdfService.genererContratVente(cdb, vendeur, acheteur, parcelle);

            // 4) Ouvrir PDF
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdf);
            } else {
                showError("Impossible d'ouvrir automatiquement le PDF. Chemin: " + pdf.getAbsolutePath());
            }

        } catch (Exception ex) {
            showError("Erreur PDF: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showSuccessAchat() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Achat");
        dialog.setHeaderText("Achat effectué avec succès");

        ButtonType printBtn = new ButtonType("Imprimer facture", ButtonBar.ButtonData.LEFT);
        ButtonType closeBtn = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(printBtn, closeBtn);

        Label msg = new Label("Votre achat a été enregistré.\nVous pourrez imprimer la facture (PDF) plus tard.");
        msg.setWrapText(true);
        msg.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(msg);

        dialog.showAndWait().ifPresent(result -> {
            if (result == printBtn) {
                // TODO: plus tard -> génération PDF
                // pour l’instant: ne rien faire
            }
        });
    }

    // ================== CONTACT EMAIL ==================
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
                "Superficie: " + c.getSuperficie() + " m²\n" +
                "Prix: " + (c.getPrixVente() == null ? "-" : (c.getPrixVente() + " DT")) + "\n\n" +
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

    // ================== SUPPRIMER (ADMIN OU PROPRIETAIRE) ==================
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

    // (popup modification simple)
    private void ouvrirPopupModification(Culture c) {
        // tu peux garder ta version existante si tu veux
        // ici je laisse un message, pour éviter compilation cassée si tu l’avais déjà ailleurs
        showError("Popup modification: garde ton implémentation existante ici.");
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