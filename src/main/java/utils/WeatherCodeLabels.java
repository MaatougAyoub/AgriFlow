package utils;

import java.util.Map;

/**
 * Codes météo WMO utilisés par Open-Meteo → libellés français.
 * @see <a href="https://open-meteo.com/en/docs#api_form">Open-Meteo docs</a>
 */
public final class WeatherCodeLabels {

    private static final Map<Integer, String> LABELS = Map.ofEntries(
        Map.entry(0, "Ciel dégagé"),
        Map.entry(1, "Principalement dégagé"),
        Map.entry(2, "Partiellement nuageux"),
        Map.entry(3, "Couvert"),
        Map.entry(45, "Brouillard"),
        Map.entry(48, "Brouillard givrant"),
        Map.entry(51, "Bruine légère"),
        Map.entry(53, "Bruine modérée"),
        Map.entry(55, "Bruine dense"),
        Map.entry(56, "Bruine verglaçante légère"),
        Map.entry(57, "Bruine verglaçante dense"),
        Map.entry(61, "Pluie légère"),
        Map.entry(63, "Pluie modérée"),
        Map.entry(65, "Pluie forte"),
        Map.entry(66, "Pluie verglaçante légère"),
        Map.entry(67, "Pluie verglaçante forte"),
        Map.entry(71, "Chute de neige légère"),
        Map.entry(73, "Chute de neige modérée"),
        Map.entry(75, "Chute de neige forte"),
        Map.entry(77, "Grains de neige"),
        Map.entry(80, "Averses légères"),
        Map.entry(81, "Averses modérées"),
        Map.entry(82, "Averses violentes"),
        Map.entry(85, "Averses de neige légères"),
        Map.entry(86, "Averses de neige fortes"),
        Map.entry(95, "Orage"),
        Map.entry(96, "Orage avec grêle légère"),
        Map.entry(99, "Orage avec grêle forte")
    );

    public static String label(int code) {
        return LABELS.getOrDefault(code, "Conditions variées");
    }
}
