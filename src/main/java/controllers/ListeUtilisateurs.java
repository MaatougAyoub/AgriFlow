package controllers;

import entities.Agriculteur;
import entities.Admin;
import entities.Expert;
import entities.Utilisateur;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ServiceAgriculteur;
import services.ServiceAdmin;
import services.ServiceExpert;
import services.VerificationAdminService;

import java.io.File;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListeUtilisateurs {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Utilisateur> table;

    @FXML
    private TableColumn<Utilisateur, String> colNom;
    @FXML
    private TableColumn<Utilisateur, String> colPrenom;
    @FXML
    private TableColumn<Utilisateur, Integer> colCin;
    @FXML
    private TableColumn<Utilisateur, String> colEmail;
    @FXML
    private TableColumn<Utilisateur, String> colRole;
    @FXML
    private TableColumn<Utilisateur, String> colDateCreation;

    @FXML
    private TableColumn<Utilisateur, String> colVerification;

    // ✅ images
    @FXML
    private TableColumn<Utilisateur, Void> colSignatureImg;
    @FXML
    private TableColumn<Utilisateur, Void> colCarteProImg;
    @FXML
    private TableColumn<Utilisateur, Void> colCertificationImg;

    // ✅ champs spécifiques (affichés seulement si le type correspond)
    @FXML
    private TableColumn<Utilisateur, String> colAdresse;
    @FXML
    private TableColumn<Utilisateur, String> colParcelles;

    @FXML
    private TableColumn<Utilisateur, Void> colActions;

    @FXML
    private Label countLabel;

    private final ServiceAgriculteur serviceAgriculteur = new ServiceAgriculteur();
    private final ServiceExpert serviceExpert = new ServiceExpert();
    private final ServiceAdmin serviceAdmin = new ServiceAdmin();
    private final VerificationAdminService verificationAdminService = new VerificationAdminService();

    private final ObservableList<Utilisateur> master = FXCollections.observableArrayList();

    private Map<String, Object> userData;

    @FXML
    public void initialize() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupColumns();
        setupImageColumns();
        setupActionsColumn();
        setupWidths();
    }

    public void setUserData(Map<String, Object> userData) {
        this.userData = userData;
        rafraichir(null);
    }

    private boolean isAdmin() {
        return userData != null && "ADMIN".equalsIgnoreCase(String.valueOf(userData.get("role")));
    }

    private int getConnectedUserId() {
        return userData == null ? -1 : Integer.parseInt(String.valueOf(userData.get("id")));
    }

    private void setupColumns() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colCin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        colVerification.setCellValueFactory(cell -> new SimpleStringProperty(n(cell.getValue().getVerificationStatus())));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        colDateCreation.setCellValueFactory(cell -> {
            var d = cell.getValue().getDateCreation();
            return new SimpleStringProperty(d == null ? "" : fmt.format(d));
        });

        // spécifiques
        colAdresse.setCellValueFactory(cell -> {
            Utilisateur u = cell.getValue();
            if (u instanceof Agriculteur a)
                return new SimpleStringProperty(n(a.getAdresse()));
            return new SimpleStringProperty("");
        });

        colParcelles.setCellValueFactory(cell -> {
            Utilisateur u = cell.getValue();
            if (u instanceof Agriculteur a)
                return new SimpleStringProperty(n(a.getParcelles()));
            return new SimpleStringProperty("");
        });

        // wrap (adresse/parcelles)
        colAdresse.setCellFactory(tc -> wrapTextCell());
        colParcelles.setCellFactory(tc -> wrapTextCell());
    }

    private TableCell<Utilisateur, String> wrapTextCell() {
        return new TableCell<>() {
            private final Label lbl = new Label();
            {
                lbl.setWrapText(true);
                lbl.prefWidthProperty().bind(widthProperty().subtract(10));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setGraphic(null);
                else {
                    lbl.setText(item);
                    setGraphic(lbl);
                }
            }
        };
    }

    private void setupImageColumns() {
        colSignatureImg.setCellFactory(col -> new TableCell<>() {
            private final ImageView view = makeThumb();

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= table.getItems().size()) {
                    setGraphic(null);
                    return;
                }
                Utilisateur u = table.getItems().get(getIndex());
                view.setImage(loadImageFromDbPath(u.getSignature()));
                setGraphic(view);
            }
        });

        colCarteProImg.setCellFactory(col -> new TableCell<>() {
            private final ImageView view = makeThumb();

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= table.getItems().size()) {
                    setGraphic(null);
                    return;
                }
                Utilisateur u = table.getItems().get(getIndex());
                if (u instanceof Agriculteur a) {
                    view.setImage(loadImageFromDbPath(a.getCarte_pro()));
                    setGraphic(view);
                } else {
                    setGraphic(null);
                }
            }
        });

        colCertificationImg.setCellFactory(col -> new TableCell<>() {
            private final ImageView view = makeThumb();

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= table.getItems().size()) {
                    setGraphic(null);
                    return;
                }
                Utilisateur u = table.getItems().get(getIndex());
                if (u instanceof Expert e) {
                    view.setImage(loadImageFromDbPath(e.getCertification()));
                    setGraphic(view);
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    private ImageView makeThumb() {
        ImageView v = new ImageView();
        v.setFitWidth(80);
        v.setFitHeight(50);
        v.setPreserveRatio(true);
        v.setSmooth(true);
        return v;
    }

    private Image loadImageFromDbPath(String path) {
        try {
            if (path == null || path.isBlank() || "null".equalsIgnoreCase(path))
                return null;

            String normalized = path.trim()
                    .replace("uploadssignatures", "uploads/signatures/")
                    .replace("uploadscertifications", "uploads/certifications/")
                    .replace("uploadscartes_pro", "uploads/cartes_pro/");

            if (normalized.startsWith("uploads") && !normalized.startsWith("uploads/")) {
                normalized = normalized.replaceFirst("^uploads", "uploads/");
            }

            File file = new File(System.getProperty("user.dir"), normalized);
            if (!file.exists())
                file = new File(normalized);
            if (!file.exists())
                return null;

            return new Image(file.toURI().toString(), true);
        } catch (Exception e) {
            return null;
        }
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Supprimer");
            private final Button btnApprove = new Button("Approuver");
            private final Button btnReject = new Button("Rejeter");
            private final HBox box = new HBox(8, btnApprove, btnReject, btnDelete);

            {
                btnApprove.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-padding: 6 10;");
                btnReject.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-padding: 6 10;");
                btnDelete.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-padding: 6 10;");
                btnDelete.setOnAction(e -> onDeleteClicked(getIndex()));

                btnApprove.setOnAction(e -> onApproveClicked(getIndex()));
                btnReject.setOnAction(e -> onRejectClicked(getIndex()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getIndex() >= table.getItems().size()) {
                    setGraphic(null);
                    return;
                }

                Utilisateur u = table.getItems().get(getIndex());

                // empêcher l'admin connecté de se supprimer lui-même
                btnDelete.setDisable(getConnectedUserId() == u.getId());

                boolean isAdminRow = u.getRole() != null && "ADMIN".equalsIgnoreCase(u.getRole());
                String vs = u.getVerificationStatus() == null ? "" : u.getVerificationStatus();
                boolean alreadyApproved = "APPROVED".equalsIgnoreCase(vs);

                btnApprove.setDisable(isAdminRow || alreadyApproved);
                btnReject.setDisable(isAdminRow);

                setGraphic(box);
            }
        });
    }

    private void setupWidths() {
        colNom.setPrefWidth(130);
        colPrenom.setPrefWidth(130);
        colCin.setPrefWidth(95);
        colEmail.setPrefWidth(190);
        colRole.setPrefWidth(90);
        colDateCreation.setPrefWidth(120);

        colVerification.setPrefWidth(140);

        colSignatureImg.setPrefWidth(110);
        colCarteProImg.setPrefWidth(110);
        colCertificationImg.setPrefWidth(120);

        colAdresse.setPrefWidth(200);
        colParcelles.setPrefWidth(200);

        colActions.setPrefWidth(260);
    }

    private void onApproveClicked(int index) {
        if (index < 0 || index >= table.getItems().size()) return;
        Utilisateur u = table.getItems().get(index);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Approuver l'utilisateur");
        confirm.setContentText("Approuver " + u.getNom() + " " + u.getPrenom() + " ?");
        var res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return;

        try {
            verificationAdminService.updateUserVerification(u.getId(), "APPROVED", null, null);
            rafraichir(null);
        } catch (Exception e) {
            e.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Erreur");
            err.setHeaderText("Impossible d'approuver");
            err.setContentText(e.getMessage());
            err.showAndWait();
        }
    }

    private void onRejectClicked(int index) {
        if (index < 0 || index >= table.getItems().size()) return;
        Utilisateur u = table.getItems().get(index);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rejet");
        dialog.setHeaderText("Rejeter l'utilisateur");
        dialog.setContentText("Raison du rejet:");
        var res = dialog.showAndWait();
        if (res.isEmpty()) return;

        try {
            String reason = res.get().trim();
            verificationAdminService.updateUserVerification(u.getId(), "REJECTED", reason.isEmpty() ? "Rejeté par admin" : reason, null);
            rafraichir(null);
        } catch (Exception e) {
            e.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Erreur");
            err.setHeaderText("Impossible de rejeter");
            err.setContentText(e.getMessage());
            err.showAndWait();
        }
    }

    @FXML
    private void rafraichir(ActionEvent event) {
        if (!isAdmin()) {
            countLabel.setText("Accès refusé (ADMIN uniquement)");
            table.setItems(FXCollections.observableArrayList());
            return;
        }

        try {
            List<Utilisateur> all = new ArrayList<>();

            // ✅ charger tous les rôles existants dans la table
            all.addAll(serviceAgriculteur.recupererAgriculteurs());
            all.addAll(serviceExpert.recupererExpert());
            all.addAll(serviceAdmin.recupererAdmin());

            // ✅ éviter doublons si des rôles sont inconsistants en DB
            Map<Integer, Utilisateur> byId = all.stream()
                    .filter(u -> u != null)
                    .collect(Collectors.toMap(Utilisateur::getId, u -> u, (a, b) -> a));

            master.setAll(byId.values());
            table.setItems(master);
            countLabel.setText(master.size() + " utilisateur(s)");

        } catch (SQLException e) {
            e.printStackTrace();
            countLabel.setText("Erreur chargement");
        }
    }

    @FXML
    private void rechercher(ActionEvent event) {
        String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            table.setItems(master);
            countLabel.setText(master.size() + " utilisateur(s)");
            return;
        }

        List<Utilisateur> filtered = master.stream().filter(u -> s(u.getNom()).contains(q) ||
                s(u.getPrenom()).contains(q) ||
                s(u.getEmail()).contains(q) ||
                s(u.getRole()).contains(q)).collect(Collectors.toList());

        table.setItems(FXCollections.observableArrayList(filtered));
        countLabel.setText(filtered.size() + " utilisateur(s)");
    }

    private String s(String v) {
        return v == null ? "" : v.toLowerCase();
    }

    private String n(String v) {
        return v == null ? "" : v;
    }

    private void onDeleteClicked(int index) {
        if (index < 0 || index >= table.getItems().size())
            return;

        Utilisateur u = table.getItems().get(index);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer utilisateur");
        confirm.setContentText("Supprimer " + u.getNom() + " " + u.getPrenom() + " ?");
        var res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK)
            return;

        try {
            // ✅ suppression via services spécifiques
            if (u instanceof Agriculteur a) {
                serviceAgriculteur.supprimerAgriculteur(a);
            } else if (u instanceof Expert e) {
                serviceExpert.supprimerExpert(e);
            } else if (u instanceof Admin a) {
                serviceAdmin.supprimerAdmin(a);
            } else {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setHeaderText("Suppression non supportée");
                info.setContentText("Suppression non implémentée pour ce type d'utilisateur.");
                info.showAndWait();
            }

            rafraichir(null);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Erreur");
            err.setHeaderText("Suppression irmpossible");
            err.setContentText(e.getMessage());
            err.showAndWait();
        }
    }

    @FXML
    private void retourProfil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.setUserDataAndGoToProfil(userData);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("AgriFlow - Profil");
            stage.setScene(new Scene(root));
            //stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}