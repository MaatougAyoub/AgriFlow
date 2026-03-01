package utils;

import javafx.application.Platform;
import javafx.scene.web.WebEngine;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Pont entre la page HTML (Leaflet / OpenStreetMap) et JavaFX.
 * Appelé depuis JavaScript quand l'utilisateur choisit une localisation.
 * Le géocodage inversé est effectué côté Java (HttpClient) pour contourner
 * les restrictions réseau du WebView (fetch/CORS).
 */
public class MapPickerBridge {

    private final Runnable onLocationSelected;
    private WebEngine webEngine;
    private double latitude;
    private double longitude;
    private String address;

    private static final HttpClient HTTP = HttpClient.newHttpClient();

    /**
     * @param onLocationSelected callback exécuté sur le thread JavaFX
     *                           une fois que latitude/longitude/adresse sont renseignés.
     */
    public MapPickerBridge(Runnable onLocationSelected) {
        this.onLocationSelected = onLocationSelected;
    }

    /** Permet au controller de passer le WebEngine pour les callbacks JS. */
    public void setWebEngine(WebEngine engine) {
        this.webEngine = engine;
    }

    /**
     * Appelé depuis JavaScript : déclenche le géocodage inversé en Java
     * et renvoie l'adresse au JS via updateAddressLabel().
     */
    public void lookupAddress(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;

        new Thread(() -> {
            String addr = fetchAddressFromNominatim(lat, lng);
            this.address = addr != null ? addr : "";

            Platform.runLater(() -> {
                // Mettre à jour le label dans la page HTML
                if (webEngine != null) {
                    String safe = this.address.replace("\\", "\\\\").replace("'", "\\'").replace("\n", " ");
                    webEngine.executeScript("updateAddressLabel('" + safe + "')");
                }
                // Déclencher le callback JavaFX
                if (onLocationSelected != null) {
                    onLocationSelected.run();
                }
            });
        }).start();
    }

    /**
     * Méthode appelée depuis JavaScript (ancien mode, conservée pour compatibilité).
     */
    public void setLocation(double lat, double lng, String addr) {
        this.latitude = lat;
        this.longitude = lng;
        this.address = addr != null ? addr : "";

        if (onLocationSelected != null) {
            Platform.runLater(onLocationSelected);
        }
    }

    /** Effectue le géocodage inversé via l'API Nominatim (côté Java). */
    private String fetchAddressFromNominatim(double lat, double lng) {
        try {
            String url = String.format(java.util.Locale.US,
                "https://nominatim.openstreetmap.org/reverse?format=json&lat=%.6f&lon=%.6f&zoom=18&addressdetails=1",
                lat, lng);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("User-Agent", "AgriFlow-PIDEV2026/1.0")
                .GET()
                .build();

            HttpResponse<String> response = HTTP.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() == 200) {
                JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                if (root.has("display_name") && !root.get("display_name").isJsonNull()) {
                    return root.get("display_name").getAsString();
                }
            } else {
                System.err.println("Nominatim HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Nominatim error: " + e.getMessage());
        }
        // Fallback : coordonnées brutes
        return String.format(java.util.Locale.US, "%.5f, %.5f", lat, lng);
    }

    public double getLatitude()  { return latitude; }
    public double getLongitude() { return longitude; }
    public String getAddress()   { return address; }
}

