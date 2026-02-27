package services;

import entities.CollabApplication;
import entities.CollabRequest;
import entities.RecommendationResult;
import utils.AIUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 🤖 Service d'IA pour la Recommandation de demandes
 * Recommande les meilleures demandes pour un candidat
 */
public class RecommendationService {

    private CollabRequestService requestService;

    public RecommendationService() {
        this.requestService = new CollabRequestService();
    }

    /**
     * Recommande les meilleures demandes pour un candidat
     * Basé sur son expérience, salaire souhaité, et localisation
     */
    public List<RecommendationResult> recommendRequests(int candidateExperience,
                                                        double desiredSalary,
                                                        String preferredLocation) throws SQLException {

        System.out.println("\n🤖 IA - Recherche des meilleures demandes pour le candidat...");
        System.out.println("   Expérience: " + candidateExperience + " ans");
        System.out.println("   Salaire souhaité: " + desiredSalary + " DT/jour");
        System.out.println("   Lieu préféré: " + preferredLocation);

        // Récupérer toutes les demandes approuvées
        List<CollabRequest> allRequests = requestService.findAll().stream()
                .filter(r -> "APPROVED".equals(r.getStatus()))
                .collect(Collectors.toList());

        List<RecommendationResult> recommendations = new ArrayList<>();

        for (CollabRequest request : allRequests) {
            double matchScore = calculateRecommendationScore(
                    candidateExperience,
                    desiredSalary,
                    preferredLocation,
                    request
            );

            String reason = generateRecommendationReason(
                    candidateExperience,
                    desiredSalary,
                    preferredLocation,
                    request,
                    matchScore
            );

            RecommendationResult result = new RecommendationResult(request, matchScore, reason);
            recommendations.add(result);
        }

        // Trier par score décroissant
        List<RecommendationResult> sortedRecommendations = recommendations.stream()
                .sorted(Comparator.comparingDouble(RecommendationResult::getMatchScore).reversed())
                .collect(Collectors.toList());

        System.out.println("✅ Trouvé " + sortedRecommendations.size() + " recommandation(s)");

        return sortedRecommendations;
    }

    /**
     * Calcule le score de compatibilité entre un candidat et une demande
     */
    private double calculateRecommendationScore(int candidateExperience,
                                                double desiredSalary,
                                                String preferredLocation,
                                                CollabRequest request) {

        // 1️⃣ Score d'expérience (poids: 30%)
        double experienceScore = calculateExperienceMatch(candidateExperience) * 0.30;

        // 2️⃣ Score de salaire (poids: 40%)
        double salaryScore = calculateSalaryMatch(desiredSalary, request.getSalaryPerDay()) * 0.40;

        // 3️⃣ Score de localisation (poids: 30%)
        double locationScore = calculateLocationMatch(preferredLocation, request.getLocation()) * 0.30;

        double totalScore = experienceScore + salaryScore + locationScore;

        return Math.min(100.0, totalScore); // Limiter à 100
    }

    /**
     * Calcule le score d'expérience
     */
    private double calculateExperienceMatch(int candidateExperience) {
        if (candidateExperience >= 5) {
            return 100.0; // Expert
        } else if (candidateExperience >= 3) {
            return 80.0; // Expérimenté
        } else if (candidateExperience >= 1) {
            return 60.0; // Débutant avec expérience
        } else {
            return 40.0; // Débutant
        }
    }

    /**
     * Calcule le score de salaire
     */
    private double calculateSalaryMatch(double desiredSalary, double offeredSalary) {
        if (offeredSalary >= desiredSalary) {
            return 100.0; // Salaire offert ≥ souhaité (parfait!)
        }

        double difference = ((desiredSalary - offeredSalary) / desiredSalary) * 100;

        if (difference <= 10) {
            return 85.0;
        } else if (difference <= 20) {
            return 65.0;
        } else if (difference <= 30) {
            return 45.0;
        } else {
            return 20.0;
        }
    }

    /**
     * Calcule le score de localisation
     */
    private double calculateLocationMatch(String preferredLocation, String requestLocation) {
        if (preferredLocation == null || requestLocation == null) {
            return 50.0;
        }

        return AIUtils.calculateSimilarity(preferredLocation, requestLocation);
    }

    /**
     * Génère une raison pour la recommandation
     */
    private String generateRecommendationReason(int candidateExperience,
                                                double desiredSalary,
                                                String preferredLocation,
                                                CollabRequest request,
                                                double matchScore) {
        List<String> reasons = new ArrayList<>();

        // Analyse du salaire
        if (request.getSalaryPerDay() >= desiredSalary) {
            reasons.add("💰 Salaire proposé (" + request.getSalaryPerDay() + " DT) correspond à vos attentes");
        } else {
            double diff = desiredSalary - request.getSalaryPerDay();
            reasons.add("⚠️ Salaire proposé (" + request.getSalaryPerDay() + " DT) inférieur de " + String.format("%.1f", diff) + " DT à vos attentes");
        }

        // Analyse de la localisation
        if (preferredLocation != null && request.getLocation() != null) {
            double locationMatch = AIUtils.calculateSimilarity(preferredLocation, request.getLocation());
            if (locationMatch >= 80) {
                reasons.add("📍 Localisation très proche de vos préférences (" + request.getLocation() + ")");
            } else if (locationMatch >= 50) {
                reasons.add("📍 Localisation acceptable (" + request.getLocation() + ")");
            }
        }

        // Analyse de l'expérience
        if (candidateExperience >= 5) {
            reasons.add("⭐ Votre expérience (" + candidateExperience + " ans) est excellente pour cette demande");
        } else if (candidateExperience >= 3) {
            reasons.add("✅ Votre expérience (" + candidateExperience + " ans) convient bien pour cette demande");
        }

        // Nombre de postes disponibles
        if (request.getNeededPeople() > 1) {
            reasons.add("👥 " + request.getNeededPeople() + " postes disponibles (bonnes chances)");
        }

        return String.join(" | ", reasons);
    }

    /**
     * Affiche les recommandations dans la console
     */
    public void printRecommendations(List<RecommendationResult> recommendations) {
        System.out.println("\n🎯 ========== RECOMMANDATIONS IA ==========");

        int position = 1;
        for (RecommendationResult rec : recommendations) {
            System.out.printf("\n%d. %s\n", position, rec.getSummary());
            System.out.printf("   💡 %s\n", rec.getReason());
            position++;
        }

        System.out.println("\n==========================================\n");
    }

    /**
     * Filtre les recommandations par score minimum
     */
    public List<RecommendationResult> filterByMinScore(List<RecommendationResult> recommendations, double minScore) {
        return recommendations.stream()
                .filter(rec -> rec.getMatchScore() >= minScore)
                .collect(Collectors.toList());
    }

    /**
     * Retourne les "Top N" meilleures recommandations
     */
    public List<RecommendationResult> getTopRecommendations(List<RecommendationResult> recommendations, int topN) {
        return recommendations.stream()
                .limit(topN)
                .collect(Collectors.toList());
    }
}