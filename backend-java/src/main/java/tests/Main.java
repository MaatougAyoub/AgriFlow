package tests;

import entities.CollabRequest;
import entities.CollabApplication;
import services.CollabRequestService;
import services.CollabApplicationService;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private final CollabRequestService requestService = new CollabRequestService();
    private final CollabApplicationService applicationService = new CollabApplicationService();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public void run() {
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║   AGRIFLOW - MODULE COLLABORATIONS (JDBC CRUD)   ║");
        System.out.println("║     Architecture 3-tiers professionnelle         ║");
        System.out.println("╚═══════════════════════════════════════════════════╝\n");

        while (true) {
            System.out.println("\n┌─────────────────────────────────────┐");
            System.out.println("│       MENU PRINCIPAL                │");
            System.out.println("├─────────────────────────────────────┤");
            System.out.println("│ DEMANDES DE COLLABORATION           │");
            System.out.println("│  1. Créer une demande               │");
            System.out.println("│  2. Lister toutes les demandes      │");
            System.out.println("│  3. Modifier statut d'une demande   │");
            System.out.println("│  4. Supprimer une demande           │");
            System.out.println("├─────────────────────────────────────┤");
            System.out.println("│ CANDIDATURES                        │");
            System.out.println("│  5. Créer une candidature           │");
            System.out.println("│  6. Lister candidatures (demande)   │");
            System.out.println("│  7. Modifier statut candidature     │");
            System.out.println("│  8. Supprimer candidature           │");
            System.out.println("├─────────────────────────────────────┤");
            System.out.println("│  0. Quitter                         │");
            System.out.println("└─────────────────────────────────────┘");
            System.out.print("Choix : ");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("❌ Veuillez entrer un nombre valide.");
                scanner.nextLine();
                continue;
            }

            try {
                switch (choice) {
                    case 1 -> createRequest();
                    case 2 -> listRequests();
                    case 3 -> updateRequestStatus();
                    case 4 -> deleteRequest();
                    case 5 -> createApplication();
                    case 6 -> listApplications();
                    case 7 -> updateApplicationStatus();
                    case 8 -> deleteApplication();
                    case 0 -> {
                        System.out.println("\n✅ Au revoir !");
                        return;
                    }
                    default -> System.out.println("❌ Choix invalide.");
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createRequest() throws Exception {
        System.out.println("\n--- CRÉER UNE DEMANDE ---");
        System.out.print("ID du demandeur (2, 3, 4) : ");
        long requesterId = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Titre : ");
        String title = scanner.nextLine();

        System.out.print("Description : ");
        String description = scanner.nextLine();

        System.out.print("Date début (YYYY-MM-DD) : ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Date fin (YYYY-MM-DD) : ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Nombre de personnes : ");
        int neededPeople = scanner.nextInt();
        scanner.nextLine();

        CollabRequest req = new CollabRequest(requesterId, title, description, startDate, endDate, neededPeople, "PENDING");
        long id = requestService.add(req);
        System.out.println("✅ Demande créée avec ID = " + id);
    }

    private void listRequests() throws Exception {
        System.out.println("\n--- LISTE DES DEMANDES ---");
        requestService.findAll().forEach(System.out::println);
    }

    private void updateRequestStatus() throws Exception {
        System.out.print("ID demande : ");
        long id = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Nouveau statut (PENDING/APPROVED/REJECTED/CLOSED) : ");
        String status = scanner.nextLine();

        requestService.updateStatus(id, status);
        System.out.println("✅ Demande mise à jour.");
    }

    private void deleteRequest() throws Exception {
        System.out.print("ID demande à supprimer : ");
        long id = scanner.nextLong();
        scanner.nextLine();

        requestService.delete(id);
        System.out.println("✅ Demande supprimée.");
    }

    private void createApplication() throws Exception {
        System.out.println("\n--- CRÉER UNE CANDIDATURE ---");
        System.out.print("ID de la demande : ");
        long requestId = scanner.nextLong();

        System.out.print("ID du candidat : ");
        long candidateId = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Message : ");
        String message = scanner.nextLine();

        CollabApplication app = new CollabApplication(requestId, candidateId, message, "PENDING");
        long id = applicationService.add(app);
        System.out.println("✅ Candidature créée avec ID = " + id);
    }

    private void listApplications() throws Exception {
        System.out.print("ID de la demande : ");
        long requestId = scanner.nextLong();
        scanner.nextLine();

        System.out.println("\n--- CANDIDATURES pour demande " + requestId + " ---");
        applicationService.findByRequestId(requestId).forEach(System.out::println);
    }

    private void updateApplicationStatus() throws Exception {
        System.out.print("ID candidature : ");
        long id = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Nouveau statut (PENDING/APPROVED/REJECTED) : ");
        String status = scanner.nextLine();

        applicationService.updateStatus(id, status);
        System.out.println("✅ Candidature mise à jour.");
    }

    private void deleteApplication() throws Exception {
        System.out.print("ID candidature à supprimer : ");
        long id = scanner.nextLong();
        scanner.nextLine();

        applicationService.delete(id);
        System.out.println("✅ Candidature supprimée.");
    }
}
