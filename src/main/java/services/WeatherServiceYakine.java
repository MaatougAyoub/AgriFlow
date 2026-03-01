package services;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
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
 */
public class WeatherServiceYakine {

    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

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
            // L'API standard n'accepte pas start_date/end_date, seulement forecast_days (max 16)
            String url = String.format(
                    "%s?latitude=%.4f&longitude=%.4f&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,weathercode&forecast_days=16&timezone=auto",
                    BASE_URL, latitude, longitude
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                System.err.println("WeatherService: HTTP " + response.statusCode());
                return new ArrayList<>();
            }

            OpenMeteoResponse parsed = GSON.fromJson(response.body(), OpenMeteoResponse.class);
            if (parsed == null || parsed.daily == null) {
                return new ArrayList<>();
            }

            List<DailyForecast> list = new ArrayList<>();
            int n = parsed.daily.time != null ? parsed.daily.time.size() : 0;
            for (int i = 0; i < n; i++) {
                LocalDate date = parsed.daily.time.get(i) != null ? LocalDate.parse(parsed.daily.time.get(i)) : null;
                if (date == null) continue;
                // Ne garder que les jours dans la période de travail [start, end]
                if (date.isBefore(start) || date.isAfter(end)) continue;
                double tMax = getAt(parsed.daily.temperature_2m_max, i, 0);
                double tMin = getAt(parsed.daily.temperature_2m_min, i, 0);
                double precip = getAt(parsed.daily.precipitation_sum, i, 0);
                int code = getAtInt(parsed.daily.weathercode, i, 0);
                list.add(new DailyForecast(date, tMin, tMax, precip, code));
            }
            return list;
        } catch (Exception e) {
            System.err.println("WeatherService: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static double getAt(List<Double> list, int i, double def) {
        if (list == null || i < 0 || i >= list.size()) return def;
        Double v = list.get(i);
        return v != null ? v : def;
    }

    private static int getAtInt(List<Number> list, int i, int def) {
        if (list == null || i < 0 || i >= list.size()) return def;
        Number v = list.get(i);
        return v != null ? v.intValue() : def;
    }

    /** Structure JSON renvoyée par Open-Meteo (daily uniquement). */
    @SuppressWarnings("unused")
    private static class OpenMeteoResponse {
        Daily daily;
    }

    @SuppressWarnings("unused")
    private static class Daily {
        List<String> time;
        @SerializedName("temperature_2m_max")
        List<Double> temperature_2m_max;
        @SerializedName("temperature_2m_min")
        List<Double> temperature_2m_min;
        @SerializedName("precipitation_sum")
        List<Double> precipitation_sum;
        List<Number> weathercode;
    }
}
