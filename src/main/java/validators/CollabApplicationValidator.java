package validators;

import entities.CollabApplication;

/**
 * Validateur pour les candidatures aux demandes de collaboration
 * Contrôle de saisie des inputs utilisateur
 */
public class CollabApplicationValidator {
    
    /**
     * Valide une candidature complète
     * @param application La candidature à valider
     * @return true si valide
     * @throws IllegalArgumentException si validation échoue
     */
    public static boolean validate(CollabApplication application) {
        validateRequestId(application.getRequestId());
        validateCandidateId(application.getCandidateId());
        validateMessage(application.getMotivation());
        return true;
    }
    
    /**
     * Valide l'ID de la demande
     * - Doit être > 0
     */
    public static void validateRequestId(long requestId) {
        if (requestId <= 0) {
            throw new IllegalArgumentException("L'ID de la demande est invalide");
        }
    }
    
    /**
     * Valide l'ID du candidat
     * - Doit être > 0
     */
    public static void validateCandidateId(long candidateId) {
        if (candidateId <= 0) {
            throw new IllegalArgumentException("L'ID du candidat est invalide");
        }
    }
    
    /**
     * Valide le message de candidature
     * - Optionnel
     * - Si présent : min 10 caractères, max 255 caractères
     */
    public static void validateMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            String trimmedMessage = message.trim();
            if (trimmedMessage.length() < 10) {
                throw new IllegalArgumentException("Le message doit contenir au moins 10 caractères");
            }
            if (trimmedMessage.length() > 255) {
                throw new IllegalArgumentException("Le message ne peut pas dépasser 255 caractères");
            }
        }
    }
}
