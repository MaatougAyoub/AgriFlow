package services;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.nio.file.Files;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class AIService {

    String apiKey = System.getenv("GROQ_API_KEY");
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";



    public String decrireImage(File selectedFile) {
        try {
            // 1. Encodage de l'image en Base64
            byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
            String base64Image = Base64.getEncoder().encodeToString(fileContent);
            String dataUrl = "data:image/jpeg;base64," + base64Image;

            // 2. Préparation du JSON avec le modèle Llama 4 Scout
            JSONObject payload = new JSONObject();
            payload.put("model", "meta-llama/llama-4-scout-17b-16e-instruct");

            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");

            JSONArray content = new JSONArray();

            // Le "Prompt" pour forcer l'IA à analyser la santé de la culture
            String instructions = "Agis en tant que système AGRIFLOW. "
                    + "Rédige une demande de réclamation agricole claire "
                    + "basée sur cette image. "
                    + "Décris le problème observé sur la culture ou le fruit, "
                    + "destinée à un expert agricole, en français.";

            content.put(new JSONObject().put("type", "text").put("text", instructions));
            content.put(new JSONObject().put("type", "image_url").put("image_url", new JSONObject().put("url", dataUrl)));

            userMessage.put("content", content);
            messages.put(userMessage);
            payload.put("messages", messages);

            // 3. Envoi de la requête à Groq
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                return jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } else {
                return "Erreur API Groq (" + response.statusCode() + ") : " + response.body();
            }

        } catch (Exception e) {
            return "Erreur lors de l'analyse : " + e.getMessage();
        }
    }
}