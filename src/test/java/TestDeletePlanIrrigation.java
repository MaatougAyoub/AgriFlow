import entities.PlanIrrigation;
import services.ServicePlanIrrigation;

import java.util.List;

public class TestDeletePlanIrrigation {

    public static void run() {
        System.out.println("TEST DELETE PLAN IRRIGATION ");
        try {
            ServicePlanIrrigation service = new ServicePlanIrrigation();
            List<PlanIrrigation> plans = service.recuperer();

            if (!plans.isEmpty()) {
                PlanIrrigation dernier = plans.get(plans.size() - 1);
                int id = dernier.getPlanId();
                String nom = dernier.getNomCulture();

                service.supprimer(dernier);

                System.out.println("plan ID " + id +" supprimé");
            } else {
                System.out.println("Aucun plan à supprimer");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
    }

    public static void main(String[] args) {
        run();
    }
}