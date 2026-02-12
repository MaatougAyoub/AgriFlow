package com.agriflow.marketplace.services;

import com.agriflow.marketplace.models.Annonce;
import java.util.Arrays;
import java.util.List;

/**
 * Service de contrôle anti-fraude (Métier Avancé — IA simulée).
 *
 * Vérifie la cohérence des annonces avant publication :
 *  - Prix dans une plage réaliste [1 – 100 000 DT]
 *  - Description sans mots-clés frauduleux
 *  - URL image non vide
 *  - Titre non vide et longueur minimale
 *
 * Retourne un message d'erreur détaillé ou null si l'annonce est valide.
 */
public class FraudControlService {

    /** Mots-clés suspects détectés par le moteur de modération. */
    private static final List<String> MOTS_SUSPECTS = Arrays.asList(
            "arnaque", "western union", "gratuit", "cash",
            "virement", "scam", "fake", "urgent transfert",
            "moneygram", "bitcoin", "crypto"
    );

    private static final double PRIX_MIN = 1.0;
    private static final double PRIX_MAX = 100_000.0;
    private static final int TITRE_MIN_LENGTH = 3;

    /**
     * Vérifie si une annonce est conforme aux règles anti-fraude.
     *
     * @param annonce L'annonce à vérifier
     * @return true si l'annonce est valide, false sinon
     */
    public static boolean checkAnnonce(Annonce annonce) {
        return getMotifRejet(annonce) == null;
    }

    /**
     * Retourne le motif de rejet détaillé, ou null si l'annonce est valide.
     * Utile pour afficher un message d'erreur précis à l'utilisateur.
     *
     * @param annonce L'annonce à analyser
     * @return Le motif de rejet, ou null si valide
     */
    public static String getMotifRejet(Annonce annonce) {
        if (annonce == null) {
            return "Annonce invalide (null).";
        }

        // ── Vérification du titre ──
        if (annonce.getTitre() == null || annonce.getTitre().trim().length() < TITRE_MIN_LENGTH) {
            return "Le titre est trop court (minimum " + TITRE_MIN_LENGTH + " caractères).";
        }

        // ── Vérification du prix ──
        if (annonce.getPrix() < PRIX_MIN) {
            return "Prix incohérent : le prix doit être supérieur à " + PRIX_MIN + " DT.";
        }
        if (annonce.getPrix() > PRIX_MAX) {
            return "Prix incohérent : le prix ne peut pas dépasser " + PRIX_MAX + " DT.";
        }

        // ── Vérification de la description (mots-clés frauduleux) ──
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

        // ── Vérification du titre (mots-clés frauduleux) ──
        if (annonce.getTitre() != null) {
            String titreLower = annonce.getTitre().toLowerCase();
            for (String mot : MOTS_SUSPECTS) {
                if (titreLower.contains(mot)) {
                    return "Contenu suspect détecté : mot interdit \"" + mot
                            + "\" trouvé dans le titre.";
                }
            }
        }

        // ── Vérification de l'image ──
        String image = annonce.getImage();
        if (image == null || image.trim().isEmpty()) {
            return "L'URL de l'image est obligatoire pour publier une annonce.";
        }

        // ✅ Annonce conforme
        return null;
    }
}
