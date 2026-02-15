/*
package controllers;

import entities.Categorie;
import entities.Reclamation;
import entities.ReclamationRow;
import entities.Statut;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ServiceReclamation;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListeReclamations {

    // TAB1
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categorieFilterCombo;

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

    // TAB2
    @FXML private ComboBox<String> categorieAddCombo;
    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;

    @FXML private Label addErrorLabel;
    @FXML private Label addSuccessLabel;

    private final ServiceReclamation service = new ServiceReclamation();

    private final ObservableList<ReclamationRow> masterData = FXCollections.observableArrayList();

    private Map<String, Object> userData;

    public void setUserData(Map<String, Object> userData) {
        this.userData = userData;
    }

    @FXML
    public void initialize() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        initCategoryCombos();
        setupColumns();
        setupActionsColumn();

        rafraichir(null);
    }

    private void initCategoryCombos() {
        // Filtre : "Toutes" + catégories
        categorieFilterCombo.setItems(FXCollections.observableArrayList(
                "Toutes",
                "TECHNIQUE", "ACCESS", "DELIVERY", "PAIMENT", "SERVICE", "AUTRE"
        ));
        categorieFilterCombo.getSelectionModel().selectFirst();

        // Ajout : catégories
        categorieAddCombo.setItems(FXCollections.observableArrayList(
                "TECHNIQUE", "ACCESS", "DELIVERY", "PAIMENT", "SERVICE", "AUTRE"
        ));

        // quand filtre change => appliquer recherche
        categorieFilterCombo.setOnAction(e -> rechercher(null));
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
            private final Button btnReply = new Button("Répondre");
            private final HBox box = new HBox(8, btnDelete, btnReply);

            {
                btnDelete.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-padding: 6 10;");
                btnReply.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-padding: 6 10;");

                btnDelete.setOnAction(e -> onDeleteClicked(getIndex()));
                btnReply.setOnAction(e -> onReplyClicked(getIndex()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getIndex() >= table.getItems().size()) {
                    setGraphic(null);
                    return;
                }

                ReclamationRow row = table.getItems().get(getIndex());

                int connectedUserId = getConnectedUserId();
                boolean canDelete = (connectedUserId == row.getUtilisateurId());
                btnDelete.setDisable(!canDelete);

                // (Option) limiter réponse à ADMIN seulement:
                // btnReply.setDisable(!isAdmin());

                setGraphic(box);
            }
        });
    }

    private int getConnectedUserId() {
        return userData == null ? -1 : Integer.parseInt(String.valueOf(userData.get("id")));
    }

    private String getConnectedIdentity() {
        String nom = String.valueOf(userData.get("nom"));
        String prenom = String.valueOf(userData.get("prenom"));
        String role = String.valueOf(userData.get("role"));
        return nom + " " + prenom + " (" + role + ")";
    }

    private void onReplyClicked(int index) {
        if (index < 0 || index >= table.getItems().size()) return;
        ReclamationRow row = table.getItems().get(index);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Répondre");
        dialog.setHeaderText("Réponse à la réclamation: " + row.getTitre());
        dialog.setContentText("Votre réponse :");

        var res = dialog.showAndWait();
        if (res.isEmpty()) return;

        String reponse = res.get().trim();
        if (reponse.isEmpty()) return;

        String formatted = getConnectedIdentity() + " : " + reponse;

        try {
            service.repondreAReclamation(row.getId(), formatted);
            rafraichir(null);
        } catch (SQLException e) {
            showErrorDialog("Erreur", "Impossible d'enregistrer la réponse", e.getMessage());
            e.printStackTrace();
        }
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
            service.supprimerReclamation(new Reclamation(row.getId(), row.getUtilisateurId(), null, null, null));
            rafraichir(null);
        } catch (SQLException e) {
            showErrorDialog("Erreur", "Suppression impossible", e.getMessage());
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
            rechercher(null); // réapplique filtre/recherche
        } catch (SQLException e) {
            e.printStackTrace();
            countLabel.setText("Erreur chargement");
        }
    }

    @FXML
    private void rechercher(ActionEvent event) {
        String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String cat = categorieFilterCombo.getValue() == null ? "Toutes" : categorieFilterCombo.getValue();

        List<ReclamationRow> filtered = masterData.stream().filter(r -> {
            boolean matchText = q.isEmpty()
                    || safe(r.getTitre()).contains(q)
                    || safe(r.getDescription()).contains(q)
                    || safe(r.getEmail()).contains(q)
                    || safe(r.getCategorie()).contains(q)
                    || safe(r.getStatut()).contains(q);

            boolean matchCat = "Toutes".equals(cat) || cat.equalsIgnoreCase(r.getCategorie());

            return matchText && matchCat;
        }).collect(Collectors.toList());

        table.setItems(FXCollections.observableArrayList(filtered));
        countLabel.setText(filtered.size() + " réclamation(s)");
    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase();
    }

    // ===== TAB2: ajout =====

    @FXML
    private void validerAjout(ActionEvent event) {
        hideAddMessages();

        if (userData == null) {
            showAddError("Utilisateur non connecté.");
            return;
        }

        String cat = categorieAddCombo.getValue();
        String titre = titreField.getText() == null ? "" : titreField.getText().trim();
        String desc = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();

        if (cat == null || cat.isBlank()) {
            showAddError("Veuillez choisir une catégorie.");
            return;
        }
        if (titre.isBlank()) {
            showAddError("Le titre est obligatoire.");
            return;
        }
        if (desc.isBlank()) {
            showAddError("La description est obligatoire.");
            return;
        }

        try {
            int userId = getConnectedUserId();

            Reclamation r = new Reclamation(userId, Categorie.valueOf(cat), titre, desc);
            // statut et réponse sont gérés dans le service
            service.ajouterReclamation(r);

            showAddSuccess("Réclamation ajoutée avec succès ✅");
            clearAddForm();

            // Revenir à l'onglet liste + refresh
            rafraichir(null);

        } catch (Exception e) {
            showAddError("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annulerAjout(ActionEvent event) {
        hideAddMessages();
        clearAddForm();
    }

    private void clearAddForm() {
        categorieAddCombo.getSelectionModel().clearSelection();
        titreField.clear();
        descriptionArea.clear();
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

    private void hideAddMessages() {
        addErrorLabel.setVisible(false);
        addSuccessLabel.setVisible(false);
    }

    private void showErrorDialog(String title, String header, String content) {
        Alert err = new Alert(Alert.AlertType.ERROR);
        err.setTitle(title);
        err.setHeaderText(header);
        err.setContentText(content);
        err.showAndWait();
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
}*/

package controllers;

import entities.Categorie;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ServiceReclamation;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListeReclamations {

    // TAB1
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categorieFilterCombo;

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

    // TAB2
    @FXML private ComboBox<String> categorieAddCombo;
    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;

    @FXML private Label addErrorLabel;
    @FXML private Label addSuccessLabel;

    private final ServiceReclamation service = new ServiceReclamation();
    private final ObservableList<ReclamationRow> masterData = FXCollections.observableArrayList();

    private Map<String, Object> userData;

    public void setUserData(Map<String, Object> userData) {
        this.userData = userData;
    }

    @FXML
    public void initialize() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        initCategoryCombos();
        setupColumns();
        setupActionsColumn();
        setupColumnWidths();

        rafraichir(null);
    }

    private void initCategoryCombos() {
        categorieFilterCombo.setItems(FXCollections.observableArrayList(
                "Toutes",
                "TECHNIQUE", "ACCESS", "DELIVERY", "PAIMENT", "SERVICE", "AUTRE"
        ));
        categorieFilterCombo.getSelectionModel().selectFirst();
        categorieFilterCombo.setOnAction(e -> rechercher(null));

        categorieAddCombo.setItems(FXCollections.observableArrayList(
                "TECHNIQUE", "ACCESS", "DELIVERY", "PAIMENT", "SERVICE", "AUTRE"
        ));
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

        // ✅ wrap + retour à la ligne pour texte long
        colDescription.setCellFactory(tc -> wrapCell());
        colReponse.setCellFactory(tc -> wrapCell());
    }

    /**
     * ✅ Réserver plus de largeur à Description et Réponse
     * (TableView est en CONSTRAINED_RESIZE_POLICY -> PrefWidth aide à répartir)
     */
    private void setupColumnWidths() {
        colNom.setPrefWidth(120);
        colPrenom.setPrefWidth(120);
        colRole.setPrefWidth(110);
        colEmail.setPrefWidth(210);

        colCategorie.setPrefWidth(120);
        colTitre.setPrefWidth(180);

        // ✅ plus large
        colDescription.setPrefWidth(290);
        colDescription.setMinWidth(230);

        colDate.setPrefWidth(150);
        colStatut.setPrefWidth(110);

        // ✅ plus large
        colReponse.setPrefWidth(340);
        colReponse.setMinWidth(280);

        colActions.setPrefWidth(220);
    }

    /**
     * ✅ Cellule qui wrap le texte + permet affichage multi-lignes
     */
    private TableCell<ReclamationRow, String> wrapCell() {
        return new TableCell<>() {
            private final Label label = new Label();

            {
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);

                // important: le wrap dépend de la largeur de la cellule
                label.prefWidthProperty().bind(this.widthProperty().subtract(10));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setPrefHeight(Control.USE_COMPUTED_SIZE);
                } else {
                    // normaliser retours ligne Windows
                    label.setText(item.replace("\r\n", "\n"));
                    setGraphic(label);
                    setPrefHeight(Control.USE_COMPUTED_SIZE);
                }
            }
        };
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Supprimer");
            private final Button btnReply = new Button("Répondre");
            private final HBox box = new HBox(8, btnDelete, btnReply);

            {
                btnDelete.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-padding: 6 10;");
                btnReply.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-padding: 6 10;");

                btnDelete.setOnAction(e -> onDeleteClicked(getIndex()));
                btnReply.setOnAction(e -> onReplyClicked(getIndex()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getIndex() >= table.getItems().size()) {
                    setGraphic(null);
                    return;
                }

                ReclamationRow row = table.getItems().get(getIndex());

                int connectedUserId = getConnectedUserId();
                boolean canDelete = (connectedUserId == row.getUtilisateurId());
                btnDelete.setDisable(!canDelete);

                setGraphic(box);
            }
        });
    }

    private int getConnectedUserId() {
        return userData == null ? -1 : Integer.parseInt(String.valueOf(userData.get("id")));
    }

    private String getConnectedIdentity() {
        String nom = String.valueOf(userData.get("nom"));
        String prenom = String.valueOf(userData.get("prenom"));
        String role = String.valueOf(userData.get("role"));
        return nom + " " + prenom + " (" + role + ")";
    }

    private void onReplyClicked(int index) {
        if (index < 0 || index >= table.getItems().size()) return;
        ReclamationRow row = table.getItems().get(index);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Répondre");
        dialog.setHeaderText("Réponse à la réclamation: " + row.getTitre());
        dialog.setContentText("Votre réponse :");

        var res = dialog.showAndWait();
        if (res.isEmpty()) return;

        String reponse = res.get().trim();
        if (reponse.isEmpty()) return;

        String formatted = getConnectedIdentity() + " : " + reponse;

        try {
            // ✅ concatène au lieu d’écraser
            service.ajouterReponseConcatenee(row.getId(), formatted);
            rafraichir(null);
        } catch (SQLException e) {
            showErrorDialog("Erreur", "Impossible d'enregistrer la réponse", e.getMessage());
            e.printStackTrace();
        }
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
            service.supprimerReclamation(new Reclamation(row.getId(), row.getUtilisateurId(), null, null, null));
            rafraichir(null);
        } catch (SQLException e) {
            showErrorDialog("Erreur", "Suppression impossible", e.getMessage());
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
            rechercher(null); // réappliquer filtre
        } catch (SQLException e) {
            e.printStackTrace();
            countLabel.setText("Erreur chargement");
        }
    }

    @FXML
    private void rechercher(ActionEvent event) {
        String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String cat = (categorieFilterCombo.getValue() == null) ? "Toutes" : categorieFilterCombo.getValue();

        List<ReclamationRow> filtered = masterData.stream().filter(r -> {
            boolean matchText = q.isEmpty()
                    || safe(r.getTitre()).contains(q)
                    || safe(r.getDescription()).contains(q)
                    || safe(r.getEmail()).contains(q)
                    || safe(r.getCategorie()).contains(q)
                    || safe(r.getStatut()).contains(q)
                    || safe(r.getReponse()).contains(q);

            boolean matchCat = "Toutes".equals(cat) || cat.equalsIgnoreCase(r.getCategorie());

            return matchText && matchCat;
        }).collect(Collectors.toList());

        table.setItems(FXCollections.observableArrayList(filtered));
        countLabel.setText(filtered.size() + " réclamation(s)");
    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase();
    }

    // ===== TAB2: ajout =====

    @FXML
    private void validerAjout(ActionEvent event) {
        hideAddMessages();

        if (userData == null) {
            showAddError("Utilisateur non connecté.");
            return;
        }

        String cat = categorieAddCombo.getValue();
        String titre = titreField.getText() == null ? "" : titreField.getText().trim();
        String desc = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();

        if (cat == null || cat.isBlank()) {
            showAddError("Veuillez choisir une catégorie.");
            return;
        }
        if (titre.isBlank()) {
            showAddError("Le titre est obligatoire.");
            return;
        }
        if (desc.isBlank()) {
            showAddError("La description est obligatoire.");
            return;
        }

        try {
            int userId = getConnectedUserId();

            Reclamation r = new Reclamation(userId, Categorie.valueOf(cat), titre, desc);
            service.ajouterReclamation(r);

            showAddSuccess("Réclamation ajoutée avec succès ✅");
            clearAddForm();

            rafraichir(null);

        } catch (Exception e) {
            showAddError("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annulerAjout(ActionEvent event) {
        hideAddMessages();
        clearAddForm();
    }

    private void clearAddForm() {
        categorieAddCombo.getSelectionModel().clearSelection();
        titreField.clear();
        descriptionArea.clear();
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

    private void hideAddMessages() {
        addErrorLabel.setVisible(false);
        addSuccessLabel.setVisible(false);
    }

    private void showErrorDialog(String title, String header, String content) {
        Alert err = new Alert(Alert.AlertType.ERROR);
        err.setTitle(title);
        err.setHeaderText(header);
        err.setContentText(content);
        err.showAndWait();
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
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}