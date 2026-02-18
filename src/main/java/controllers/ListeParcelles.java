


package controllers;

import entities.Parcelle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.ServiceParcelle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ListeParcelles {

    @FXML private TabPane tabPane;

    // Liste
    @FXML private ComboBox<String> typeFilterCombo;
    @FXML private TextField searchField;
    @FXML private TableView<Parcelle> table;

    @FXML private TableColumn<Parcelle, Integer> colId;
    @FXML private TableColumn<Parcelle, Integer> colAgriculteurId;
    @FXML private TableColumn<Parcelle, String> colNom;
    @FXML private TableColumn<Parcelle, Double> colSuperficie;
    @FXML private TableColumn<Parcelle, String> colTypeTerre;
    @FXML private TableColumn<Parcelle, String> colLocalisation;
    @FXML private TableColumn<Parcelle, Object> colDateCreation;
    @FXML private TableColumn<Parcelle, Void> colActions;

    @FXML private Label countLabel;

    // Ajout
    @FXML private TextField agriculteurIdField;
    @FXML private TextField nomField;
    @FXML private TextField superficieField;
    @FXML private ComboBox<Parcelle.TypeTerre> typeTerreCombo;
    @FXML private TextField localisationField;

    @FXML private Label addErrorLabel;
    @FXML private Label addSuccessLabel;

    private final ServiceParcelle sp = new ServiceParcelle();

    @FXML
    public void initialize() {
        // combos
        typeTerreCombo.setItems(FXCollections.observableArrayList(Parcelle.TypeTerre.values()));
        typeFilterCombo.setItems(FXCollections.observableArrayList(
                "TOUT", "ARGILEUSE","SABLEUSE","LIMONEUSE","CALCAIRE","HUMIFERE","SALINE","MIXTE","AUTRE"
        ));
        typeFilterCombo.setValue("TOUT");

        // columns
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colAgriculteurId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getAgriculteurId()).asObject());
        colNom.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNom()));
        colSuperficie.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getSuperficie()).asObject());
        colTypeTerre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTypeTerre().name()));
        colLocalisation.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLocalisation()));
        colDateCreation.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getDateCreation()));

        rafraichir();
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDelete = new Button("Supprimer");
            private final HBox box = new HBox(8, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-background-color:#1976d2; -fx-text-fill:white; -fx-padding:6 10;");
                btnDelete.setStyle("-fx-background-color:#d32f2f; -fx-text-fill:white; -fx-padding:6 10;");

                btnDelete.setOnAction(e -> {
                    Parcelle p = getTableView().getItems().get(getIndex());
                    supprimerParcelle(p);
                });

                btnEdit.setOnAction(e -> {
                    Parcelle p = getTableView().getItems().get(getIndex());
                    ouvrirPopupModification(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }
    private void supprimerParcelle(Parcelle p) {
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
                    showAddError("Erreur suppression: " + ex.getMessage());
                }
            }
        });
    }
    private void ouvrirPopupModification(Parcelle p) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Parcelle");
        dialog.setHeaderText("Modifier la parcelle (ID: " + p.getId() + ")");

        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        // Champs
        TextField agriculteurIdFieldEdit = new TextField(String.valueOf(p.getAgriculteurId()));
        TextField nomFieldEdit = new TextField(p.getNom());
        TextField superficieFieldEdit = new TextField(String.valueOf(p.getSuperficie()));
        ComboBox<Parcelle.TypeTerre> typeComboEdit = new ComboBox<>(
                FXCollections.observableArrayList(Parcelle.TypeTerre.values())
        );
        typeComboEdit.setValue(p.getTypeTerre());
        TextField localisationFieldEdit = new TextField(p.getLocalisation());

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Agriculteur ID *"), 0, 0);
        grid.add(agriculteurIdFieldEdit, 1, 0);

        grid.add(new Label("Nom *"), 0, 1);
        grid.add(nomFieldEdit, 1, 1);

        grid.add(new Label("Superficie *"), 0, 2);
        grid.add(superficieFieldEdit, 1, 2);

        grid.add(new Label("Type Terre *"), 0, 3);
        grid.add(typeComboEdit, 1, 3);

        grid.add(new Label("Localisation *"), 0, 4);
        grid.add(localisationFieldEdit, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Validation simple avant fermer
        dialog.setResultConverter(button -> {
            if (button == saveBtn) {
                String nom = nomFieldEdit.getText().trim();
                String loc = localisationFieldEdit.getText().trim();
                if (nom.isEmpty() || loc.isEmpty() || typeComboEdit.getValue() == null) {
                    showAddError("Veuillez remplir tous les champs obligatoires.");
                    return null;
                }
                return button;
            }
            return button;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == saveBtn) {
                try {
                    // Construire l'objet modifié (garder l'ID existant)
                    p.setAgriculteurId(Integer.parseInt(agriculteurIdFieldEdit.getText().trim()));
                    p.setNom(nomFieldEdit.getText().trim());
                    p.setSuperficie(Double.parseDouble(superficieFieldEdit.getText().trim()));
                    p.setTypeTerre(typeComboEdit.getValue());
                    p.setLocalisation(localisationFieldEdit.getText().trim());

                    sp.modifier(p);
                    rafraichir();

                } catch (NumberFormatException ex) {
                    showAddError("Agriculteur ID et superficie doivent être numériques.");
                } catch (SQLException ex) {
                    showAddError("Erreur modification: " + ex.getMessage());
                }
            }
        });
    }


    @FXML
    void rafraichir() {
        try {
            List<Parcelle> list = sp.recuperer();
            table.setItems(FXCollections.observableArrayList(list));
            countLabel.setText(list.size() + " parcelle(s)");
        } catch (SQLException e) {
            showAddError("Erreur DB: " + e.getMessage());
        }
    }

    @FXML
    void rechercher() {
        try {
            List<Parcelle> list = sp.recuperer();

            String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
            String type = typeFilterCombo.getValue();

            List<Parcelle> filtered = list.stream()
                    .filter(p -> q.isEmpty()
                            || p.getNom().toLowerCase().contains(q)
                            || p.getLocalisation().toLowerCase().contains(q))
                    .filter(p -> "TOUT".equals(type) || p.getTypeTerre().name().equals(type))
                    .collect(Collectors.toList());

            table.setItems(FXCollections.observableArrayList(filtered));
            countLabel.setText(filtered.size() + " parcelle(s)");
        } catch (SQLException e) {
            showAddError("Erreur DB: " + e.getMessage());
        }
    }

    @FXML
    void validerAjout() {
        hideMessages();

        try {
            int agriculteurId = Integer.parseInt(agriculteurIdField.getText().trim());
            String nom = nomField.getText().trim();
            double superficie = Double.parseDouble(superficieField.getText().trim());
            Parcelle.TypeTerre type = typeTerreCombo.getValue();
            String loc = localisationField.getText().trim();

            if (nom.isEmpty() || loc.isEmpty() || type == null) {
                showAddError("Veuillez remplir tous les champs obligatoires.");
                return;
            }

            Parcelle p = new Parcelle(agriculteurId, nom, superficie, type, loc);
            sp.ajouter(p);

            showAddSuccess("Parcelle ajoutée avec succès.");
            clearAddForm();
            rafraichir();

            // revenir tab liste si tu veux
            tabPane.getSelectionModel().select(0);

        } catch (NumberFormatException ex) {
            showAddError("Agriculteur ID et superficie doivent être numériques.");
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
        // à adapter à ta navigation (changer scène)
    }

    private void clearAddForm() {
        agriculteurIdField.clear();
        nomField.clear();
        superficieField.clear();
        typeTerreCombo.setValue(null);
        localisationField.clear();
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