package services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MailerSendEmailService {
    private final String apiKey;
    private final String fromEmail;
    private final String fromName;

    public MailerSendEmailService() {
        this.apiKey = requireConfig("MAILERSEND_API_KEY");
        this.fromEmail = requireConfig("MAILERSEND_FROM_EMAIL");
        this.fromName = getConfigOrDefault("MAILERSEND_FROM_NAME", "AgriFlow");
    }

    public void sendEmail(String toEmail, String subject, String text, String html) throws Exception {
        sendEmail(toEmail, null, subject, text, html);
    }

    public void sendEmail(String toEmail, String toName, String subject, String text, String html) throws Exception {
        if (toEmail == null || toEmail.isBlank()) {
            throw new IllegalArgumentException("Email destinataire manquant");
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Sujet email manquant");
        }

        // Build JSON body for Brevo API
        JSONObject sender = new JSONObject();
        sender.put("name", fromName);
        sender.put("email", fromEmail);

        JSONObject recipient = new JSONObject();
        recipient.put("email", toEmail);
        if (toName != null && !toName.isBlank()) {
            recipient.put("name", toName);
        }

        JSONArray toArray = new JSONArray();
        toArray.put(recipient);

        JSONObject body = new JSONObject();
        body.put("sender", sender);
        body.put("to", toArray);
        body.put("subject", subject);

        if (html != null && !html.isBlank()) {
            body.put("htmlContent", html);
        } else if (text != null && !text.isBlank()) {
            body.put("textContent", text);
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new Exception("Brevo API error (" + response.statusCode() + "): " + response.body());
        }
    }

    private static String requireConfig(String key) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Configuration manquante: " + key + ". " +
                            "Définissez-la en variable d'environnement ou en option JVM -D" + key + "=...");
        }
        return value;
    }

    private static String getConfigOrDefault(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key);
        }
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}