import entities.PlanIrrigation;
import services.ServicePlanIrrigation;

import java.util.List;

public class TestReadPlanIrrigation {

    public static void run() {
        System.out.println("──────────── TEST READ ALL PLANS IRRIGATION ────────────");
        try {
            ServicePlanIrrigation service = new ServicePlanIrrigation();
            List<PlanIrrigation> plans = service.recuperer();

                System.out.printf("%-6s %-15s %-12s %-10s %-12s %-10s%n",
                        "ID", "Culture", "Statut", "Volume", "Heure Irr.", "Date");
                System.out.println("────── ─────────────── ──────────── ────────── ──────────── ──────────");

                for (PlanIrrigation p : plans) {
                    System.out.printf("%-6d %-15s %-12s %-10.1f %-12s %-10s%n",
                            p.getPlanId(),
                            p.getNomCulture(),
                            p.getStatut(),
                            p.getVolumeEauPropose(),
                            p.getTempIrrigation() != null ? p.getTempIrrigation().toString() : "N/A",
                            p.getDateDemande() != null ? p.getDateDemande().toLocalDate().toString() : "N/A");
            }

            System.out.println("réussi : " + plans.size());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
    }

    public static void main(String[] args) {
        run();
    }
}