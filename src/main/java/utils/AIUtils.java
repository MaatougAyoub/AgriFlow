package utils;

import java.util.Arrays;
import java.util.List;

/**
 * Utilitaires pour les algorithmes d'Intelligence Artificielle
 */
public class AIUtils {

    // ========== MOTS-CLÉS POUR L'ANALYSE DE SENTIMENT ==========

    /**
     * Mots-clés positifs pour l'analyse de motivation
     */
    public static final List<String> POSITIVE_KEYWORDS = Arrays.asList(
            "motivé", "expérience", "sérieux", "compétent", "passion", "expertise",
            "professionnel", "qualifié", "dévoué", "engagé", "enthousiaste",
            "excellente", "solide", "prouvée", "confirmée", "remarquable",
            "agriculture", "récolte", "plantation", "cultiver", "terre"
    );

    /**
     * Mots-clés négatifs (red flags)
     */
    public static final List<String> NEGATIVE_KEYWORDS = Arrays.asList(
            "peut-être", "je ne sais pas", "difficile", "compliqué", "incertain",
            "hésitant", "doute", "jamais fait", "première fois", "inexpérimenté"
    );

    /**
     * Villes tunisiennes principales (pour le calcul de distance)
     */
    public static final List<String> TUNISIAN_CITIES = Arrays.asList(
            "tunis", "ariana", "ben arous", "manouba",
            "nabeul", "zaghouan", "bizerte", "béja",
            "jendouba", "kef", "siliana", "sousse",
            "monastir", "mahdia", "sfax", "kairouan",
            "kasserine", "sidi bouzid", "gabès", "médenine",
            "tataouine", "gafsa", "tozeur", "kébili"
    );

    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Normalise un score entre 0 et 100
     */
    public static double normalizeScore(double value, double min, double max) {
        if (max == min) return 50.0; // Valeur par défaut
        return ((value - min) / (max - min)) * 100.0;
    }

    /**
     * Calcule la moyenne de plusieurs scores
     */
    public static double calculateAverage(double... scores) {
        if (scores.length == 0) return 0.0;
        double sum = 0.0;
        for (double score : scores) {
            sum += score;
        }
        return sum / scores.length;
    }

    /**
     * Compte le nombre de mots-clés présents dans un texte
     */
    public static int countKeywords(String text, List<String> keywords) {
        if (text == null || text.isEmpty()) return 0;

        String lowerText = text.toLowerCase();
        int count = 0;

        for (String keyword : keywords) {
            if (lowerText.contains(keyword.toLowerCase())) {
                count++;
            }
        }

        return count;
    }

    /**
     * Calcule un score de similarité entre deux chaînes (simple)
     */
    public static double calculateSimilarity(String str1, String str2) {
        if (str1 == null || str2 == null) return 0.0;

        str1 = str1.toLowerCase().trim();
        str2 = str2.toLowerCase().trim();

        if (str1.equals(str2)) return 100.0;
        if (str1.contains(str2) || str2.contains(str1)) return 80.0;

        // Comparaison simple basée sur les caractères communs
        int commonChars = 0;
        for (char c : str1.toCharArray()) {
            if (str2.indexOf(c) != -1) {
                commonChars++;
            }
        }

        return (commonChars * 100.0) / Math.max(str1.length(), str2.length());
    }

    /**
     * Formate un score pour l'affichage (avec 1 décimale)
     */
    public static String formatScore(double score) {
        return String.format("%.1f%%", score);
    }

    /**
     * Retourne un emoji en fonction du score
     */
    public static String getScoreEmoji(double score) {
        if (score >= 80) return "🌟";
        if (score >= 60) return "✅";
        if (score >= 40) return "⚠️";
        return "❌";
    }
}