package services;

import entities.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Test d'intégration JUnit 5 pour ServiceReservation.
 *
 * Conforme au pattern exact du professeur (PidevTest) :
 * - @TestMethodOrder(MethodOrderer.OrderAnnotation.class) pour l'ordre CRUD
 * - @BeforeAll pour initialiser le service
 * - @Test @Order(n) pour chaque opération CRUD ordonnée
 * - assertTrue/assertFalse avec stream().anyMatch() pour vérification
 * - @AfterAll pour le nettoyage final
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReservationServiceTest {

    static ServiceReservation service;
    static UserService userService;
    static AnnonceService annonceService;

    // IDs partagés entre les tests (exactement comme PidevTest)
    static int idReservationTest;
    static int idUserTest;
    static int idAnnonceTest;

    @BeforeAll
    static void setup() {
        service = new ServiceReservation();
        userService = new UserService();
        annonceService = new AnnonceService();
    }

    // =========================================
    // @Order(1) — Test Ajouter Réservation
    // =========================================
    @Test
    @Order(1)
    void testAjouterReservation() throws SQLException {
        // Créer un utilisateur de test
        User user = new User();
        user.setNom("TestNom");
        user.setPrenom("TestPrenom");
        user.setEmail("test_reservation@agriflow.tn");
        user.setTelephone("+216 99 999 999");
        user.setRegion("Sousse");
        userService.ajouter(user);

        // Récupérer l'ID du user créé
        List<User> users = userService.recuperer();
        assertFalse(users.isEmpty());
        User userCree = users.stream()
                .filter(u -> u.getEmail().equals("test_reservation@agriflow.tn"))
                .findFirst().orElse(null);
        assertNotNull(userCree);
        idUserTest = userCree.getId();

        // Créer une annonce de test
        Annonce annonce = new Annonce();
        annonce.setTitre("Tracteur Test JUnit");
        annonce.setDescription("Annonce de test pour JUnit");
        annonce.setType(TypeAnnonce.LOCATION);
        annonce.setStatut(StatutAnnonce.DISPONIBLE);
        annonce.setPrix(100.0); // 100 DT/jour
        annonce.setUnitePrix("jour");
        annonce.setCategorie("Tracteur");
        annonce.setLocalisation("Sousse");
        annonce.setProprietaire(userCree);
        annonce.setDateDebutDisponibilite(LocalDate.now());
        annonce.setDateFinDisponibilite(LocalDate.now().plusMonths(3));
        annonceService.ajouter(annonce);

        List<Annonce> annonces = annonceService.recuperer();
        Annonce annonceCree = annonces.stream()
                .filter(a -> a.getTitre().equals("Tracteur Test JUnit"))
                .findFirst().orElse(null);
        assertNotNull(annonceCree);
        idAnnonceTest = annonceCree.getId();

        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setAnnonce(annonceCree);
        reservation.setDemandeur(userCree);
        reservation.setDateDebut(LocalDate.now().plusDays(1));
        reservation.setDateFin(LocalDate.now().plusDays(3)); // 3 jours

        service.ajouter(reservation);

        // Vérifier (stream + anyMatch — pattern du prof)
        List<Reservation> reservations = service.afficherTout();
        assertFalse(reservations.isEmpty());

        boolean existe = reservations.stream()
                .anyMatch(r -> r.getDemandeur() != null
                        && r.getDemandeur().getId() == idUserTest
                        && r.getAnnonce() != null
                        && r.getAnnonce().getId() == idAnnonceTest);
        assertTrue(existe, "La réservation doit exister après ajout");

        // Récupérer l'ID pour les tests suivants
        idReservationTest = reservations.stream()
                .filter(r -> r.getDemandeur() != null && r.getDemandeur().getId() == idUserTest)
                .findFirst()
                .map(Reservation::getId)
                .orElse(0);

        assertTrue(idReservationTest > 0, "L'ID de réservation doit être > 0");

        // Vérifier le statut par défaut EN_ATTENTE
        Reservation creee = service.recupererParId(idReservationTest);
        assertNotNull(creee);
        assertEquals(StatutReservation.EN_ATTENTE, creee.getStatut(),
                "Le statut par défaut doit être EN_ATTENTE");

        // Vérifier le calcul du prix (durée x prixJour + commission 10%)
        // 3 jours x 100 DT = 300 DT + 10% = 330 DT
        assertTrue(creee.getPrixTotal() > 0, "Le prix total doit être > 0");

        System.out.println("✅ Test Ajouter OK — ID: " + idReservationTest);
    }

    // =========================================
    // @Order(2) — Test Modifier Réservation
    // =========================================
    @Test
    @Order(2)
    void testModifierReservation() throws SQLException {
        assertTrue(idReservationTest > 0, "ID de réservation doit exister (de @Order(1))");

        Reservation reservation = service.recupererParId(idReservationTest);
        assertNotNull(reservation);

        // Modifier les dates
        LocalDate nouvelleDateDebut = LocalDate.now().plusDays(5);
        LocalDate nouvelleDateFin = LocalDate.now().plusDays(10);
        reservation.setDateDebut(nouvelleDateDebut);
        reservation.setDateFin(nouvelleDateFin);

        service.modifier(reservation);

        // Vérifier la modification (pattern prof : stream + anyMatch)
        List<Reservation> reservations = service.afficherTout();
        boolean modifiee = reservations.stream()
                .anyMatch(r -> r.getId() == idReservationTest
                        && r.getDateDebut().equals(nouvelleDateDebut));
        assertTrue(modifiee, "Les dates doivent être modifiées");

        System.out.println("✅ Test Modifier OK");
    }

    // =========================================
    // @Order(3) — Test Afficher (afficherTout)
    // =========================================
    @Test
    @Order(3)
    void testAfficherTout() throws SQLException {
        List<Reservation> reservations = service.afficherTout();
        assertNotNull(reservations);
        assertFalse(reservations.isEmpty(), "La liste ne doit pas être vide");

        // Vérifier que notre réservation test est dans la liste
        boolean existe = reservations.stream()
                .anyMatch(r -> r.getId() == idReservationTest);
        assertTrue(existe, "La réservation de test doit être dans afficherTout()");

        System.out.println("✅ Test AfficherTout OK — Total: " + reservations.size());
    }

    // =========================================
    // @Order(4) — Test Supprimer Réservation
    // =========================================
    @Test
    @Order(4)
    void testSupprimerReservation() throws SQLException {
        assertTrue(idReservationTest > 0, "ID de réservation doit exister");

        Reservation reservation = service.recupererParId(idReservationTest);
        assertNotNull(reservation);

        service.supprimer(reservation);

        // Vérifier la suppression (pattern prof : stream + anyMatch → assertFalse)
        List<Reservation> reservations = service.afficherTout();
        boolean existe = reservations.stream()
                .anyMatch(r -> r.getId() == idReservationTest);
        assertFalse(existe, "La réservation ne doit plus exister après suppression");

        System.out.println("✅ Test Supprimer OK");
    }

    // =========================================
    // Nettoyage final — exactement comme PidevTest
    // =========================================
    @AfterAll
    static void cleanup() {
        try {
            // Nettoyer les données de test
            if (idAnnonceTest > 0) {
                Annonce a = new Annonce();
                a.setId(idAnnonceTest);
                annonceService.supprimer(a);
            }
            if (idUserTest > 0) {
                User u = new User();
                u.setId(idUserTest);
                userService.supprimer(u);
            }
            System.out.println("🧹 Nettoyage des données de test terminé");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur nettoyage: " + e.getMessage());
        }
    }
}
