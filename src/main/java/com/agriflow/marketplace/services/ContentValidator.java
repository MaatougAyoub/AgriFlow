package com.agriflow.marketplace.services;

import com.agriflow.marketplace.models.Annonce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Validateur de contenu "IA Guard".
 * Simule une validation intelligente des annonces avant publication.
 *
 * Fonctionnalités :
 * - Détection de mots interdits (anti-fraude)
 * - Validation des URLs d'images
 * - Validation de la fourchette de prix
 * - Vérification des champs obligatoires
 * - Cohérence des dates
 * - Score de confiance global
 */
public class ContentValidator {

    private static final double PRIX_MIN = 1.0;
    private static final double PRIX_MAX = 50000.0;

    private static final List<String> MOTS_INTERDITS = Arrays.asList(
            "arnaque", "faux", "contrefacon", "illegal",
            "drogue", "arme", "fausse", "escroquerie",
            "volé", "vol", "pirate", "hack",
            "gratuit", "cadeau", "argent");

    private static final List<String> EXTENSIONS_IMAGE_VALIDES = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp");

    /**
     * Valide une annonce et retourne la liste des erreurs détectées.
     */
    public List<String> validerAnnonce(Annonce a) {
        List<String> erreurs = new ArrayList<>();

        if (a == null) {
            erreurs.add("Annonce nulle");
            return erreurs;
        }

        // 1. Champs obligatoires
        if (a.getTitre() == null || a.getTitre().trim().isEmpty()) {
            erreurs.add("Le titre est obligatoire");
        }
        if (a.getDescription() == null || a.getDescription().trim().isEmpty()) {
            erreurs.add("La description est obligatoire");
        }
        if (a.getType() == null) {
            erreurs.add("Le type d'annonce est obligatoire");
        }
        if (a.getProprietaire() == null) {
            erreurs.add("Le propriétaire est obligatoire");
        }

        // 2. Détection de mots interdits
        String contenuComplet = "";
        if (a.getTitre() != null)
            contenuComplet += a.getTitre().toLowerCase() + " ";
        if (a.getDescription() != null)
            contenuComplet += a.getDescription().toLowerCase();

        for (String mot : MOTS_INTERDITS) {
            if (contenuComplet.contains(mot)) {
                erreurs.add("Mot interdit détecté : \"" + mot + "\"");
            }
        }

        // 3. Validation des images (seulement si des photos existent)
        List<String> photos = a.getPhotos();
        if (photos != null && !photos.isEmpty()) {
            for (String url : photos) {
                if (url != null && !url.trim().isEmpty()) {
                    boolean extensionValide = EXTENSIONS_IMAGE_VALIDES.stream()
                            .anyMatch(ext -> url.toLowerCase().endsWith(ext));
                    if (!extensionValide) {
                        erreurs.add("Format d'image non supporté : " + url);
                    }
                }
            }
        }

        // 4. Validation du prix
        if (a.getPrix() < PRIX_MIN) {
            erreurs.add("Le prix minimum est " + PRIX_MIN + " DT");
        }
        if (a.getPrix() > PRIX_MAX) {
            erreurs.add("Le prix maximum est " + PRIX_MAX + " DT");
        }

        // 5. Cohérence des dates
        if (a.getDateDebutDisponibilite() != null && a.getDateFinDisponibilite() != null) {
            if (a.getDateDebutDisponibilite().isAfter(a.getDateFinDisponibilite())) {
                erreurs.add("La date de début doit être avant la date de fin");
            }
        }

        return erreurs;
    }

    /**
     * Calcule un score de confiance (0-100) pour une annonce.
     * Plus le score est élevé, plus l'annonce est fiable.
     */
    public int calculerScoreConfiance(Annonce a) {
        if (a == null)
            return 0;

        int score = 100;
        List<String> erreurs = validerAnnonce(a);

        // -20 par erreur critique
        score -= erreurs.size() * 20;

        // Bonus si photo présente
        if (a.getPhotos() != null && !a.getPhotos().isEmpty()) {
            score += 5;
        }

        // Bonus si description détaillée (> 50 caractères)
        if (a.getDescription() != null && a.getDescription().length() > 50) {
            score += 5;
        }

        // Bonus si localisation précise
        if (a.getLocalisation() != null && !a.getLocalisation().trim().isEmpty()) {
            score += 5;
        }

        // Clamper entre 0 et 100
        return Math.max(0, Math.min(100, score));
    }

    // TODO: Futur — Intégration Google Vision API pour analyse d'images
    // TODO: Futur — Intégration OpenAI API pour analyse sémantique
}
