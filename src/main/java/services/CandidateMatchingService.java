package services;

import entities.CollabApplication;
import entities.CollabRequest;
import entities.MatchScore;
import utils.AIUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 🤖 Service d'IA pour le Matching automatique Candidat ↔ Demande
 * Analyse la compatibilité entre un candidat et une demande
 */
public class CandidateMatchingService {

    /**
     * Calcule le score de compatibilité pour une candidature
     */
    public MatchScore calculateMatchScore(CollabApplication application, CollabRequest request) {
        MatchScore score = new MatchScore(application);

        // 1️⃣ Score d'expérience (0-100)
        score.setExperienceScore(calculateExperienceScore(application.getYearsOfExperience()));

        // 2️⃣ Score de salaire (0-100)
        score.setSalaryScore(calculateSalaryScore(application.getExpectedSalary(), request.getSalaryPerDay()));

        // 3️⃣ Score de localisation (0-100)
        score.setLocationScore(calculateLocationScore(request.getLocation()));

        // 4️⃣ Score de disponibilité (0-100)
        score.setAvailabilityScore(calculateAvailabilityScore());

        // Calculer le score total
        score.calculateTotalScore();

        System.out.println("✅ Score calculé pour " + application.getFullName() + ": " + score.getTotalScore() + "%");

        return score;
    }

    /**
     * Calcule le score basé sur l'expérience
     * Plus le candidat a d'expérience, meilleur est le score
     */
    private double calculateExperienceScore(int yearsOfExperience) {
        if (yearsOfExperience >= 10) {
            return 100.0; // Expert (10+ ans)
        } else if (yearsOfExperience >= 7) {
            return 95.0; // Très expérimenté (7-9 ans)
        } else if (yearsOfExperience >= 5) {
            return 90.0; // Expérimenté (5-6 ans)
        } else if (yearsOfExperience >= 3) {
            return 75.0; // Bonne expérience (3-4 ans)
        } else if (yearsOfExperience >= 1) {
            return 55.0; // Expérience moyenne (1-2 ans)
        } else {
            return 30.0; // Débutant (<1 an)
        }
    }

    /**
     * Calcule le score basé sur le salaire
     * Compare le salaire demandé avec le salaire proposé
     */
    private double calculateSalaryScore(double expectedSalary, double offeredSalary) {
        if (expectedSalary <= offeredSalary) {
            return 100.0; // Salaire demandé ≤ proposé (parfait!)
        }

        // Calcul de la différence en pourcentage
        double difference = ((expectedSalary - offeredSalary) / offeredSalary) * 100;

        if (difference <= 5) {
            return 95.0; // Différence très faible (≤5%)
        } else if (difference <= 10) {
            return 85.0; // Différence faible (≤10%)
        } else if (difference <= 20) {
            return 65.0; // Différence moyenne (≤20%)
        } else if (difference <= 30) {
            return 45.0; // Différence importante (≤30%)
        } else if (difference <= 50) {
            return 25.0; // Différence très importante (≤50%)
        } else {
            return 10.0; // Différence excessive (>50%)
        }
    }

    /**
     * Calcule le score basé sur la localisation
     * Vérifie si le lieu est une ville connue de Tunisie
     */
    private double calculateLocationScore(String location) {
        if (location == null || location.isEmpty()) {
            return 50.0; // Score neutre si pas de localisation
        }

        String lowerLocation = location.toLowerCase().trim();

        // Vérifie si c'est une ville tunisienne connue
        for (String city : AIUtils.TUNISIAN_CITIES) {
            if (lowerLocation.contains(city)) {
                return 85.0; // Ville connue
            }
        }

        return 70.0; // Localisation non reconnue mais acceptée
    }

    /**
     * Calcule le score de disponibilité
     * Pour l'instant, score fixe (peut être amélioré avec gestion des dates)
     */
    private double calculateAvailabilityScore() {
        // Le candidat a postulé, donc il est disponible
        return 100.0;
    }

    /**
     * Classe toutes les candidatures par score décroissant
     */
    public List<MatchScore> rankApplications(List<CollabApplication> applications, CollabRequest request) {
        List<MatchScore> scores = new ArrayList<>();

        System.out.println("\n🤖 IA - Analyse de " + applications.size() + " candidature(s)...");

        for (CollabApplication app : applications) {
            MatchScore score = calculateMatchScore(app, request);
            scores.add(score);
        }

        // Trier par score décroissant (meilleur candidat en premier)
        List<MatchScore> rankedScores = scores.stream()
                .sorted(Comparator.comparingDouble(MatchScore::getTotalScore).reversed())
                .collect(Collectors.toList());

        System.out.println("✅ Classement terminé !");

        return rankedScores;
    }

    /**
     * Affiche le classement dans la console (pour debug)
     */
    public void printRanking(List<MatchScore> ranking) {
        System.out.println("\n🎯 ========== CLASSEMENT IA DES CANDIDATS ==========");

        int position = 1;
        for (MatchScore score : ranking) {
            System.out.printf("\n%d. %s\n", position, score.getSummary());
            System.out.printf("   📊 %s\n", score.getDetailedScores());
            position++;
        }

        System.out.println("\n===================================================\n");
    }

    /**
     * Retourne le meilleur candidat (score le plus élevé)
     */
    public MatchScore getBestCandidate(List<CollabApplication> applications, CollabRequest request) {
        List<MatchScore> ranking = rankApplications(applications, request);

        if (ranking.isEmpty()) {
            return null;
        }

        MatchScore best = ranking.get(0);
        System.out.println("🏆 Meilleur candidat: " + best.getSummary());

        return best;
    }

    /**
     * Filtre les candidats avec un score minimum
     */
    public List<MatchScore> filterByMinScore(List<MatchScore> scores, double minScore) {
        return scores.stream()
                .filter(score -> score.getTotalScore() >= minScore)
                .collect(Collectors.toList());
    }

    /**
     * Retourne les "Top N" meilleurs candidats
     */
    public List<MatchScore> getTopCandidates(List<MatchScore> ranking, int topN) {
        return ranking.stream()
                .limit(topN)
                .collect(Collectors.toList());
    }
}