import entities.Diagnostic;
import services.ExpertService;

import java.util.List;

public class TestMain {

    public static void main(String[] args) {

        System.out.println("=== TEST AFFICHAGE DIAGNOSTICS ===");

        try {
            ExpertService service = new ExpertService();
            List<Diagnostic> diagnostics = service.getAllDiagnostics();

            if (diagnostics.isEmpty()) {
                System.out.println("❌ AUCUN DIAGNOSTIC TROUVÉ");
            } else {
                System.out.println("✅ Nombre de diagnostics : " + diagnostics.size());

                for (Diagnostic d : diagnostics) {
                    System.out.println("----------------------------------");
                    System.out.println("ID Diagnostic : " + d.getIdDiagnostic());
                    System.out.println("ID Agriculteur: " + d.getIdAgriculteur());
                    System.out.println("Culture       : " + d.getNomCulture());
                    System.out.println("Description   : " + d.getDescription());
                    System.out.println("Image Path    : " + d.getImagePath());
                    System.out.println("Réponse Expert: " + d.getReponseExpert());
                    System.out.println("Date Envoi    : " + d.getDateEnvoi());
                    System.out.println("Statut        : " + d.getStatut());
                }
            }

        } catch (Exception e) {
            System.out.println("❌ ERREUR LORS DE LA RÉCUPÉRATION DES DIAGNOSTICS");
            e.printStackTrace();
        }

        System.out.println("=== FIN TEST ===");
    }
}
