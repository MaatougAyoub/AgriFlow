package controllers;

import entities.CollabApplication;
import entities.CollabRequest;
import entities.MatchScore;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mains.MainFX;
import services.*;
import services.PredictionService.PredictionResult;
import services.SentimentAnalysisService.SentimentResult;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewApplicationsController {

    @FXML private TableView<CollabApplication> applicationsTable;
    @FXML private TableColumn<CollabApplication, String> fullNameColumn;
    @FXML private TableColumn<CollabApplication, String> phoneColumn;
    @FXML private TableColumn<CollabApplication, String> emailColumn;
    @FXML private TableColumn<CollabApplication, Integer> experienceColumn;
    @FXML private TableColumn<CollabApplication, Double> expectedSalaryColumn;
    @FXML private TableColumn<CollabApplication, String> motivationColumn;
    @FXML private TableColumn<CollabApplication, String> aiScoreColumn;
    @FXML private TableColumn<CollabApplication, String> statusColumn;

    @FXML private Text requestTitleText;
    @FXML private Text applicationsCountText;
    @FXML private VBox emptyStateBox;

    private CollabApplicationService applicationService = new CollabApplicationService();
    private CollabRequestService requestService = new CollabRequestService();

    // Services IA
    private CandidateMatchingService matchingService = new CandidateMatchingService();
    private SentimentAnalysisService sentimentService = new SentimentAnalysisService();
    private PredictionService predictionService = new PredictionService();

    private Long currentRequestId;
    private CollabRequest currentRequest;

    // Cache des scores IA
    private Map<Long, MatchScore> aiScoresCache = new HashMap<>();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        // Configuration des colonnes de base
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        experienceColumn.setCellValueFactory(new PropertyValueFactory<>("yearsOfExperience"));
        expectedSalaryColumn.setCellValueFactory(new PropertyValueFactory<>("expectedSalary"));
        motivationColumn.setCellValueFactory(new PropertyValueFactory<>("motivation"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Style pour l'expérience
        experienceColumn.setCellFactory(column -> new TableCell<CollabApplication, Integer>() {
            @Override
            protected void updateItem(Integer years, boolean empty) {
                super.updateItem(years, empty);
                if (empty || years == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(years + " ans");
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        // Style pour le salaire
        expectedSalaryColumn.setCellFactory(column -> new TableCell<CollabApplication, Double>() {
            @Override
            protected void updateItem(Double salary, boolean empty) {
                super.updateItem(salary, empty);
                if (empty || salary == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f DT", salary));
                    setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
                }
            }
        });

        // Style pour la motivation (limiter le texte)
        motivationColumn.setCellFactory(column -> new TableCell<CollabApplication, String>() {
            @Override
            protected void updateItem(String motivation, boolean empty) {
                super.updateItem(motivation, empty);
                if (empty || motivation == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    String truncated = motivation.length() > 40
                            ? motivation.substring(0, 40) + "..."
                            : motivation;
                    setText(truncated);
                    setTooltip(new Tooltip(motivation));
                    setStyle("-fx-wrap-text: true;");
                }
            }
        });

        // ✨ NOUVELLE COLONNE : Score IA
        aiScoreColumn.setCellValueFactory(cellData -> {
            CollabApplication app = cellData.getValue();
            MatchScore score = aiScoresCache.get(app.getId());

            if (score != null) {
                String display = String.format("%.0f%% %s",
                        score.getTotalScore(),
                        score.getStarsDisplay());
                return javafx.beans.binding.Bindings.createStringBinding(() -> display);
            }

            return javafx.beans.binding.Bindings.createStringBinding(() -> "---");
        });

        aiScoreColumn.setCellFactory(column -> new TableCell<CollabApplication, String>() {
            @Override
            protected void updateItem(String scoreText, boolean empty) {
                super.updateItem(scoreText, empty);
                if (empty || scoreText == null || scoreText.equals("---")) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(scoreText);

                    // Extraire le score numérique
                    String[] parts = scoreText.split("%");
                    if (parts.length > 0) {
                        try {
                            double score = Double.parseDouble(parts[0].trim());

                            if (score >= 80) {
                                setStyle("-fx-background-color: #C8E6C9; -fx-text-fill: #1B5E20; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-font-size: 13px;");
                            } else if (score >= 60) {
                                setStyle("-fx-background-color: #FFF9C4; -fx-text-fill: #F57F17; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-font-size: 13px;");
                            } else {
                                setStyle("-fx-background-color: #FFCCBC; -fx-text-fill: #BF360C; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-font-size: 13px;");
                            }
                        } catch (NumberFormatException e) {
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                }
            }
        });

        // Style pour le statut
        statusColumn.setCellFactory(column -> new TableCell<CollabApplication, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    switch (status) {
                        case "PENDING":
                            setText("EN ATTENTE");
                            setStyle("-fx-background-color: #FFA726; -fx-text-fill: white; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 10;");
                            break;
                        case "APPROVED":
                            setText("ACCEPTÉE");
                            setStyle("-fx-background-color: #66BB6A; -fx-text-fill: white; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 10;");
                            break;
                        case "REJECTED":
                            setText("REJETÉE");
                            setStyle("-fx-background-color: #EF5350; -fx-text-fill: white; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 10;");
                            break;
                        default:
                            setText(status);
                            setStyle("-fx-alignment: CENTER;");
                    }
                }
            }
        });
    }

    private void loadData() {
        currentRequestId = MyRequestsController.getSelectedRequestId();

        if (currentRequestId == null) {
            showError("Erreur", "Aucune demande sélectionnée.");
            handleBack();
            return;
        }

        try {
            currentRequest = requestService.findById(currentRequestId);
            if (currentRequest != null) {
                requestTitleText.setText(currentRequest.getTitle());
            }

            List<CollabApplication> applications = applicationService.getApplicationsByRequestId(currentRequestId);

            if (applications.isEmpty()) {
                applicationsTable.setVisible(false);
                applicationsTable.setManaged(false);
                emptyStateBox.setVisible(true);
                emptyStateBox.setManaged(true);
                applicationsCountText.setText("0 candidature");
            } else {
                applicationsTable.setVisible(true);
                applicationsTable.setManaged(true);
                emptyStateBox.setVisible(false);
                emptyStateBox.setManaged(false);

                applicationsTable.getItems().clear();
                applicationsTable.getItems().addAll(applications);

                applicationsCountText.setText(applications.size() + " candidature" + (applications.size() > 1 ? "s" : ""));
            }

            System.out.println("✅ Chargé " + applications.size() + " candidature(s)");

        } catch (SQLException e) {
            showError("Erreur de chargement", "Impossible de charger les candidatures : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAIAnalysis() {
        List<CollabApplication> applications = applicationsTable.getItems();

        if (applications.isEmpty()) {
            showInfo("Aucune candidature", "Il n'y a aucune candidature à analyser.");
            return;
        }

        System.out.println("\n🤖 ========== LANCEMENT DE L'ANALYSE IA ==========");

        // Calculer les scores IA pour toutes les candidatures
        List<MatchScore> matchScores = matchingService.rankApplications(applications, currentRequest);

        // Mettre en cache les scores
        aiScoresCache.clear();
        for (MatchScore score : matchScores) {
            aiScoresCache.put(score.getApplication().getId(), score);
        }

        // Rafraîchir le tableau pour afficher les scores
        applicationsTable.refresh();

        // Afficher la fenêtre d'analyse détaillée
        showAIInsightsWindow(matchScores, applications);
    }

    /**
     * Affiche une fenêtre popup avec les analyses IA détaillées
     */
    /**
     * Affiche une fenêtre popup avec les analyses IA détaillées (DESIGN AMÉLIORÉ)
     */
    private void showAIInsightsWindow(List<MatchScore> matchScores, List<CollabApplication> applications) {
        Stage aiStage = new Stage();
        aiStage.initModality(Modality.APPLICATION_MODAL);
        aiStage.setTitle("🤖 AI Insights - Analyses Avancées");

        // ScrollPane principal
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        VBox root = new VBox(30);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        // ========== HEADER ==========
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); " +
                "-fx-padding: 30; " +
                "-fx-background-radius: 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");

        Text icon = new Text("🤖");
        icon.setStyle("-fx-font-size: 60px;");

        Text title = new Text("ANALYSES D'INTELLIGENCE ARTIFICIELLE");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 5, 0, 0, 2);");

        Text subtitle = new Text("Système d'aide à la décision basé sur 4 algorithmes d'IA");
        subtitle.setStyle("-fx-font-size: 14px; -fx-fill: rgba(255,255,255,0.9);");

        header.getChildren().addAll(icon, title, subtitle);

        // ========== SECTION 1: MATCHING AUTOMATIQUE ==========
        VBox matchingSection = createModernSection(
                "🎯 MATCHING AUTOMATIQUE",
                "#FF6B6B", // Rouge-rose
                "Analyse de compatibilité candidat ↔ demande"
        );

        for (int i = 0; i < matchScores.size(); i++) {
            MatchScore score = matchScores.get(i);
            VBox candidateCard = createCandidateCard(i + 1, score);
            matchingSection.getChildren().add(candidateCard);
        }

        // ========== SECTION 2: ANALYSE DE SENTIMENT ==========
        VBox sentimentSection = createModernSection(
                "💬 ANALYSE DE SENTIMENT (NLP)",
                "#4ECDC4", // Turquoise
                "Évaluation de la qualité des motivations"
        );

        List<SentimentResult> sentimentResults = sentimentService.analyzeMultipleCandidates(applications);

        for (SentimentResult result : sentimentResults) {
            VBox sentimentCard = createSentimentCard(result);
            sentimentSection.getChildren().add(sentimentCard);
        }

        // ========== SECTION 3: PRÉDICTION DE SUCCÈS ==========
        VBox predictionSection = createModernSection(
                "🔮 PRÉDICTION DE SUCCÈS (ML)",
                "#A8E6CF", // Vert menthe
                "Probabilité d'acceptation basée sur l'historique"
        );

        List<PredictionResult> predictions = predictionService.predictMultiple(applications, currentRequest);

        for (PredictionResult pred : predictions) {
            VBox predCard = createPredictionCard(pred);
            predictionSection.getChildren().add(predCard);
        }

        // ========== FOOTER ==========
        VBox footer = new VBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(30));

        Button closeBtn = new Button("✓ Fermer");
        closeBtn.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #667eea; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 15 50; " +
                        "-fx-background-radius: 25; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);"
        );
        closeBtn.setOnMouseEntered(e ->
                closeBtn.setStyle(closeBtn.getStyle() + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;")
        );
        closeBtn.setOnMouseExited(e ->
                closeBtn.setStyle(closeBtn.getStyle().replace("-fx-scale-x: 1.05; -fx-scale-y: 1.05;", ""))
        );
        closeBtn.setOnAction(e -> aiStage.close());

        Text footerText = new Text("Propulsé par AgriFlow AI Engine v1.0");
        footerText.setStyle("-fx-fill: rgba(255,255,255,0.7); -fx-font-size: 12px;");

        footer.getChildren().addAll(closeBtn, footerText);

        // Assembler tout
        root.getChildren().addAll(header, matchingSection, sentimentSection, predictionSection, footer);
        scrollPane.setContent(root);

        Scene scene = new Scene(scrollPane, 950, 750);
        aiStage.setScene(scene);
        aiStage.show();

        showInfo("✅ Analyse terminée",
                "L'analyse IA a été effectuée avec succès !\n\n" +
                        "Les scores sont maintenant affichés dans le tableau.");
    }

    /**
     * Crée une section moderne avec header coloré
     */
    private VBox createModernSection(String title, String color, String subtitle) {
        VBox section = new VBox(20);
        section.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 0; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);"
        );

        // Header de section
        VBox sectionHeader = new VBox(5);
        sectionHeader.setPadding(new Insets(25));
        sectionHeader.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-background-radius: 20 20 0 0;"
        );

        Text titleText = new Text(title);
        titleText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: white;");

        Text subtitleText = new Text(subtitle);
        subtitleText.setStyle("-fx-font-size: 13px; -fx-fill: rgba(255,255,255,0.9);");

        sectionHeader.getChildren().addAll(titleText, subtitleText);

        VBox content = new VBox(15);
        content.setPadding(new Insets(25));

        section.getChildren().addAll(sectionHeader, content);

        return section;
    }

    /**
     * Crée une carte pour un candidat (matching)
     */
    private VBox createCandidateCard(int position, MatchScore score) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: #dee2e6; " +
                        "-fx-border-radius: 15; " +
                        "-fx-border-width: 1;"
        );

        // Header de la carte
        HBox cardHeader = new HBox(15);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        Text positionText = new Text("#" + position);
        positionText.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-fill: #667eea;"
        );

        Text nameText = new Text(score.getApplication().getFullName());
        nameText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Text scoreText = new Text(String.format("%.0f%%", score.getTotalScore()));
        scoreText.setStyle(
                "-fx-font-size: 28px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-fill: " + getScoreColor(score.getTotalScore()) + ";"
        );

        Text starsText = new Text(score.getStarsDisplay());
        starsText.setStyle("-fx-font-size: 20px;");

        cardHeader.getChildren().addAll(positionText, nameText, spacer, scoreText, starsText);

        // Barre de progression
        ProgressBar progressBar = new ProgressBar(score.getTotalScore() / 100.0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setStyle(
                "-fx-accent: " + getScoreColor(score.getTotalScore()) + ";"
        );

        // Recommandation
        Text recommendation = new Text(score.getRecommendation());
        recommendation.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-fill: " + getScoreColor(score.getTotalScore()) + ";"
        );

        // Détails des scores
        HBox details = new HBox(20);
        details.setAlignment(Pos.CENTER_LEFT);
        details.setPadding(new Insets(10, 0, 0, 0));

        details.getChildren().addAll(
                createDetailBadge("💼 Exp", score.getExperienceScore()),
                createDetailBadge("💰 Salaire", score.getSalaryScore()),
                createDetailBadge("📍 Lieu", score.getLocationScore()),
                createDetailBadge("📅 Dispo", score.getAvailabilityScore())
        );

        card.getChildren().addAll(cardHeader, progressBar, recommendation, details);

        return card;
    }

    /**
     * Crée une carte pour l'analyse de sentiment
     */
    private VBox createSentimentCard(SentimentResult result) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: #dee2e6; " +
                        "-fx-border-radius: 15; " +
                        "-fx-border-width: 1;"
        );

        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Text nameText = new Text(result.getCandidateName());
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Text scoreText = new Text(String.format("%.0f%%", result.getScore()));
        scoreText.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-fill: " + getSentimentColor(result.getSentiment()) + ";"
        );

        Text sentimentText = new Text(result.getSentiment());
        sentimentText.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-fill: " + getSentimentColor(result.getSentiment()) + ";"
        );

        header.getChildren().addAll(nameText, spacer, scoreText, sentimentText);

        // Barre de progression
        ProgressBar progressBar = new ProgressBar(result.getScore() / 100.0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setStyle(
                "-fx-accent: " + getSentimentColor(result.getSentiment()) + ";"
        );

        // Analyse
        Text analysis = new Text(result.getAnalysis());
        analysis.setStyle("-fx-font-size: 12px; -fx-fill: #6c757d; -fx-wrap-text: true;");
        analysis.setWrappingWidth(800);

        card.getChildren().addAll(header, progressBar, analysis);

        return card;
    }

    /**
     * Crée une carte pour la prédiction
     */
    private VBox createPredictionCard(PredictionResult pred) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: #dee2e6; " +
                        "-fx-border-radius: 15; " +
                        "-fx-border-width: 1;"
        );

        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Text nameText = new Text(pred.getCandidateName());
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Text probText = new Text(String.format("%.0f%%", pred.getProbability()));
        probText.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-fill: " + getScoreColor(pred.getProbability()) + ";"
        );

        header.getChildren().addAll(nameText, spacer, probText);

        // Barre de progression
        ProgressBar progressBar = new ProgressBar(pred.getProbability() / 100.0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setStyle(
                "-fx-accent: " + getScoreColor(pred.getProbability()) + ";"
        );

        // Prédiction
        HBox predBox = new HBox(10);
        predBox.setAlignment(Pos.CENTER_LEFT);

        Text predLabel = new Text("Prédiction:");
        predLabel.setStyle("-fx-font-size: 13px; -fx-fill: #6c757d;");

        Text predValue = new Text(pred.getPrediction());
        predValue.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-fill: " + getScoreColor(pred.getProbability()) + ";"
        );

        Text confText = new Text("| Confiance: " + pred.getConfidence());
        confText.setStyle("-fx-font-size: 13px; -fx-fill: #6c757d;");

        predBox.getChildren().addAll(predLabel, predValue, confText);

        // Facteurs clés
        VBox factorsBox = new VBox(5);
        Text factorsTitle = new Text("Facteurs clés:");
        factorsTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-fill: #495057;");
        factorsBox.getChildren().add(factorsTitle);

        for (String factor : pred.getKeyFactors()) {
            Text factorText = new Text("  • " + factor);
            factorText.setStyle("-fx-font-size: 11px; -fx-fill: #6c757d;");
            factorsBox.getChildren().add(factorText);
        }

        card.getChildren().addAll(header, progressBar, predBox, factorsBox);

        return card;
    }

    /**
     * Crée un badge de détail
     */
    private VBox createDetailBadge(String label, double score) {
        VBox badge = new VBox(3);
        badge.setAlignment(Pos.CENTER);
        badge.setPadding(new Insets(8, 12, 8, 12));
        badge.setStyle(
                "-fx-background-color: " + getScoreColor(score) + "20; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: " + getScoreColor(score) + "; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 1;"
        );

        Text labelText = new Text(label);
        labelText.setStyle("-fx-font-size: 11px; -fx-fill: #6c757d;");

        Text scoreText = new Text(String.format("%.0f%%", score));
        scoreText.setStyle(
                "-fx-font-size: 13px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-fill: " + getScoreColor(score) + ";"
        );

        badge.getChildren().addAll(labelText, scoreText);

        return badge;
    }

    /**
     * Retourne la couleur en fonction du score
     */
    private String getScoreColor(double score) {
        if (score >= 80) return "#10b981"; // Vert
        if (score >= 60) return "#3b82f6"; // Bleu
        if (score >= 40) return "#f59e0b"; // Orange
        return "#ef4444"; // Rouge
    }

    /**
     * Retourne la couleur en fonction du sentiment
     */
    private String getSentimentColor(String sentiment) {
        switch (sentiment) {
            case "TRÈS POSITIF": return "#10b981";
            case "POSITIF": return "#3b82f6";
            case "NEUTRE": return "#6b7280";
            case "NÉGATIF": return "#f59e0b";
            case "TRÈS NÉGATIF": return "#ef4444";
            default: return "#6b7280";
        }
    }

    /**
     * Crée une section stylisée
     */
    private VBox createSection(String title) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-radius: 8; -fx-background-radius: 8;");

        Text titleText = new Text(title);
        titleText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #333;");
        section.getChildren().add(titleText);

        return section;
    }

    @FXML
    private void handleApprove() {
        CollabApplication selected = applicationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Aucune sélection", "Veuillez sélectionner une candidature à accepter.");
            return;
        }

        if ("APPROVED".equals(selected.getStatus())) {
            showInfo("Déjà acceptée", "Cette candidature a déjà été acceptée.");
            return;
        }

        // Afficher le score IA si disponible
        MatchScore aiScore = aiScoresCache.get(selected.getId());
        String aiInfo = "";
        if (aiScore != null) {
            aiInfo = "\n\n🤖 Score IA: " + aiScore.getTotalScore() + "% " + aiScore.getStarsDisplay() +
                    "\n   " + aiScore.getRecommendation();
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Accepter cette candidature ?");
        confirm.setContentText("Candidat : " + selected.getFullName() + "\n" +
                "Expérience : " + selected.getYearsOfExperience() + " ans\n" +
                "Salaire demandé : " + selected.getExpectedSalary() + " DT/jour" +
                aiInfo);

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                applicationService.approveApplication(selected.getId());
                showInfo("Succès", "Candidature acceptée avec succès !\n\n" +
                        "Le candidat sera notifié de votre décision.");
                loadData();
            } catch (SQLException e) {
                showError("Erreur", "Impossible d'accepter la candidature.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleReject() {
        CollabApplication selected = applicationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Aucune sélection", "Veuillez sélectionner une candidature à rejeter.");
            return;
        }

        if ("REJECTED".equals(selected.getStatus())) {
            showInfo("Déjà rejetée", "Cette candidature a déjà été rejetée.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Rejeter cette candidature ?");
        confirm.setContentText("Candidat : " + selected.getFullName());

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                applicationService.rejectApplication(selected.getId());
                showInfo("Candidature rejetée", "La candidature a été rejetée.");
                loadData();
            } catch (SQLException e) {
                showError("Erreur", "Impossible de rejeter la candidature.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRefresh() {
        System.out.println("🔄 Rafraîchissement de la liste...");
        aiScoresCache.clear(); // Vider le cache des scores IA
        loadData();
        showInfo("Rafraîchi", "La liste des candidatures a été mise à jour.");
    }

    @FXML
    private void handleBack() {
        MainFX.showMyRequests();
    }

    // Méthodes utilitaires
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
