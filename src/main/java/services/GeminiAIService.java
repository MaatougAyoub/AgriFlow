package services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Service d'Intelligence Artificielle — Google Gemini.
 *
 * Métier avancé : intègre l'IA générative de Google pour enrichir
 * l'expérience utilisateur du Marketplace Agriflow.
 *
 * Fonctionnalités :
 * 1. Amélioration automatique des descriptions d'annonces
 * 2. Suggestion intelligente de prix
 * 3. Modération de contenu par IA (détection fraude/contenu inapproprié)
 *
 * Utilise l'API REST Gemini via java.net.http.HttpClient.
 */
public class GeminiAIService {

    // ── Configuration API ──
    private static final String API_KEY = "AIzaSyCYIKlpxAFhF_uMJu-AFQdu0UQ5uMiRueE";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key="
            + API_KEY;

    private final HttpClient httpClient;

    public GeminiAIService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    // ═══════════════════════════════════════════════════════════════
    // 1. AMÉLIORATION DE DESCRIPTION
    // ═══════════════════════════════════════════════════════════════

    /**
     * Utilise l'IA pour réécrire et améliorer la description d'une annonce.
     * Le prompt est contextualisé au domaine agricole tunisien.
     *
     * @param titre       Le titre de l'annonce
     * @param description La description originale à améliorer
     * @param categorie   La catégorie du produit/équipement
     * @return La description améliorée par l'IA
     * @throws Exception en cas d'erreur réseau ou API
     */
    public String ameliorerDescription(String titre, String description, String categorie) throws Exception {
        String prompt = String.format(
                "Tu es un expert en rédaction d'annonces pour un marketplace agricole en Tunisie (AgriFlow). "
                        + "Réécris et améliore cette description d'annonce pour la rendre plus professionnelle, "
                        + "détaillée et attractive pour les agriculteurs tunisiens. "
                        + "Garde le même sens mais enrichis le texte. "
                        + "Réponds UNIQUEMENT avec la nouvelle description, sans commentaire ni explication.\n\n"
                        + "Titre de l'annonce : %s\n"
                        + "Catégorie : %s\n"
                        + "Description originale : %s",
                titre,
                (categorie != null && !categorie.isEmpty()) ? categorie : "Non spécifiée",
                description);

        return envoyerRequete(prompt);
    }

    // ═══════════════════════════════════════════════════════════════
    // 2. SUGGESTION DE PRIX
    // ═══════════════════════════════════════════════════════════════

    /**
     * Utilise l'IA pour suggérer un prix optimal basé sur les détails de l'annonce.
     *
     * @param titre        Le titre de l'annonce
     * @param description  La description
     * @param categorie    La catégorie
     * @param localisation La localisation (ville/région)
     * @param type         Le type (Location ou Vente)
     * @return Le prix suggéré en DT (Dinar Tunisien)
     * @throws Exception en cas d'erreur réseau ou API
     */
    public double suggererPrix(String titre, String description, String categorie,
            String localisation, String type) throws Exception {
        String prompt = String.format(
                "Tu es un expert du marché agricole en Tunisie. "
                        + "Suggère un prix réaliste en Dinar Tunisien (DT) pour cette annonce. "
                        + "Prends en compte le marché tunisien actuel.\n\n"
                        + "Titre : %s\n"
                        + "Description : %s\n"
                        + "Catégorie : %s\n"
                        + "Localisation : %s\n"
                        + "Type : %s\n\n"
                        + "Réponds UNIQUEMENT avec le nombre (prix en DT), sans texte, sans unité, "
                        + "sans explication. Exemple : 250.00",
                titre,
                (description != null && !description.isEmpty()) ? description : "Non spécifiée",
                (categorie != null && !categorie.isEmpty()) ? categorie : "Non spécifiée",
                (localisation != null && !localisation.isEmpty()) ? localisation : "Non spécifiée",
                (type != null && !type.isEmpty()) ? type : "Location");

        String reponse = envoyerRequete(prompt);

        // Extraire le nombre de la réponse
        String prixStr = reponse.replaceAll("[^0-9.,]", "").replace(",", ".");
        try {
            return Double.parseDouble(prixStr);
        } catch (NumberFormatException e) {
            throw new Exception("L'IA n'a pas pu déterminer un prix. Réponse : " + reponse);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // 3. MODÉRATION DE CONTENU PAR IA
    // ═══════════════════════════════════════════════════════════════

    /**
     * Analyse le contenu d'une annonce avec l'IA pour détecter :
     * - Contenu frauduleux ou suspect
     * - Produits illégaux ou interdits
     * - Langage inapproprié
     * - Incohérences dans l'annonce
     *
     * @param titre       Le titre de l'annonce
     * @param description La description de l'annonce
     * @return null si le contenu est acceptable, sinon le motif de rejet
     * @throws Exception en cas d'erreur réseau ou API
     */
    public String modererContenu(String titre, String description) throws Exception {
        String prompt = String.format(
                "Tu es un modérateur de contenu pour AgriFlow, un marketplace agricole en Tunisie. "
                        + "Analyse cette annonce et détermine si elle est acceptable pour publication.\n\n"
                        + "REJETER UNIQUEMENT si :\n"
                        + "- Le titre ou la description mentionne explicitement des produits illégaux "
                        + "(drogues, armes à feu, contrefaçon)\n"
                        + "- Le contenu est clairement une arnaque ou du spam\n"
                        + "- Le langage est vulgaire ou offensant\n"
                        + "- Le contenu n'a AUCUN rapport avec l'agriculture ou l'équipement rural\n\n"
                        + "ACCEPTER si le produit peut avoir un usage agricole (tracteurs, drones agricoles, "
                        + "outils, semences, engrais, animaux d'élevage, équipements, véhicules utilitaires, etc.)\n\n"
                        + "Titre : %s\n"
                        + "Description : %s\n\n"
                        + "Si l'annonce est ACCEPTABLE, réponds exactement : OK\n"
                        + "Si l'annonce est REJETÉE, réponds avec : REJET: [motif en une phrase courte]",
                titre, description);

        String reponse = envoyerRequete(prompt);

        // Si la réponse contient "OK" (seul ou au début), le contenu est accepté
        if (reponse.trim().equalsIgnoreCase("OK")) {
            return null; // Contenu acceptable
        }
        return reponse.trim(); // Retourne le motif de rejet
    }

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODE INTERNE — Appel API Gemini
    // ═══════════════════════════════════════════════════════════════

    /**
     * Envoie une requête à l'API Google Gemini et retourne la réponse textuelle.
     *
     * @param prompt Le prompt à envoyer
     * @return Le texte de la réponse générée
     * @throws Exception en cas d'erreur
     */
    private String envoyerRequete(String prompt) throws Exception {
        // Construction du body JSON selon le format Gemini API
        JSONObject textPart = new JSONObject();
        textPart.put("text", prompt);

        JSONArray partsArray = new JSONArray();
        partsArray.put(textPart);

        JSONObject content = new JSONObject();
        content.put("parts", partsArray);

        JSONArray contentsArray = new JSONArray();
        contentsArray.put(content);

        JSONObject requestBody = new JSONObject();
        requestBody.put("contents", contentsArray);

        // Envoi de la requête HTTP POST
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_URL))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Vérification du code HTTP avec messages clairs
        if (response.statusCode() != 200) {
            switch (response.statusCode()) {
                case 429:
                    throw new Exception("Quota API dépassé. Veuillez patienter 30 secondes et réessayer.");
                case 401:
                    throw new Exception("Clé API Gemini invalide. Vérifiez votre configuration.");
                case 403:
                    throw new Exception("Accès refusé. Vérifiez les permissions de votre clé API.");
                default:
                    throw new Exception("Erreur API Gemini (HTTP " + response.statusCode() + "). Réessayez plus tard.");
            }
        }

        // Extraction du texte de la réponse JSON
        JSONObject responseJson = new JSONObject(response.body());
        JSONArray candidates = responseJson.getJSONArray("candidates");

        if (candidates.isEmpty()) {
            throw new Exception("Aucune réponse générée par l'IA.");
        }

        return candidates
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
                .trim();
    }
}
