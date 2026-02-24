package services;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ServiceGenerateurPlan {

    private static final float DEBIT_SYSTEME_MM_H = 12.0f;

    public Map<String, float[]> genererPlanHebdo(float totalEauMm) {
        Map<String, float[]> plan = new HashMap<>();
        String[] jours = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        Random random = new Random();

        float eauRestante = totalEauMm;

        // On boucle (Lundi -> Samedi)
        for (int i = 0; i < 6; i++) {

            int joursRestants = 7 - i;
            float moyenneIdeale = eauRestante / joursRestants;

            float variation = (random.nextFloat() * 0.3f) - 0.15f;
            float eauJour = moyenneIdeale * (1 + variation);
            eauJour = Math.round(eauJour * 100.0f) / 100.0f;


            if (eauJour < 0) eauJour = 0;

            eauRestante -= eauJour;
            int tempsMin = calculerDuree(eauJour);
            float tempC = genererTemperature(random);

            plan.put(jours[i], new float[]{eauJour, (float) tempsMin, tempC});
        }

        //  (DIMANCHE)
        float eauDimanche = Math.round(eauRestante * 100.0f) / 100.0f;
        if (eauDimanche < 0) eauDimanche = 0;

        int tempsDimanche = calculerDuree(eauDimanche);
        float tempDimanche = genererTemperature(random);

        plan.put("SUN", new float[]{eauDimanche, (float) tempsDimanche, tempDimanche});

        return plan;
    }


    private int calculerDuree(float eauMm) {
        if (eauMm <= 0) return 0;

        float heures = eauMm / DEBIT_SYSTEME_MM_H;
        return (int) Math.ceil(heures * 60);
    }


    private float genererTemperature(Random random) {
        float temp = 20 + random.nextInt(12) + random.nextFloat();
        return Math.round(temp * 10.0f) / 10.0f;
    }
}