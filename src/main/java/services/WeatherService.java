package services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import java.util.Locale;

public class WeatherService {

    public JSONObject getForecast(double lat, double lon) throws Exception {

        // L'utilisation de Locale.US force le point (.) au lieu de la virgule
        String urlString = String.format(Locale.US,
                "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&daily=precipitation_sum,temperature_2m_max,relative_humidity_2m_max&timezone=auto",
                lat, lon
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new Exception("Erreur serveur API : Code " + response.statusCode());
            }

            return new JSONObject(response.body());
        } catch (Exception e) {
            throw new Exception("Connexion internet perdue ou API indisponible.");
        }
    }
}