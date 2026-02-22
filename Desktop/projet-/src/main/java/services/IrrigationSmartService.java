package services;

import entities.Culture;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class IrrigationSmartService {

    private final WeatherService weatherService = new WeatherService();
    private final ServiceParcelle serviceParcelle = new ServiceParcelle();


    private static final float DEBIT_L_MIN = 12.0f;

    public Map<String, float[]> genererPlanIA(Culture culture) throws Exception {
        Map<String, float[]> plan = new HashMap<>();
        String[] joursCles = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};


        String loc = serviceParcelle.recupererLocalisation(culture.getIdParcelle());

        if (loc == null || !loc.contains(",")) {
            throw new Exception("Localisation GPS introuvable pour la parcelle N°" + culture.getIdParcelle());
        }

        String[] coords = loc.split(",");
        double lat = Double.parseDouble(coords[0].trim());
        double lon = Double.parseDouble(coords[1].trim());



        JSONObject data = weatherService.getForecast(lat, lon);
        JSONObject daily = data.getJSONObject("daily");

        JSONArray pluies = daily.getJSONArray("precipitation_sum");
        JSONArray tempsMax = daily.getJSONArray("temperature_2m_max");
        JSONArray humidites = daily.getJSONArray("relative_humidity_2m_max");



        float besoinQuotidienTheorique = culture.getQuantiteEau() / 7.0f;

        for (int i = 0; i < 7; i++) {
            float pluiePrevue = (float) pluies.getDouble(i);
            float tMax = (float) tempsMax.getDouble(i);
            float humMax = (float) humidites.getDouble(i);

            // Ajustement du besoin selon la météo
            float ratioAjustement = 1.0f;


            if (tMax > 30) ratioAjustement += 0.2f;

            if (humMax > 80) ratioAjustement -= 0.1f;

            float besoinAjuste = besoinQuotidienTheorique * ratioAjustement;


            float eauAFournir = Math.max(0, besoinAjuste - pluiePrevue);

            // Calcul de la durée d'arrosage (en minutes)
            int dureeMinutes = (int) Math.ceil((eauAFournir / DEBIT_L_MIN) * 60);


            plan.put(joursCles[i], new float[]{
                    eauAFournir,
                    (float) dureeMinutes,
                    tMax,
                    humMax,
                    pluiePrevue
            });
        }

        return plan;
    }
}