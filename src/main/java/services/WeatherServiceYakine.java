package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.DailyForecast;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Récupère les prévisions météo (Open-Meteo, gratuit, sans clé API)
 * pour une localisation et une période données.
 * Utilise JsonParser manuel pour éviter les problèmes d'accessibilité
 * des modules Java avec Gson.
 */
public class WeatherServiceYakine {

    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    /**
     * Prévisions quotidiennes pour la parcelle entre start et end (dates de travail).
     * Retourne une liste vide en cas d'erreur ou si lat/lng sont null.
     */
    public List<DailyForecast> getForecast(Double latitude, Double longitude, LocalDate start, LocalDate end) {
        if (latitude == null || longitude == null || start == null || end == null) {
            return new ArrayList<>();
        }
        if (end.isBefore(start)) {
            return new ArrayList<>();
        }

        try {
            String url = String.format(java.util.Locale.US,
                "%s?latitude=%.4f&longitude=%.4f&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,weather_code&forecast_days=16&timezone=auto",
                BASE_URL, latitude, longitude
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                System.err.println("WeatherService: HTTP " + response.statusCode() + " - " + response.body());
                return new ArrayList<>();
            }

            // Parse manuel via JsonParser — évite les restrictions de réflexion des modules
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            if (!root.has("daily") || root.get("daily").isJsonNull()) {
                System.err.println("WeatherService: pas de champ 'daily' dans la réponse");
                return new ArrayList<>();
            }

            JsonObject daily = root.getAsJsonObject("daily");
            JsonArray times   = daily.has("time")                ? daily.getAsJsonArray("time")                : new JsonArray();
            JsonArray tMaxArr = daily.has("temperature_2m_max")  ? daily.getAsJsonArray("temperature_2m_max")  : new JsonArray();
            JsonArray tMinArr = daily.has("temperature_2m_min")  ? daily.getAsJsonArray("temperature_2m_min")  : new JsonArray();
            JsonArray precArr = daily.has("precipitation_sum")   ? daily.getAsJsonArray("precipitation_sum")   : new JsonArray();
            JsonArray codeArr = daily.has("weather_code")        ? daily.getAsJsonArray("weather_code")        : new JsonArray();

            List<DailyForecast> list = new ArrayList<>();
            for (int i = 0; i < times.size(); i++) {
                JsonElement timeEl = times.get(i);
                if (timeEl == null || timeEl.isJsonNull()) continue;
                LocalDate date = LocalDate.parse(timeEl.getAsString());
                // Ne garder que les jours dans la période de travail [start, end]
                if (date.isBefore(start) || date.isAfter(end)) continue;

                double tMax   = getDouble(tMaxArr, i, 0.0);
                double tMin   = getDouble(tMinArr, i, 0.0);
                double precip = getDouble(precArr,  i, 0.0);
                int    code   = getInt(codeArr,    i, 0);

                list.add(new DailyForecast(date, tMin, tMax, precip, code));
            }
            System.out.println("WeatherService: " + list.size() + " jour(s) de prévision chargé(s)");
            return list;

        } catch (Exception e) {
            System.err.println("WeatherService: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static double getDouble(JsonArray arr, int i, double def) {
        if (arr == null || i < 0 || i >= arr.size()) return def;
        JsonElement el = arr.get(i);
        return (el == null || el.isJsonNull()) ? def : el.getAsDouble();
    }

    private static int getInt(JsonArray arr, int i, int def) {
        if (arr == null || i < 0 || i >= arr.size()) return def;
        JsonElement el = arr.get(i);
        return (el == null || el.isJsonNull()) ? def : el.getAsInt();
    }
}