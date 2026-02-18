import entities.Categorie;
import entities.Reclamation;
import entities.ReclamationRow;
import entities.Statut;
import org.junit.jupiter.api.*;
import services.ServiceReclamation;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReclamationServiceTest {

    static ServiceReclamation sr;
    static int idReclamation;

    // ⚠️ adapte cet id à un utilisateur qui existe chez toi dans la table utilisateurs
    // (par ex. ton admin id=38 dans ta capture précédente)
    static final int TEST_USER_ID = 38;

    @BeforeAll
    static void setUp() {
        sr = new ServiceReclamation();
    }

    @Test
    @Order(1)
    void testAjouterReclamation() throws SQLException {
        Reclamation r = new Reclamation(
                TEST_USER_ID,
                Categorie.TECHNIQUE,
                "Titre test",
                "Description test"
        );

        sr.ajouterReclamation(r);

        // ✅ l'id est récupéré via getGeneratedKeys dans ajouterReclamation
        assertTrue(r.getId() > 0, "L'id de la réclamation n'a pas été généré");
        idReclamation = r.getId();
        System.out.println("Inserted reclamation id = " + idReclamation);

        // ✅ vérifier que la réclamation existe via recupererReclamation()
        List<Reclamation> recs = sr.recupererReclamation();
        assertTrue(recs.stream().anyMatch(x -> x.getId() == idReclamation),
                "La réclamation insérée n'est pas présente dans recupererReclamation()");
    }

    @Test
    @Order(2)
    void testModifierReclamation() throws SQLException {
        assertTrue(idReclamation > 0, "idReclamation non initialisé. Exécute testAjouterReclamation d'abord.");

        Reclamation updated = new Reclamation(
                idReclamation,
                TEST_USER_ID,
                Categorie.SERVICE,
                "Titre modifié",
                "Description modifiée"
        );
        updated.setStatut(Statut.EN_ATTENTE);
        updated.setReponse(null);

        sr.modifierReclamation(updated);

        List<Reclamation> recs = sr.recupererReclamation();
        Reclamation found = recs.stream()
                .filter(x -> x.getId() == idReclamation)
                .findFirst()
                .orElse(null);

        assertNotNull(found, "Réclamation introuvable après modification");
        assertEquals(TEST_USER_ID, found.getId_utilisateur());
        assertEquals(Categorie.SERVICE, found.getCategorie());
        assertEquals("Titre modifié", found.getTitre());
        assertEquals("Description modifiée", found.getDescription());
        assertEquals(Statut.EN_ATTENTE, found.getStatut());
    }

    @Test
    @Order(3)
    void testRecupererReclamationAvecUtilisateur() throws SQLException {
        assertTrue(idReclamation > 0, "idReclamation non initialisé.");

        List<ReclamationRow> rows = sr.recupererReclamationAvecUtilisateur();
        assertNotNull(rows);

        ReclamationRow row = rows.stream()
                .filter(x -> x.getId() == idReclamation)
                .findFirst()
                .orElse(null);

        assertNotNull(row, "La réclamation n'apparaît pas dans recupererReclamationAvecUtilisateur()");
        assertEquals(TEST_USER_ID, row.getUtilisateurId());
        assertNotNull(row.getEmail(), "Email utilisateur doit être rempli via JOIN");
        assertNotNull(row.getNom(), "Nom utilisateur doit être rempli via JOIN");
        assertNotNull(row.getPrenom(), "Prénom utilisateur doit être rempli via JOIN");
    }

    @Test
    @Order(4)
    void testAjouterReponseConcatenee() throws SQLException {
        assertTrue(idReclamation > 0, "idReclamation non initialisé.");

        String rep1 = "Admin Test (ADMIN) : première réponse";
        sr.ajouterReponseConcatenee(idReclamation, rep1);

        String after1 = sr.getReponseById(idReclamation);
        assertNotNull(after1);
        assertTrue(after1.contains("première réponse"));

        String rep2 = "Expert Test (EXPERT) : deuxième réponse";
        sr.ajouterReponseConcatenee(idReclamation, rep2);

        String after2 = sr.getReponseById(idReclamation);
        assertNotNull(after2);

        // ✅ doit contenir les deux réponses + concaténation avec saut de ligne
        assertTrue(after2.contains("première réponse"));
        assertTrue(after2.contains("deuxième réponse"));
        assertTrue(after2.contains("\n"), "La concaténation devrait ajouter un saut de ligne");
    }

    @Test
    @Order(5)
    void testGetReponseById() throws SQLException {
        assertTrue(idReclamation > 0, "idReclamation non initialisé.");

        String rep = sr.getReponseById(idReclamation);
        // On ne peut pas garantir qu'elle n'est pas null, mais ici on l'a alimentée dans le test précédent
        assertNotNull(rep, "La réponse ne devrait pas être null après testAjouterReponseConcatenee()");
    }

    @Test
    @Order(6)
    void testSupprimerReclamation() throws SQLException {
        assertTrue(idReclamation > 0, "idReclamation non initialisé.");

        // Service.supprimerReclamation attend un Reclamation (tu utilises id)
        sr.supprimerReclamation(new Reclamation(idReclamation, TEST_USER_ID, null, null, null));

        List<Reclamation> recs = sr.recupererReclamation();
        assertFalse(recs.stream().anyMatch(x -> x.getId() == idReclamation),
                "La réclamation n'a pas été supprimée");
    }
}