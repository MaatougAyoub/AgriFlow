package services;

import entities.CollabApplication;
import entities.CollabRequest;
import services.SentimentAnalysisService.SentimentResult;
import java.util.stream.Collectors;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 🤖 Service d'IA pour la Prédiction de Succès (Machine Learning)
 * Prédit si une candidature sera acceptée ou rejetée
 */
public class PredictionService {

    private CollabApplicationService applicationService;
    private SentimentAnalysisService sentimentService;

    public PredictionService() {
        this.applicationService = new CollabApplicationService();
        this.sentimentService = new SentimentAnalysisService();
    }

    /**
     * Prédit la probabilité d'acceptation d'une candidature
     */
    public PredictionResult predictSuccess(CollabApplication application, CollabRequest request) {
        System.out.println("\n🤖 IA - Prédiction de succès pour: " + application.getFullName());

        // 1️⃣ Analyse de l'expérience (poids: 25%)
        double experienceScore = analyzeExperience(application.getYearsOfExperience()) * 0.25;

        // 2️⃣ Analyse du salaire (poids: 30%)
        double salaryScore = analyzeSalary(application.getExpectedSalary(), request.getSalaryPerDay()) * 0.30;

        // 3️⃣ Analyse de la motivation (poids: 25%)
        SentimentResult sentiment = sentimentService.analyzeSentiment(application);
        double motivationScore = sentiment.getScore() * 0.25;

        // 4️⃣ Analyse de l'historique (poids: 20%)
        double historyScore = analyzeHistory(application.getCandidateId()) * 0.20;

        // Calculer la probabilité finale
        double probability = experienceScore + salaryScore + motivationScore + historyScore;
        probability = Math.min(100.0, probability); // Limiter à 100%

        // Déterminer la prédiction
        String prediction = determinePrediction(probability);
        String confidence = determineConfidence(probability);

        // Générer les facteurs clés
        List<String> keyFactors = generateKeyFactors(
                experienceScore / 0.25,
                salaryScore / 0.30,
                sentiment.getScore(),
                historyScore / 0.20
        );

        System.out.println("   ✅ Probabilité d'acceptation: " + probability + "% - " + prediction);

        return new PredictionResult(
                application.getId(),
                application.getFullName(),
                probability,
                prediction,
                confidence,
                keyFactors
        );
    }

    /**
     * Analyse le score d'expérience
     */
    private double analyzeExperience(int years) {
        if (years >= 10) return 100.0;
        if (years >= 7) return 90.0;
        if (years >= 5) return 80.0;
        if (years >= 3) return 65.0;
        if (years >= 1) return 45.0;
        return 25.0;
    }

    /**
     * Analyse le score de salaire
     */
    private double analyzeSalary(double expected, double offered) {
        if (expected <= offered) return 100.0;

        double ratio = expected / offered;

        if (ratio <= 1.05) return 95.0;  // +5%
        if (ratio <= 1.10) return 80.0;  // +10%
        if (ratio <= 1.20) return 60.0;  // +20%
        if (ratio <= 1.30) return 40.0;  // +30%
        return 20.0; // >30%
    }

    /**
     * Analyse l'historique du candidat
     * Basé sur ses candidatures précédentes
     */
    private double analyzeHistory(Long candidateId) {
        try {
            // Récupérer toutes les candidatures du candidat
            List<CollabApplication> allApplications = applicationService.findAll();

            List<CollabApplication> history = allApplications.stream()
                    .filter(app -> {
                        Long appCandidateId = app.getCandidateId();
                        return appCandidateId != null && appCandidateId.equals(candidateId);
                    })
                    .collect(Collectors.toList());

            if (history.isEmpty()) {
                System.out.println("   ℹ️ Aucun historique trouvé pour le candidat #" + candidateId);
                return 50.0; // Pas d'historique = score neutre
            }

            // Compter les candidatures acceptées
            long acceptedCount = history.stream()
                    .filter(app -> "APPROVED".equals(app.getStatus()))
                    .count();

            System.out.println("   📊 Historique: " + acceptedCount + "/" + history.size() + " candidature(s) acceptée(s)");

            // Calculer le taux d'acceptation
            double acceptanceRate = (double) acceptedCount / history.size();

            // Convertir en score
            if (acceptanceRate >= 0.75) return 100.0; // 75%+ acceptées
            if (acceptanceRate >= 0.50) return 80.0;  // 50-74% acceptées
            if (acceptanceRate >= 0.25) return 60.0;  // 25-49% acceptées
            return 40.0; // <25% acceptées

        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors de l'analyse de l'historique: " + e.getMessage());
            e.printStackTrace();
            return 50.0; // Score neutre en cas d'erreur
        }
    }

    /**
     * Détermine la prédiction finale
     */
    private String determinePrediction(double probability) {
        if (probability >= 75) return "ACCEPTATION TRÈS PROBABLE";
        if (probability >= 60) return "ACCEPTATION PROBABLE";
        if (probability >= 40) return "INCERTAIN";
        if (probability >= 25) return "REJET PROBABLE";
        return "REJET TRÈS PROBABLE";
    }

    /**
     * Détermine le niveau de confiance
     */
    private String determineConfidence(double probability) {
        if (probability >= 80 || probability <= 20) return "HAUTE";
        if (probability >= 60 || probability <= 40) return "MOYENNE";
        return "FAIBLE";
    }

    /**
     * Génère les facteurs clés de décision
     */
    private List<String> generateKeyFactors(double expScore, double salScore,
                                            double motScore, double histScore) {
        List<String> factors = new ArrayList<>();

        // Expérience
        if (expScore >= 80) {
            factors.add("✅ Excellente expérience");
        } else if (expScore >= 60) {
            factors.add("✅ Bonne expérience");
        } else if (expScore < 50) {
            factors.add("⚠️ Expérience limitée");
        }

        // Salaire
        if (salScore >= 90) {
            factors.add("✅ Salaire très compétitif");
        } else if (salScore >= 70) {
            factors.add("✅ Salaire acceptable");
        } else if (salScore < 50) {
            factors.add("❌ Salaire trop élevé");
        }

        // Motivation
        if (motScore >= 75) {
            factors.add("✅ Excellente motivation");
        } else if (motScore >= 55) {
            factors.add("✅ Bonne motivation");
        } else if (motScore < 40) {
            factors.add("⚠️ Motivation faible");
        }

        // Historique
        if (histScore >= 80) {
            factors.add("✅ Excellent historique");
        } else if (histScore >= 60) {
            factors.add("✅ Bon historique");
        } else if (histScore < 50) {
            factors.add("⚠️ Historique mitigé");
        }

        return factors;
    }

    /**
     * Prédit le succès de plusieurs candidatures
     */
    public List<PredictionResult> predictMultiple(List<CollabApplication> applications,
                                                  CollabRequest request) {
        List<PredictionResult> predictions = new ArrayList<>();

        System.out.println("\n🤖 IA - Prédiction pour " + applications.size() + " candidature(s)...");

        for (CollabApplication app : applications) {
            PredictionResult result = predictSuccess(app, request);
            predictions.add(result);
        }

        // Trier par probabilité décroissante
        predictions.sort((p1, p2) -> Double.compare(p2.getProbability(), p1.getProbability()));

        System.out.println("✅ Prédictions terminées !");

        return predictions;
    }

    /**
     * Affiche les prédictions
     */
    public void printPredictions(List<PredictionResult> predictions) {
        System.out.println("\n🔮 ========== PRÉDICTIONS ML ==========");

        int position = 1;
        for (PredictionResult pred : predictions) {
            System.out.printf("\n%d. %s\n", position, pred.getCandidateName());
            System.out.printf("   Probabilité: %.1f%% - %s\n",
                    pred.getProbability(),
                    pred.getPrediction());
            System.out.printf("   Confiance: %s\n", pred.getConfidence());
            System.out.println("   Facteurs clés:");
            for (String factor : pred.getKeyFactors()) {
                System.out.println("      • " + factor);
            }
            position++;
        }

        System.out.println("\n======================================\n");
    }

    /**
     * Classe interne pour stocker les résultats de prédiction
     */
    public static class PredictionResult {
        private Long applicationId;
        private String candidateName;
        private double probability;      // 0-100%
        private String prediction;       // ACCEPTATION PROBABLE, etc.
        private String confidence;       // HAUTE, MOYENNE, FAIBLE
        private List<String> keyFactors; // Facteurs clés

        public PredictionResult(Long applicationId, String candidateName,
                                double probability, String prediction,
                                String confidence, List<String> keyFactors) {
            this.applicationId = applicationId;
            this.candidateName = candidateName;
            this.probability = probability;
            this.prediction = prediction;
            this.confidence = confidence;
            this.keyFactors = keyFactors;
        }

        // Getters
        public Long getApplicationId() { return applicationId; }
        public String getCandidateName() { return candidateName; }
        public double getProbability() { return probability; }
        public String getPrediction() { return prediction; }
        public String getConfidence() { return confidence; }
        public List<String> getKeyFactors() { return keyFactors; }

        @Override
        public String toString() {
            return String.format("%s - %.1f%% - %s (Confiance: %s)",
                    candidateName, probability, prediction, confidence);
        }
    }
}
