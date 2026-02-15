package controllers;

import entities.Culture;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import services.ServiceCulture;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CulturesEnVente {

    @FXML private ComboBox<String> typeFilterCombo;
    @FXML private TextField searchField;
    @FXML private Label countLabel;
    @FXML private FlowPane cardsPane;

    private final ServiceCulture sc = new ServiceCulture();

    @FXML
    public void initialize() {
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
            List<Culture> list = sc.recuperer().stream()
                    .filter(c -> c.getEtat() == Culture.Etat.EN_VENTE)
                    .collect(Collectors.toList());

            renderCards(list);
        } catch (SQLException e) {
            showError("Erreur DB: " + e.getMessage());
        }
    }

    @FXML
    void rechercher() {
        try {
            String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
            String type = typeFilterCombo.getValue();

            List<Culture> list = sc.recuperer().stream()
                    .filter(c -> c.getEtat() == Culture.Etat.EN_VENTE)
                    .filter(c -> q.isEmpty() || c.getNom().toLowerCase().contains(q))
                    .filter(c -> "TOUT".equals(type) || c.getTypeCulture().name().equals(type))
                    .collect(Collectors.toList());

            renderCards(list);
        } catch (SQLException e) {
            showError("Erreur DB: " + e.getMessage());
        }
    }

    @FXML
    void retour() {
        // navigation à adapter
    }

    private void renderCards(List<Culture> cultures) {
        cardsPane.getChildren().clear();
        countLabel.setText(cultures.size() + " culture(s) en vente");

        for (Culture c : cultures) {
            cardsPane.getChildren().add(createCard(c));
        }
    }

    private Region createCard(Culture c) {
        // Container card
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setPrefWidth(300);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: #E0E0E0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 8, 0, 0, 2);"
        );

        // Tag (badge)
        Label badge = new Label("EN VENTE");
        badge.setStyle("-fx-background-color:#2E7D32; -fx-text-fill:white; -fx-padding:4 10; -fx-background-radius: 12;");

        // Title
        Label title = new Label(c.getNom());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #212121;");

        // Infos
        Label type = new Label("Type: " + (c.getTypeCulture() != null ? c.getTypeCulture().name() : "-"));
        type.setStyle("-fx-text-fill:#2E7D32;");

        Label superficie = new Label("Superficie: " + c.getSuperficie());
        superficie.setStyle("-fx-text-fill:#616161;");

        Label dateRec = new Label("Date recolte: " + (c.getDateRecolte() != null ? c.getDateRecolte().toString() : "-"));
        dateRec.setStyle("-fx-text-fill:#616161;");

        Label recolte = new Label("Recolte estimee: " + (c.getRecolteEstime() != null ? c.getRecolteEstime() : "-"));
        recolte.setStyle("-fx-text-fill:#616161;");

        Label owner = new Label("Proprietaire ID: " + c.getProprietaireId());
        owner.setStyle("-fx-text-fill:#616161;");

        // Button
        Button contacter = new Button("Contacter");
        contacter.setMaxWidth(Double.MAX_VALUE);
        contacter.setStyle("-fx-background-color:#2E7D32; -fx-text-fill:white; -fx-padding:10 12; -fx-font-weight:bold;");
        contacter.setOnAction(e -> ouvrirPopupContact(c));

        // Layout
        card.getChildren().addAll(badge, title, type, superficie, dateRec, recolte, owner, contacter);
        VBox.setVgrow(contacter, Priority.NEVER);

        return card;
    }

    private void ouvrirPopupContact(Culture c) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Contacter le proprietaire");
        dialog.setHeaderText("Culture: " + c.getNom() + " (ID: " + c.getId() + ")");

        ButtonType sendBtn = new ButtonType("Envoyer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendBtn, ButtonType.CANCEL);

        Label info = new Label("Proprietaire ID: " + c.getProprietaireId());
        TextArea message = new TextArea();
        message.setPromptText("Écrivez votre message...");
        message.setPrefRowCount(6);

        VBox box = new VBox(10, info, new Label("Message *"), message);
        box.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(box);

        dialog.showAndWait().ifPresent(result -> {
            if (result == sendBtn) {
                String msg = message.getText() == null ? "" : message.getText().trim();
                if (msg.isEmpty()) {
                    showError("Message obligatoire.");
                    return;
                }
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Envoyé");
                ok.setHeaderText(null);
                ok.setContentText("Message envoyé au proprietaire (ID: " + c.getProprietaireId() + ").");
                ok.showAndWait();
            }
        });
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erreur");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}
