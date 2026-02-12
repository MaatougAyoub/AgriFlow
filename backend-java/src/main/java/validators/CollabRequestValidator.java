package validators;

import entities.CollabRequest;
import java.time.LocalDate;

/**
 * Validateur pour les demandes de collaboration
 * Contrôle de saisie des inputs utilisateur
 */
public class CollabRequestValidator {
    
    /**
     * Valide une demande complète
     * @param request La demande à valider
     * @return true si valide
     * @throws IllegalArgumentException si validation échoue
     */
    public static boolean validate(CollabRequest request) {
        validateTitle(request.getTitle());
        validateDescription(request.getDescription());
        validateDates(request.getStartDate(), request.getEndDate());
        validateNeededPeople(request.getNeededPeople());
        validateRequesterId(request.getRequesterId());
        return true;
    }
    
    /**
     * Valide le titre
     * - Obligatoire
     * - Min 5 caractères
     * - Max 150 caractères
     */
    public static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        String trimmedTitle = title.trim();
        if (trimmedTitle.length() < 5) {
            throw new IllegalArgumentException("Le titre doit contenir au moins 5 caractères");
        }
        if (trimmedTitle.length() > 150) {
            throw new IllegalArgumentException("Le titre ne peut pas dépasser 150 caractères");
        }
    }
    
    /**
     * Valide la description
     * - Optionnelle
     * - Max 500 caractères
     */
    public static void validateDescription(String description) {
        if (description != null && description.trim().length() > 500) {
            throw new IllegalArgumentException("La description ne peut pas dépasser 500 caractères");
        }
    }
    
    /**
     * Valide les dates
     * - Date début obligatoire et dans le futur
     * - Date fin obligatoire et après date début
     */
    public static void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("La date de début est obligatoire");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("La date de fin est obligatoire");
        }
        
        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today) || startDate.isEqual(today)) {
            throw new IllegalArgumentException("La date de début doit être dans le futur");
        }
        
        if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }
    }
    
    /**
     * Valide le nombre de personnes recherchées
     * - Entre 1 et 50
     */
    public static void validateNeededPeople(int neededPeople) {
        if (neededPeople < 1) {
            throw new IllegalArgumentException("Le nombre de personnes doit être au moins 1");
        }
        if (neededPeople > 50) {
            throw new IllegalArgumentException("Le nombre de personnes ne peut pas dépasser 50");
        }
    }
    
    /**
     * Valide l'ID du demandeur
     * - Doit être > 0
     */
    public static void validateRequesterId(long requesterId) {
        if (requesterId <= 0) {
            throw new IllegalArgumentException("L'ID du demandeur est invalide");
        }
    }
}
