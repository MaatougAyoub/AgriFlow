package entities;

/**
 * Représente le score de compatibilité entre un candidat et une demande
 */
public class MatchScore {
    private CollabApplication application;
    private double totalScore;           // Score total (0-100)
    private double experienceScore;      // Score expérience (0-100)
    private double salaryScore;          // Score salaire (0-100)
    private double locationScore;        // Score localisation (0-100)
    private double availabilityScore;    // Score disponibilité (0-100)
    private String recommendation;       // "Excellent", "Bon", "Moyen", "Faible"
    private int stars;                   // Nombre d'étoiles (1-5)

    public MatchScore(CollabApplication application) {
        this.application = application;
    }

    /**
     * Calcule le score total et la recommandation
     */
    public void calculateTotalScore() {
        this.totalScore = (experienceScore + salaryScore + locationScore + availabilityScore) / 4.0;

        // Déterminer la recommandation
        if (totalScore >= 80) {
            this.recommendation = "Excellent candidat";
            this.stars = 5;
        } else if (totalScore >= 60) {
            this.recommendation = "Bon candidat";
            this.stars = 4;
        } else if (totalScore >= 40) {
            this.recommendation = "Candidat moyen";
            this.stars = 3;
        } else if (totalScore >= 20) {
            this.recommendation = "Candidat faible";
            this.stars = 2;
        } else {
            this.recommendation = "Candidat non recommandé";
            this.stars = 1;
        }
    }

    /**
     * Retourne l'affichage des étoiles
     */
    public String getStarsDisplay() {
        return "⭐".repeat(Math.max(0, stars));
    }

    /**
     * Retourne un résumé formaté
     */
    public String getSummary() {
        return String.format("%s - Score: %.1f%% %s - %s",
                application.getFullName(),
                totalScore,
                getStarsDisplay(),
                recommendation);
    }

    /**
     * Retourne les détails des scores
     */
    public String getDetailedScores() {
        return String.format("Exp: %.0f%% | Salaire: %.0f%% | Lieu: %.0f%% | Dispo: %.0f%%",
                experienceScore, salaryScore, locationScore, availabilityScore);
    }

    // ========== GETTERS ET SETTERS ==========

    public CollabApplication getApplication() {
        return application;
    }

    public void setApplication(CollabApplication application) {
        this.application = application;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getExperienceScore() {
        return experienceScore;
    }

    public void setExperienceScore(double experienceScore) {
        this.experienceScore = experienceScore;
    }

    public double getSalaryScore() {
        return salaryScore;
    }

    public void setSalaryScore(double salaryScore) {
        this.salaryScore = salaryScore;
    }

    public double getLocationScore() {
        return locationScore;
    }

    public void setLocationScore(double locationScore) {
        this.locationScore = locationScore;
    }

    public double getAvailabilityScore() {
        return availabilityScore;
    }

    public void setAvailabilityScore(double availabilityScore) {
        this.availabilityScore = availabilityScore;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    @Override
    public String toString() {
        return getSummary();
    }
}
