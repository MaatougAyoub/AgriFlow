import entities.PlanIrrigation;
import services.ServicePlanIrrigation;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TestCreatePlanIrrigation {

    public static void run() {
        System.out.println("TEST CREATE PLAN IRRIGATION ");
        try {
            ServicePlanIrrigation service = new ServicePlanIrrigation();

            PlanIrrigation plan = new PlanIrrigation();
            plan.setNomCulture("Tomates Test");
            plan.setDateDemande(LocalDateTime.now());
            plan.setStatut("en_attente");
            plan.setVolumeEauPropose(45.5f);
            plan.setTempIrrigation(LocalTime.of(6, 30));
            plan.setTemp(LocalDateTime.now());
            plan.setDonneesMeteojson("{\"temperature\": 28, \"humidite\": 65}");

            service.ajouter(plan);

            System.out.println(" CREATE réussi :");
            System.out.println("   Culture      : " + plan.getNomCulture());
            System.out.println("   Statut       : " + plan.getStatut());
            System.out.println("   Volume eau   : " + plan.getVolumeEauPropose() + " L");
            System.out.println("   Heure irrig. : " + plan.getTempIrrigation());
        } catch (Exception e) {
            System.out.println("❌ CREATE échoué : " + e.getMessage());
        }
        System.out.println();
    }

    public static void main(String[] args) {
        run();
    }
}