package services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

// Service IA - nesta3mlou Google Gemini API bech n7asnou les annonces w ncontroliw el contenu
// ya3ni neb3thou prompt (texte) l Google, w howa yredlna reponse
public class GeminiAIService {

    // el cle API mta3 Gemini (men Google Cloud Console)
    private static final String API_KEY = "AIzaSyDkwXGRdu7eyhHSk_T_C3bWEkWI5Jtk67k";
    // el URL mta3 l'API (endpoint REST)
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key="
            + API_KEY;

    // HttpClient mta3 Java 11+ bech nab3thou requetes HTTP
    private final HttpClient httpClient;

    public GeminiAIService() {
        // n7addrou el HttpClient b timeout 15 secondes
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }



    // ===== 1. AMELIORER DESCRIPTION =====
    // neb3thou el titre + description l Gemini, w howa yredha 7sanner w plus professionnelle
    public String ameliorerDescription(String titre, String description, String categorie) throws Exception {
        // el prompt = el texte elli nab3thousou l l'IA (kifech na7ki m3aha)
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



    // ===== 2. SUGGERER PRIX =====
    // nab3thou les details mta3 l annonce l Gemini, w howa y9ollna 9addech el soum el mouneseb
    public double suggererPrix(String titre, String description, String categorie,
            String localisation, String type) throws Exception {
        String prompt = String.format(
                "Tu es un expert du marché agricole en Tunisie. "
                        + "Suggère un prix réaliste en Dinar Tunisien (DT) pour cette annonce sur un marketplace agricole.\n\n"
                        + "IMPORTANT :\n"
                        + "- Si c'est une LOCATION, donne le prix par JOUR de location (pas le prix d'achat de l'équipement)\n"
                        + "- Si c'est une VENTE, donne le prix unitaire de vente\n"
                        + "- Prends en compte le marché tunisien actuel\n"
                        + "- Un prix de location journalier est généralement entre 50 et 1000 DT\n\n"
                        + "Titre : %s\n"
                        + "Description : %s\n"
                        + "Catégorie : %s\n"
                        + "Localisation : %s\n"
                        + "Type : %s\n\n"
                        + "Réponds UNIQUEMENT avec le nombre (prix en DT), sans texte, sans unité, "
                        + "sans explication, sans virgule de milliers. Exemple : 250.00",
                titre,
                (description != null && !description.isEmpty()) ? description : "Non spécifiée",
                (categorie != null && !categorie.isEmpty()) ? categorie : "Non spécifiée",
                (localisation != null && !localisation.isEmpty()) ? localisation : "Non spécifiée",
                (type != null && !type.isEmpty()) ? type : "Location");

        String reponse = envoyerRequete(prompt);

        // Gemini yredlna string (ex: "250.00" wella "1,500.00"), lezem n7awlouha l double
        // nna7iw kol haja mahich chiffre, point, wella virgule
        String prixStr = reponse.replaceAll("[^0-9.,]", "");
        // ken fih virgule comme separateur milliers (ex: 1,500.00), nna7iwha
        // sinon ken fih virgule comme separateur decimal (ex: 250,00), nbadlouha b point
        if (prixStr.contains(",") && prixStr.contains(".")) {
            // Format 1,500.00 -> nna7iw el virgule
            prixStr = prixStr.replace(",", "");
        } else if (prixStr.contains(",")) {
            // Format 250,00 -> nbadlou b point
            prixStr = prixStr.replace(",", ".");
        }
        try {
            return Double.parseDouble(prixStr);
        } catch (NumberFormatException e) {
            throw new Exception("L'IA n'a pas pu déterminer un prix. Réponse : " + reponse);
        }
    }



    // ===== 3. MODERER CONTENU =====
    // nab3thou el titre + description l Gemini, w howa y9ollna ken fih mochkla wella ok
    // retourne null = OK, sinon retourne le motif de rejet
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



    // ===== METHODE PRIVEE : Envoi de la requete HTTP a Gemini =====
    // hedhi hiya el methode elli tab3ath el prompt l Google w traj3a el reponse
    // tkhdem bel HttpClient (Java 11+) w JSON (org.json)
    // ken Google yredlna 429 wella 403 (rate limit), nestannew 5 secondes w n3awdou
    private String envoyerRequete(String prompt) throws Exception {
        // nebniw el JSON elli nab3thousou (format elli Gemini yestanna)
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

        // nab3thou requete HTTP POST bel JSON
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_URL))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        // retry 2 fois max en cas de rate limit (403 ou 429)
        int maxRetries = 2;
        HttpResponse<String> response = null;
        for (int i = 0; i <= maxRetries; i++) {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // ken mochkla rate limit, nestannew w n3awdou
            if ((response.statusCode() == 429 || response.statusCode() == 403) && i < maxRetries) {
                System.out.println("Rate limit Gemini, on attend 5 secondes... (tentative " + (i + 1) + ")");
                Thread.sleep(5000);
                continue;
            }
            break;
        }

        // nchoufou el status code : 200 = OK, ghir 200 = mochkla
        if (response.statusCode() != 200) {
            switch (response.statusCode()) {
                case 429:
                    throw new Exception("Quota API dépassé. Patientez quelques secondes et réessayez.");
                case 401:
                    throw new Exception("Clé API Gemini invalide. Vérifiez votre configuration.");
                case 403:
                    throw new Exception("Quota API dépassé. Patientez quelques secondes et réessayez.");
                default:
                    throw new Exception("Erreur API Gemini (HTTP " + response.statusCode() + "). Réessayez plus tard.");
            }
        }

        // nestakhrejou el texte mel JSON mta3 la reponse
        // el structure : candidates[0].content.parts[0].text
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
