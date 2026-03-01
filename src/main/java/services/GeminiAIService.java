package services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

// Service IA - nesta3mlou Groq API (Llama 3) bech n7asnou les annonces w ncontroliw el contenu
// ya3ni neb3thou prompt (texte) l Groq, w howa yredlna reponse
public class GeminiAIService {

    // el cle API (chargee depuis fichier gemini_config.txt pour securite)
    private static String API_KEY;
    // el URL mta3 Groq API (compatible OpenAI format)
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    // el modele elli nesta3mlouh - Llama 3.1 8B (rapide w gratuit)
    private static final String MODEL = "llama-3.1-8b-instant";

    // HttpClient mta3 Java 11+ bech nab3thou requetes HTTP
    private final HttpClient httpClient;

    public GeminiAIService() {
        // nchargiw el cle men fichier gemini_config.txt
        chargerCleAPI();
        // n7addrou el HttpClient b timeout 15 secondes
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    // nkharjou el cle men fichier gemini_config.txt (mech hardcoded fl code)
    private void chargerCleAPI() {
        if (API_KEY != null) return; // deja chargee
        try {
            java.io.File configFile = new java.io.File("gemini_config.txt");
            if (configFile.exists()) {
                API_KEY = new String(java.nio.file.Files.readAllBytes(configFile.toPath())).trim();
                System.out.println("Clé IA chargée depuis gemini_config.txt");
            } else {
                System.err.println("⚠️ Fichier gemini_config.txt introuvable !");
                API_KEY = "";
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement clé IA : " + e.getMessage());
            API_KEY = "";
        }
    }

    // ===== 1. AMELIORER DESCRIPTION =====
    // neb3thou el titre + description l l'IA, w howa yredha 7sanner w plus professionnelle
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

        try {
            return envoyerRequete(prompt);
        } catch (Exception e) {
            System.out.println("Mode fallback IA (amélioration description)");
            String cat = (categorie != null && !categorie.isEmpty()) ? categorie : "produit agricole";
            return String.format(
                "%s — %s de qualité supérieure, disponible sur AgriFlow. "
                + "Ce %s est en excellent état et prêt à l'emploi. "
                + "Idéal pour les professionnels agricoles exigeants. "
                + "N'hésitez pas à nous contacter pour plus d'informations ou pour planifier une visite.",
                titre != null ? titre : "Article", cat, cat);
        }
    }

    // ===== 2. SUGGERER PRIX =====
    // nab3thou les details mta3 l annonce l l'IA, w howa y9ollna 9addech el soum el mouneseb
    public double suggererPrix(String titre, String description, String categorie,
            String localisation, String type) throws Exception {
        String prompt = String.format(
                "Tu es un expert du marché agricole en Tunisie. "
                        + "Suggère un prix réaliste en Dinar Tunisien (DT) pour cette annonce sur un marketplace agricole.\n\n"
                        + "IMPORTANT :\n"
                        + "- Si c'est une LOCATION, donne le prix par JOUR de location\n"
                        + "- Si c'est une VENTE, donne le prix unitaire de vente\n"
                        + "- Prends en compte le marché tunisien actuel\n\n"
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

        String reponse;
        try {
            reponse = envoyerRequete(prompt);
        } catch (Exception e) {
            System.out.println("Mode fallback IA (suggestion prix)");
            if (type != null && type.toLowerCase().contains("location")) {
                return 150.0 + Math.random() * 350;
            } else {
                return 500.0 + Math.random() * 4500;
            }
        }

        // l'IA yredlna string, lezem n7awlouha l double
        String prixStr = reponse.replaceAll("[^0-9.,]", "");
        if (prixStr.contains(",") && prixStr.contains(".")) {
            prixStr = prixStr.replace(",", "");
        } else if (prixStr.contains(",")) {
            prixStr = prixStr.replace(",", ".");
        }
        try {
            return Double.parseDouble(prixStr);
        } catch (NumberFormatException ex) {
            return 250.0;
        }
    }

    // ===== 3. MODERER CONTENU =====
    // nab3thou el titre + description l l'IA, w howa y9ollna ken fih mochkla wella ok
    // retourne null = OK, sinon retourne le motif de rejet
    public String modererContenu(String titre, String description) throws Exception {
        String prompt = String.format(
                "Marketplace agricole. Cette annonce est-elle liée à l'agriculture ? "
                        + "Titre: %s. Description: %s. "
                        + "Réponds OK si agricole, sinon REJET: motif.",
                titre, description);

        String reponse;
        try {
            reponse = envoyerRequete(prompt);
            System.out.println(">>> IA API : Réponse reçue -> " + reponse.trim());
        } catch (Exception e) {
            System.out.println(">>> IA API indisponible -> Fallback filtre local activé");
            return FraudControlService.getMotifRejet(
                    new entities.Annonce() {{ setTitre(titre); setDescription(description); setPrix(100); setPhotos(java.util.List.of("ok.jpg")); }}
            );
        }

        String upper = reponse.trim().toUpperCase();
        if (upper.startsWith("OK") || upper.startsWith("OUI") || !upper.contains("REJET")) {
            System.out.println(">>> IA : Contenu ACCEPTÉ ✅");
            return null;
        }
        // extraire le motif après "REJET:"
        String motif = reponse.trim();
        if (motif.toUpperCase().contains("REJET:")) {
            motif = motif.substring(motif.toUpperCase().indexOf("REJET:") + 6).trim();
        }
        System.out.println(">>> IA : Contenu REJETÉ ❌ -> " + motif);
        return motif;
    }

    // ===== METHODE PRIVEE : Envoi de la requete HTTP a l'API =====
    // format OpenAI compatible (Groq) : POST avec Bearer token + messages JSON
    private String envoyerRequete(String prompt) throws Exception {
        // nebniw el JSON (format OpenAI chat completions)
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);

        JSONArray messages = new JSONArray();
        messages.put(message);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 500);
        requestBody.put("temperature", 0.7);

        // nab3thou requete HTTP POST b Bearer token
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        // retry 2 fois max en cas de rate limit
        int maxRetries = 2;
        HttpResponse<String> response = null;
        for (int i = 0; i <= maxRetries; i++) {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if ((response.statusCode() == 429) && i < maxRetries) {
                System.out.println("Rate limit IA, on attend 5 secondes... (tentative " + (i + 1) + ")");
                Thread.sleep(5000);
                continue;
            }
            break;
        }

        // nchoufou el status code
        if (response.statusCode() != 200) {
            System.err.println("Erreur API IA - Status: " + response.statusCode());
            System.err.println("Réponse brute: " + response.body());
            throw new Exception("Erreur API IA (HTTP " + response.statusCode() + ")");
        }

        // nestakhrejou el texte mel JSON
        // format OpenAI : choices[0].message.content
        JSONObject responseJson = new JSONObject(response.body());
        JSONArray choices = responseJson.getJSONArray("choices");

        if (choices.isEmpty()) {
            throw new Exception("Aucune réponse générée par l'IA.");
        }

        return choices
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim();
    }
}
