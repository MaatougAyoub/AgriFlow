package entities;

/**
 * Représente une recommandation de demande pour un candidat
 */
public class RecommendationResult {
    private CollabRequest request;
    private double matchScore;           // Score de compatibilité (0-100)
    private String reason;               // Raison de la recommandation
    private int priority;                // Priorité (1 = haute, 2 = moyenne, 3 = basse)

    public RecommendationResult(CollabRequest request, double matchScore, String reason) {
        this.request = request;
        this.matchScore = matchScore;
        this.reason = reason;

        // Définir la priorité automatiquement
        if (matchScore >= 75) {
            this.priority = 1; // Haute priorité
        } else if (matchScore >= 50) {
            this.priority = 2; // Moyenne priorité
        } else {
            this.priority = 3; // Basse priorité
        }
    }

    /**
     * Retourne le label de priorité avec emoji
     */
    public String getPriorityLabel() {
        switch (priority) {
            case 1: return "🔥 Haute priorité";
            case 2: return "⚡ Moyenne priorité";
            case 3: return "📌 Basse priorité";
            default: return "";
        }
    }

    /**
     * Retourne un résumé formaté
     */
    public String getSummary() {
        return String.format("%s - Score: %.1f%% - %s",
                request.getTitle(),
                matchScore,
                getPriorityLabel());
    }

    // ========== GETTERS ET SETTERS ==========

    public CollabRequest getRequest() {
        return request;
    }

    public void setRequest(CollabRequest request) {
        this.request = request;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return String.format("%s\nRaison: %s", getSummary(), reason);
    }
}