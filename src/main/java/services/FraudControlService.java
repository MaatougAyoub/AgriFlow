package services;

import entities.Annonce;
import java.util.Arrays;
import java.util.List;

// Service anti-fraude : vérifie la cohérence des annonces avant publication
public class FraudControlService {

    // Mots-clés suspects
    private static final List<String> MOTS_SUSPECTS = Arrays.asList(
            "arnaque", "western union", "gratuit", "cash",
            "virement", "scam", "fake", "urgent transfert",
            "moneygram", "bitcoin", "crypto"
    );

    private static final double PRIX_MIN = 1.0;
    private static final double PRIX_MAX = 100_000.0;
    private static final int TITRE_MIN_LENGTH = 3;


    public static boolean checkAnnonce(Annonce annonce) {
        return getMotifRejet(annonce) == null;
    }

    // Retourne le motif de rejet ou null si OK
    public static String getMotifRejet(Annonce annonce) {
        if (annonce == null) {
            return "Annonce invalide (null).";
        }

        // Titre
        if (annonce.getTitre() == null || annonce.getTitre().trim().length() < TITRE_MIN_LENGTH) {
            return "Le titre est trop court (minimum " + TITRE_MIN_LENGTH + " caractères).";
        }

        // Prix
        if (annonce.getPrix() < PRIX_MIN) {
            return "Prix incohérent : le prix doit être supérieur à " + PRIX_MIN + " DT.";
        }
        if (annonce.getPrix() > PRIX_MAX) {
            return "Prix incohérent : le prix ne peut pas dépasser " + PRIX_MAX + " DT.";
        }

        // Description (mots-clés frauduleux)
        if (annonce.getDescription() != null) {
            String descLower = annonce.getDescription().toLowerCase();
            for (String mot : MOTS_SUSPECTS) {
                if (descLower.contains(mot)) {
                    return "Contenu suspect détecté : mot interdit \"" + mot
                            + "\" trouvé dans la description. "
                            + "Veuillez reformuler votre annonce.";
                }
            }
        }

        // Titre (mots-clés frauduleux)
        if (annonce.getTitre() != null) {
            String titreLower = annonce.getTitre().toLowerCase();
            for (String mot : MOTS_SUSPECTS) {
                if (titreLower.contains(mot)) {
                    return "Contenu suspect détecté : mot interdit \"" + mot
                            + "\" trouvé dans le titre.";
                }
            }
        }

        // Image
        String image = annonce.getImage();
        if (image == null || image.trim().isEmpty()) {
            return "L'URL de l'image est obligatoire pour publier une annonce.";
        }

        // Annonce conforme
        return null;
    }
}