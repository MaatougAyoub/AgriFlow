package services;

import entities.Annonce;
import java.util.Arrays;
import java.util.List;

// Service Anti-Fraude - nchoufou ken l annonce fih mochkla 9bal ma tetzed
// hedhi regles statiques (mech IA), tkhdem AVANT el moderation Gemini
public class FraudControlService {

    // liste mta3 les mots machbouha (ken la9ina wa7da mel hethom -> rejet)
    private static final List<String> MOTS_SUSPECTS = Arrays.asList(
            "arnaque", "western union", "gratuit", "cash",
            "virement", "scam", "fake", "urgent transfert",
            "moneygram", "bitcoin", "crypto"
    );

    // el prix lezem ykoun bin 1 DT w 100000 DT (sinon louche)
    private static final double PRIX_MIN = 1.0;
    private static final double PRIX_MAX = 100_000.0;
    // el titre lezem 3la el a9al 3 caracteres
    private static final int TITRE_MIN_LENGTH = 3;


    // retourne true ken l annonce s7i7a, false ken fih mochkla
    public static boolean checkAnnonce(Annonce annonce) {
        return getMotifRejet(annonce) == null;
    }

    // ===== EL METHODE EL PRINCIPALE =====
    // nchoufou kol haja wa7da wa7da : titre, prix, mots suspects, image
    // ken la9ina mochkla -> retourne le motif (string)
    // ken kol chay behi -> retourne null (ya3ni OK)
    public static String getMotifRejet(Annonce annonce) {
        if (annonce == null) {
            return "Annonce invalide (null).";
        }

        // 1. nchoufou el titre (lazem >= 3 caracteres)
        if (annonce.getTitre() == null || annonce.getTitre().trim().length() < TITRE_MIN_LENGTH) {
            return "Le titre est trop court (minimum " + TITRE_MIN_LENGTH + " caractères).";
        }

        // 2. nchoufou el prix (lazem bin 1 DT w 100000 DT)
        if (annonce.getPrix() < PRIX_MIN) {
            return "Prix incohérent : le prix doit être supérieur à " + PRIX_MIN + " DT.";
        }
        if (annonce.getPrix() > PRIX_MAX) {
            return "Prix incohérent : le prix ne peut pas dépasser " + PRIX_MAX + " DT.";
        }

        // 3. nchoufou el description (ken fih kelma machbouha -> rejet)
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

        // 4. nchoufou el titre zeda (mots machbouha)
        if (annonce.getTitre() != null) {
            String titreLower = annonce.getTitre().toLowerCase();
            for (String mot : MOTS_SUSPECTS) {
                if (titreLower.contains(mot)) {
                    return "Contenu suspect détecté : mot interdit \"" + mot
                            + "\" trouvé dans le titre.";
                }
            }
        }

        // 5. lazem tkoun fih taswira
        String image = annonce.getImage();
        if (image == null || image.trim().isEmpty()) {
            return "L'URL de l'image est obligatoire pour publier une annonce.";
        }

        // kol chay behi, annonce valide -> retourne null (OK)
        return null;
    }
}