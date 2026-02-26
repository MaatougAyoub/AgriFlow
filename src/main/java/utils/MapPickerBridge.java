package utils;

import javafx.application.Platform;

/**
 * Pont entre la page HTML (Leaflet / OpenStreetMap) et JavaFX.
 * Appelé depuis JavaScript quand l'utilisateur choisit une localisation.
 */
public class MapPickerBridge {

    private final Runnable onLocationSelected;
    private double latitude;
    private double longitude;
    private String address;

    /**
     * @param onLocationSelected callback exécuté sur le thread JavaFX
     *                           une fois que latitude/longitude/adresse sont renseignés.
     */
    public MapPickerBridge(Runnable onLocationSelected) {
        this.onLocationSelected = onLocationSelected;
    }

    /**
     * Méthode appelée depuis JavaScript (window.javaBridge.setLocation(...)).
     */
    public void setLocation(double lat, double lng, String addr) {
        this.latitude = lat;
        this.longitude = lng;
        this.address = addr != null ? addr : "";

        if (onLocationSelected != null) {
            Platform.runLater(onLocationSelected);
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }
}

