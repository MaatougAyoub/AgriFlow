package controllers;

import entities.Reclamation;
import entities.ReclamationRow;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.ServiceReclamation;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListeReclamations {

    @FXML private TextField searchField;
    @FXML private TableView<ReclamationRow> table;

    @FXML private TableColumn<ReclamationRow, String> colNom;
    @FXML private TableColumn<ReclamationRow, String> colPrenom;
    @FXML private TableColumn<ReclamationRow, String> colRole;
    @FXML private TableColumn<ReclamationRow, String> colEmail;

    @FXML private TableColumn<ReclamationRow, String> colCategorie;
    @FXML private TableColumn<ReclamationRow, String> colTitre;
    @FXML private TableColumn<ReclamationRow, String> colDescription;
    @FXML private TableColumn<ReclamationRow, String> colDate;
    @FXML private TableColumn<ReclamationRow, String> colStatut;
    @FXML private TableColumn<ReclamationRow, String> colReponse;

    @FXML private TableColumn<ReclamationRow, Void> colActions;

    @FXML private Label countLabel;

    private final ServiceReclamation service = new ServiceReclamation();

    private ObservableList<ReclamationRow> masterData = FXCollections.observableArrayList();

    // utilisateur connecté (pour vérifier droit de suppression)
    private Map<String, Object> userData;

    public void setUserData(Map<String, Object> userData) {
        this.userData = userData;
    }

    @FXML
    public void initialize() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // ✅ ICI
        setupColumns();
        setupActionsColumn();
        rafraichir(null);
    }

    private void setupColumns() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        colDate.setCellValueFactory(cell -> {
            var d = cell.getValue().getDateCreation();
            return new SimpleStringProperty(d == null ? "" : fmt.format(d));
        });

        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colReponse.setCellValueFactory(new PropertyValueFactory<>("reponse"));

        // wrap texte pour description / réponse (optionnel)
        colDescription.setCellFactory(tc -> wrapCell());
        colReponse.setCellFactory(tc -> wrapCell());
    }

    private TableCell<ReclamationRow, String> wrapCell() {
        return new TableCell<>() {
            private final Label label = new Label();
            {
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    setGraphic(label);
                }
            }
        };
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Supprimer");

            {
                btnDelete.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-padding: 6 10;");
                btnDelete.setOnAction(e -> onDeleteClicked(getIndex()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getIndex() >= table.getItems().size()) {
                    setGraphic(null);
                    return;
                }

                ReclamationRow row = table.getItems().get(getIndex());

                // ✅ supprimer seulement si la réclamation appartient à l'utilisateur connect��
                int connectedUserId = userData == null ? -1 : Integer.parseInt(String.valueOf(userData.get("id")));
                boolean canDelete = (connectedUserId == row.getUtilisateurId());

                btnDelete.setDisable(!canDelete);

                setGraphic(btnDelete);
            }
        });
    }

    private void onDeleteClicked(int index) {
        if (index < 0 || index >= table.getItems().size()) return;

        ReclamationRow row = table.getItems().get(index);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la réclamation");
        confirm.setContentText("Voulez-vous vraiment supprimer cette réclamation ?");
        var res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return;

        try {
            // ServiceReclamation.supprimerReclamation attend un Reclamation, on lui passe l'id
            service.supprimerReclamation(new Reclamation(row.getId(), row.getUtilisateurId(), null, null, null));

            // Refresh
            rafraichir(null);

        } catch (SQLException e) {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Erreur");
            err.setHeaderText("Suppression impossible");
            err.setContentText(e.getMessage());
            err.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void rafraichir(ActionEvent event) {
        try {
            List<ReclamationRow> rows = service.recupererReclamationAvecUtilisateur();
            masterData.setAll(rows);
            table.setItems(masterData);
            countLabel.setText(masterData.size() + " réclamation(s)");
        } catch (SQLException e) {
            e.printStackTrace();
            countLabel.setText("Erreur chargement");
        }
    }

    @FXML
    private void rechercher(ActionEvent event) {
        String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            table.setItems(masterData);
            countLabel.setText(masterData.size() + " réclamation(s)");
            return;
        }

        List<ReclamationRow> filtered = masterData.stream().filter(r ->
                safe(r.getTitre()).contains(q) ||
                        safe(r.getDescription()).contains(q) ||
                        safe(r.getEmail()).contains(q) ||
                        safe(r.getCategorie()).contains(q) ||
                        safe(r.getStatut()).contains(q)
        ).collect(Collectors.toList());

        table.setItems(FXCollections.observableArrayList(filtered));
        countLabel.setText(filtered.size() + " réclamation(s) filtrée(s)");
    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase();
    }

    @FXML
    private void retourProfil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilUtilisateur.fxml"));
            Parent root = loader.load();

            ProfilUtilisateur profil = loader.getController();
            profil.setUserData(userData);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Profil");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}