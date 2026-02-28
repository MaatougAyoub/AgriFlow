package services;

import entities.CollabApplication;
import utils.AIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 🤖 Service d'IA pour l'Analyse de Sentiment (NLP)
 * Analyse la qualité de la motivation du candidat
 */
public class SentimentAnalysisService {

    /**
     * Analyse la motivation d'un candidat et retourne un score de qualité
     */
    public SentimentResult analyzeSentiment(CollabApplication application) {
        String motivation = application.getMotivation();

        if (motivation == null || motivation.trim().isEmpty()) {
            return new SentimentResult(0.0, "Aucune motivation fournie", "NEGATIVE");
        }

        System.out.println("\n🤖 IA - Analyse de sentiment pour: " + application.getFullName());

        // 1️⃣ Compter les mots-clés positifs
        int positiveCount = AIUtils.countKeywords(motivation, AIUtils.POSITIVE_KEYWORDS);

        // 2️⃣ Compter les mots-clés négatifs (red flags)
        int negativeCount = AIUtils.countKeywords(motivation, AIUtils.NEGATIVE_KEYWORDS);

        // 3️⃣ Analyser la longueur du texte
        int wordCount = motivation.split("\\s+").length;
        double lengthScore = calculateLengthScore(wordCount);

        // 4️⃣ Calculer le score final
        double sentimentScore = calculateSentimentScore(positiveCount, negativeCount, lengthScore);

        // 5️⃣ Déterminer le sentiment général
        String sentiment = determineSentiment(sentimentScore);

        // 6️⃣ Générer une analyse détaillée
        String analysis = generateAnalysis(positiveCount, negativeCount, wordCount, sentimentScore);

        System.out.println("   ✅ Score de sentiment: " + sentimentScore + "% - " + sentiment);

        return new SentimentResult(sentimentScore, analysis, sentiment);
    }

    /**
     * Calcule le score basé sur la longueur du texte
     */
    private double calculateLengthScore(int wordCount) {
        if (wordCount >= 50) {
            return 100.0; // Motivation très détaillée
        } else if (wordCount >= 30) {
            return 80.0; // Motivation détaillée
        } else if (wordCount >= 15) {
            return 60.0; // Motivation moyenne
        } else if (wordCount >= 5) {
            return 40.0; // Motivation courte
        } else {
            return 20.0; // Motivation très courte
        }
    }

    /**
     * Calcule le score de sentiment final
     */
    private double calculateSentimentScore(int positiveCount, int negativeCount, double lengthScore) {
        // Score des mots-clés positifs (max 40 points)
        double positiveScore = Math.min(40.0, positiveCount * 10.0);

        // Pénalité pour mots-clés négatifs (max -30 points)
        double negativePenalty = Math.min(30.0, negativeCount * 10.0);

        // Score de longueur (poids 30%)
        double lengthWeight = lengthScore * 0.30;

        // Bonus si pas de mots négatifs et beaucoup de positifs
        double bonus = 0.0;
        if (negativeCount == 0 && positiveCount >= 3) {
            bonus = 10.0;
        }

        double finalScore = positiveScore + lengthWeight - negativePenalty + bonus;

        // Limiter entre 0 et 100
        return Math.max(0.0, Math.min(100.0, finalScore));
    }

    /**
     * Détermine le sentiment général
     */
    private String determineSentiment(double score) {
        if (score >= 75) {
            return "TRÈS POSITIF";
        } else if (score >= 55) {
            return "POSITIF";
        } else if (score >= 35) {
            return "NEUTRE";
        } else if (score >= 15) {
            return "NÉGATIF";
        } else {
            return "TRÈS NÉGATIF";
        }
    }

    /**
     * Génère une analyse détaillée
     */
    private String generateAnalysis(int positiveCount, int negativeCount, int wordCount, double score) {
        List<String> observations = new ArrayList<>();

        // Analyse des mots-clés positifs
        if (positiveCount >= 5) {
            observations.add("✅ Nombreux termes positifs détectés (" + positiveCount + ")");
        } else if (positiveCount >= 3) {
            observations.add("✅ Plusieurs termes positifs (" + positiveCount + ")");
        } else if (positiveCount >= 1) {
            observations.add("⚠️ Peu de termes positifs (" + positiveCount + ")");
        } else {
            observations.add("❌ Aucun terme positif détecté");
        }

        // Analyse des mots-clés négatifs (red flags)
        if (negativeCount > 0) {
            observations.add("⚠️ " + negativeCount + " indicateur(s) négatif(s) détecté(s)");
        } else {
            observations.add("✅ Aucun indicateur négatif");
        }

        // Analyse de la longueur
        if (wordCount >= 50) {
            observations.add("✅ Motivation très détaillée (" + wordCount + " mots)");
        } else if (wordCount >= 30) {
            observations.add("✅ Motivation bien développée (" + wordCount + " mots)");
        } else if (wordCount >= 15) {
            observations.add("⚠️ Motivation moyenne (" + wordCount + " mots)");
        } else {
            observations.add("❌ Motivation trop courte (" + wordCount + " mots)");
        }

        // Recommandation finale
        if (score >= 75) {
            observations.add("🌟 Excellente candidature recommandée");
        } else if (score >= 55) {
            observations.add("👍 Bonne candidature");
        } else if (score >= 35) {
            observations.add("🤔 Candidature acceptable");
        } else {
            observations.add("⚠️ Candidature à examiner avec attention");
        }

        return String.join(" | ", observations);
    }

    /**
     * Analyse un lot de candidatures et les classe par qualité
     */
    public List<SentimentResult> analyzeMultipleCandidates(List<CollabApplication> applications) {
        List<SentimentResult> results = new ArrayList<>();

        System.out.println("\n🤖 IA - Analyse de sentiment de " + applications.size() + " candidature(s)...");

        for (CollabApplication app : applications) {
            SentimentResult result = analyzeSentiment(app);
            result.setApplicationId(app.getId());
            result.setCandidateName(app.getFullName());
            results.add(result);
        }

        // Trier par score décroissant
        results.sort((r1, r2) -> Double.compare(r2.getScore(), r1.getScore()));

        System.out.println("✅ Analyse terminée !");

        return results;
    }

    /**
     * Affiche les résultats d'analyse
     */
    public void printAnalysisResults(List<SentimentResult> results) {
        System.out.println("\n📊 ========== ANALYSE DE SENTIMENT (NLP) ==========");

        int position = 1;
        for (SentimentResult result : results) {
            System.out.printf("\n%d. %s\n", position, result.getCandidateName());
            System.out.printf("   Score: %.1f%% - %s %s\n",
                    result.getScore(),
                    result.getSentiment(),
                    getEmoji(result.getSentiment()));
            System.out.printf("   💡 %s\n", result.getAnalysis());
            position++;
        }

        System.out.println("\n===================================================\n");
    }

    /**
     * Retourne un emoji en fonction du sentiment
     */
    private String getEmoji(String sentiment) {
        switch (sentiment) {
            case "TRÈS POSITIF": return "🌟";
            case "POSITIF": return "😊";
            case "NEUTRE": return "😐";
            case "NÉGATIF": return "😕";
            case "TRÈS NÉGATIF": return "❌";
            default: return "";
        }
    }

    /**
     * Classe interne pour stocker les résultats d'analyse
     */
    public static class SentimentResult {
        private Long applicationId;
        private String candidateName;
        private double score;
        private String analysis;
        private String sentiment;

        public SentimentResult(double score, String analysis, String sentiment) {
            this.score = score;
            this.analysis = analysis;
            this.sentiment = sentiment;
        }

        // Getters et Setters
        public Long getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(Long applicationId) {
            this.applicationId = applicationId;
        }

        public String getCandidateName() {
            return candidateName;
        }

        public void setCandidateName(String candidateName) {
            this.candidateName = candidateName;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getAnalysis() {
            return analysis;
        }

        public void setAnalysis(String analysis) {
            this.analysis = analysis;
        }

        public String getSentiment() {
            return sentiment;
        }

        public void setSentiment(String sentiment) {
            this.sentiment = sentiment;
        }

        @Override
        public String toString() {
            return String.format("Score: %.1f%% - %s\n%s", score, sentiment, analysis);
        }
    }
}