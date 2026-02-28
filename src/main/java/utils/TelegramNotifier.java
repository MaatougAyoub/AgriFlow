package utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Utilitaire simple pour envoyer des notifications Telegram.
 * Utilise l'API officielle Telegram Bot (méthode sendMessage).
 */
public class TelegramNotifier {

    private static final String BOT_TOKEN;
    private static final String CHAT_ID;
    private static final boolean ENABLED;
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    static {
        String token = null;
        String chatId = null;

        try (InputStream in = TelegramNotifier.class
                .getClassLoader()
                .getResourceAsStream("telegram.properties")) {

            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                token = props.getProperty("telegram.botToken");
                chatId = props.getProperty("telegram.chatId");
            } else {
                System.out.println("ℹ️ Fichier telegram.properties introuvable, TelegramNotifier désactivé.");
            }
        } catch (IOException e) {
            System.err.println("❌ Impossible de charger telegram.properties : " + e.getMessage());
        }

        BOT_TOKEN = token;
        CHAT_ID = chatId;

        ENABLED = BOT_TOKEN != null
                && !BOT_TOKEN.isBlank()
                && !BOT_TOKEN.equals("VOTRE_BOT_TOKEN_ICI")
                && CHAT_ID != null
                && !CHAT_ID.isBlank()
                && !CHAT_ID.equals("VOTRE_CHAT_ID_ICI");

        if (!ENABLED) {
            System.out.println("ℹ️ TelegramNotifier désactivé (token ou chatId non configuré).");
        }
    }

    /**
     * Envoie un message texte simple sur le chat configuré.
     *
     * @param message contenu du message
     */
    public static void sendText(String message) {
        if (!ENABLED) {
            return;
        }

        if (message == null || message.isBlank()) {
            return;
        }

        try {
            String encodedText = URLEncoder.encode(message, StandardCharsets.UTF_8);
            String body = "chat_id=" + CHAT_ID + "&text=" + encodedText;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            CLIENT.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(response -> {
                        if (response.statusCode() != 200) {
                            System.err.println("⚠️ Échec envoi Telegram, code HTTP: " + response.statusCode());
                        }
                    })
                    .exceptionally(ex -> {
                        System.err.println("⚠️ Erreur lors de l'envoi Telegram: " + ex.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            System.err.println("⚠️ Exception lors de la préparation du message Telegram: " + e.getMessage());
        }
    }
}

