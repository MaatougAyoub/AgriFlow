/*
package controllers;

import entities.Culture;
import entities.Parcelle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import services.ServiceCulture;
import services.ServiceParcelle;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ListeCultures {

    @FXML private TabPane tabPane;

    // Filters
    @FXML private ComboBox<String> etatFilterCombo;
    @FXML private ComboBox<String> typeFilterCombo;
    @FXML private TextField searchField;

    // Cards
    @FXML private FlowPane cardsPane;
    @FXML private Label countLabel;

    // Add form (ETAT PAS DANS LE FORM)
    @FXML private TextField parcelleIdField;
    @FXML private TextField nomField;
    @FXML private ComboBox<Culture.TypeCulture> typeCultureCombo;
    @FXML private TextField superficieField;
    @FXML private DatePicker dateRecoltePicker;
    @FXML private TextField recolteEstimeField;

    @FXML private Label addErrorLabel;
    @FXML private Label addSuccessLabel;

    private final ServiceCulture sc = new ServiceCulture();
    private final ServiceParcelle sp = new ServiceParcelle();

    // TODO: remplacer par id utilisateur connecte (session)
    private final int currentUserId = 33;

    private final Map<Integer, String> parcelleNameById = new HashMap<>();

    @FXML
    public void initialize() {
        // add combos
        typeCultureCombo.setItems(FXCollections.observableArrayList(Culture.TypeCulture.values()));

        // filter combos
        etatFilterCombo.setItems(FXCollections.observableArrayList(
                "TOUT", "EN_COURS", "RECOLTEE", "EN_VENTE", "VENDUE"
        ));
        etatFilterCombo.setValue("TOUT");

        typeFilterCombo.setItems(FXCollections.observableArrayList(
                "TOUT",
                "BLE","ORGE","MAIS","POMME_DE_TERRE","TOMATE","OLIVIER","AGRUMES","VIGNE","FRAISE","LEGUMES","AUTRE"
        ));
        typeFilterCombo.setValue("TOUT");

        rafraichir();
    }

    @FXML
    void rafraichir() {
        try {
            chargerParcellesMap();

            List<Culture> list = sc.recuperer().stream()
                    .filter(c -> c.getProprietaireId() == currentUserId)
                    .collect(Collectors.toList());

            renderCards(list);
        } catch (SQLException e) {
            showAddError("Erreur DB: " + e.getMessage());
        }
    }

    @FXML
    void rechercher() {
        try {
            chargerParcellesMap();

            String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
            String etat = etatFilterCombo.getValue();
            String type = typeFilterCombo.getValue();

            List<Culture> list = sc.recuperer().stream()
                    .filter(c -> c.getProprietaireId() == currentUserId)
                    .filter(c -> q.isEmpty() || c.getNom().toLowerCase().contains(q))
                    .filter(c -> "TOUT".equals(etat) || (c.getEtat() != null && c.getEtat().name().equals(etat)))
                    .filter(c -> "TOUT".equals(type) || (c.getTypeCulture() != null && c.getTypeCulture().name().equals(type)))
                    .collect(Collectors.toList());

            renderCards(list);
        } catch (SQLException e) {
            showAddError("Erreur DB: " + e.getMessage());
        }
    }

    private void chargerParcellesMap() throws SQLException {
        parcelleNameById.clear();
        for (Parcelle p : sp.recuperer()) {
            parcelleNameById.put(p.getId(), p.getNom());
        }
    }

    private void renderCards(List<Culture> cultures) {
        cardsPane.getChildren().clear();
        countLabel.setText(cultures.size() + " culture(s)");

        for (Culture c : cultures) {
            cardsPane.getChildren().add(createCard(c));
        }
    }

    private Region createCard(Culture c) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setPrefWidth(340);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: #E0E0E0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 8, 0, 0, 2);"
        );

        // badge etat
        Label badge = new Label(c.getEtat() != null ? c.getEtat().name() : "-");
        String badgeColor = switch (c.getEtat()) {
            case EN_VENTE -> "#2E7D32";
            case EN_COURS -> "#1976d2";
            case RECOLTEE -> "#f9a825";
            case VENDUE -> "#616161";
            default -> "#607D8B";
        };
        badge.setStyle("-fx-background-color:" + badgeColor + "; -fx-text-fill:white; -fx-padding:4 10; -fx-background-radius: 12;");

        Label title = new Label(c.getNom());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #212121;");

        String parcelleNom = parcelleNameById.getOrDefault(c.getParcelleId(), "Parcelle #" + c.getParcelleId());
        Label parcelle = new Label("Parcelle: " + parcelleNom);
        parcelle.setStyle("-fx-text-fill:#616161;");

        Label type = new Label("Type: " + (c.getTypeCulture() != null ? c.getTypeCulture().name() : "-"));
        type.setStyle("-fx-text-fill:#2E7D32;");

        Label sup = new Label("Superficie: " + c.getSuperficie());
        sup.setStyle("-fx-text-fill:#616161;");

        Label dateRec = new Label("Date recolte: " + (c.getDateRecolte() != null ? c.getDateRecolte().toString() : "-"));
        dateRec.setStyle("-fx-text-fill:#616161;");

        Label recEst = new Label("Recolte estimee: " + (c.getRecolteEstime() != null ? c.getRecolteEstime() : "-"));
        recEst.setStyle("-fx-text-fill:#616161;");

        Button btnEdit = new Button("Modifier");
        Button btnDelete = new Button("Supprimer");
        btnEdit.setStyle("-fx-background-color:#1976d2; -fx-text-fill:white; -fx-padding:8 10;");
        btnDelete.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white; -fx-padding:8 10;");

        btnEdit.setOnAction(e -> ouvrirPopupModification(c));
        btnDelete.setOnAction(e -> supprimerCulture(c));

        HBox actions = new HBox(10, btnEdit, btnDelete);
        actions.setPadding(new Insets(6, 0, 0, 0));

        card.getChildren().addAll(badge, title, parcelle, type, sup, dateRec, recEst, actions);
        return card;
    }

    @FXML
    void validerAjout() {
        hideMessages();

        try {
            int parcelleId = Integer.parseInt(parcelleIdField.getText().trim());
            String nom = nomField.getText().trim();
            Culture.TypeCulture type = typeCultureCombo.getValue();
            double superficie = Double.parseDouble(superficieField.getText().trim());

            // ETAT PAR DEFAUT A LA CREATION
            Culture.Etat etat = Culture.Etat.EN_COURS;

            LocalDate ld = dateRecoltePicker.getValue();
            if (ld == null) {
                showAddError("Date recolte obligatoire.");
                return;
            }
            Date dateRecolte = Date.valueOf(ld);

            Double recolteEstime = null;
            String r = recolteEstimeField.getText() == null ? "" : recolteEstimeField.getText().trim();
            if (!r.isEmpty()) recolteEstime = Double.parseDouble(r);

            if (nom.isEmpty() || type == null) {
                showAddError("Veuillez remplir tous les champs obligatoires.");
                return;
            }

            Culture c = new Culture(parcelleId, currentUserId, nom, type, superficie, etat, dateRecolte, recolteEstime);
            sc.ajouter(c);

            showAddSuccess("Culture ajoutee avec succes.");
            clearAddForm();
            rafraichir();
            tabPane.getSelectionModel().select(0);

        } catch (NumberFormatException ex) {
            showAddError("Parcelle ID, superficie, recolte estimee doivent etre numeriques.");
        } catch (SQLException ex) {
            showAddError("Erreur DB: " + ex.getMessage());
        }
    }

    @FXML
    void annulerAjout() {
        clearAddForm();
        hideMessages();
    }

    @FXML
    void retourProfil() {
        // navigation a adapter
    }

    private void supprimerCulture(Culture c) {
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
                    showAddError("Erreur suppression: " + ex.getMessage());
                }
            }
        });
    }

    private void ouvrirPopupModification(Culture c) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Culture");
        dialog.setHeaderText("Modifier la culture (ID: " + c.getId() + ")");

        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField parcelleEdit = new TextField(String.valueOf(c.getParcelleId()));
        TextField nomEdit = new TextField(c.getNom());

        ComboBox<Culture.TypeCulture> typeEdit = new ComboBox<>(FXCollections.observableArrayList(Culture.TypeCulture.values()));
        typeEdit.setValue(c.getTypeCulture());

        TextField supEdit = new TextField(String.valueOf(c.getSuperficie()));

        // ICI on peut changer l'etat
        ComboBox<Culture.Etat> etatEdit = new ComboBox<>(FXCollections.observableArrayList(Culture.Etat.values()));
        etatEdit.setValue(c.getEtat());

        DatePicker dateEdit = new DatePicker(c.getDateRecolte() == null ? null : c.getDateRecolte().toLocalDate());
        TextField recolteEdit = new TextField(c.getRecolteEstime() == null ? "" : String.valueOf(c.getRecolteEstime()));

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Parcelle ID *"), 0, 0); grid.add(parcelleEdit, 1, 0);
        grid.add(new Label("Nom *"), 0, 1); grid.add(nomEdit, 1, 1);
        grid.add(new Label("Type *"), 0, 2); grid.add(typeEdit, 1, 2);
        grid.add(new Label("Superficie *"), 0, 3); grid.add(supEdit, 1, 3);
        grid.add(new Label("Etat *"), 0, 4); grid.add(etatEdit, 1, 4);
        grid.add(new Label("Date recolte *"), 0, 5); grid.add(dateEdit, 1, 5);
        grid.add(new Label("Recolte estimee"), 0, 6); grid.add(recolteEdit, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result == saveBtn) {
                try {
                    c.setParcelleId(Integer.parseInt(parcelleEdit.getText().trim()));
                    c.setNom(nomEdit.getText().trim());
                    c.setTypeCulture(typeEdit.getValue());
                    c.setSuperficie(Double.parseDouble(supEdit.getText().trim()));
                    c.setEtat(etatEdit.getValue());

                    LocalDate ld = dateEdit.getValue();
                    if (ld == null) {
                        showAddError("Date recolte obligatoire.");
                        return;
                    }
                    c.setDateRecolte(Date.valueOf(ld));

                    String rr = recolteEdit.getText() == null ? "" : recolteEdit.getText().trim();
                    c.setRecolteEstime(rr.isEmpty() ? null : Double.parseDouble(rr));

                    sc.modifier(c);
                    rafraichir();

                } catch (NumberFormatException ex) {
                    showAddError("Parcelle ID, superficie, recolte estimee doivent etre numeriques.");
                } catch (SQLException ex) {
                    showAddError("Erreur modification: " + ex.getMessage());
                }
            }
        });
    }

    private void clearAddForm() {
        parcelleIdField.clear();
        nomField.clear();
        typeCultureCombo.setValue(null);
        superficieField.clear();
        dateRecoltePicker.setValue(null);
        recolteEstimeField.clear();
    }

    private void hideMessages() {
        addErrorLabel.setVisible(false);
        addSuccessLabel.setVisible(false);
    }

    private void showAddError(String msg) {
        addErrorLabel.setText(msg);
        addErrorLabel.setVisible(true);
        addSuccessLabel.setVisible(false);
    }

    private void showAddSuccess(String msg) {
        addSuccessLabel.setText(msg);
        addSuccessLabel.setVisible(true);
        addErrorLabel.setVisible(false);
    }
}
*/
