
import entities.Expert;
import entities.Role;
import org.junit.jupiter.api.*;
import services.ServiceExpert;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExpertServiceTest {

    static ServiceExpert se;
    static int idExpert; // ✅ doit être static pour être partagé entre les tests
    static String emailAdd;
    static String emailMod;
    static int cinAdd;
    static int cinMod;

    @BeforeAll
    static void setUp() {
        se = new ServiceExpert();

        String suffix = String.valueOf(System.currentTimeMillis());
        emailAdd = "expert.test." + suffix + "@gmail.com";
        emailMod = "expert.test.mod." + suffix + "@gmail.com";
        cinAdd = (int) (System.currentTimeMillis() % 1_000_000_000L);
        if (cinAdd < 10000000) cinAdd += 10000000;
        cinMod = cinAdd + 1;
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (idExpert > 0) {
            Expert toDelete = new Expert();
            toDelete.setId(idExpert);
            se.supprimerExpert(toDelete);
        }
    }

    @Test
    @Order(1)
    void testAjouterExpert() throws SQLException {
        Expert expert = new Expert(
            "foulen", "benfoulen", cinAdd, emailAdd, "motdepasse",
                Role.EXPERT.toString(), LocalDate.parse("2026-02-15"),
            "signature44.png", "certification44.png"
        );

        se.ajouterExpert(expert);

        List<Expert> experts = se.recupererExpert();

        // ✅ vérifier que l'expert existe
        Expert inserted = experts.stream()
            .filter(ex -> emailAdd.equals(ex.getEmail()))
                // on prend le plus grand id si jamais le test a été relancé plusieurs fois
                .max(Comparator.comparingInt(Expert::getId))
                .orElse(null);

        assertNotNull(inserted, "L'expert n'a pas été inséré");
        idExpert = inserted.getId();
        System.out.println("Inserted expert id = " + idExpert);
        assertTrue(idExpert > 0);
    }

    @Test
    @Order(2)
    void testModifierExpert() throws SQLException {
        assertTrue(idExpert > 0, "idExpert non initialisé. Exécute testAjouterExpert d'abord.");

        Expert updated = new Expert(
                idExpert,
                "foulen_mod", "benfoulen_mod",
            cinMod,
            emailMod,
                "motdepasse_mod",
                Role.EXPERT.toString(),
                LocalDate.parse("2026-02-16"),
                "signature_mod.png",
                "certification_mod.png"
        );

        se.modifierExpert(updated);

        List<Expert> experts = se.recupererExpert();
        Expert found = experts.stream()
                .filter(ex -> ex.getId() == idExpert)
                .findFirst()
                .orElse(null);

        assertNotNull(found, "Expert introuvable après modification");
        assertEquals("foulen_mod", found.getNom());
        assertEquals("benfoulen_mod", found.getPrenom());
        assertEquals(cinMod, found.getCin());
        assertEquals(emailMod, found.getEmail());
        assertEquals("motdepasse_mod", found.getMotDePasse());
        assertEquals("signature_mod.png", found.getSignature());
        assertEquals("certification_mod.png", found.getCertification());
    }

    @Test
    @Order(3)
    void testEmailExiste() throws SQLException {
        assertTrue(se.emailExiste(emailMod), "emailExiste devrait retourner true");
        assertFalse(se.emailExiste("email.inexistant." + System.currentTimeMillis() + "@gmail.com"), "emailExiste devrait retourner false");
    }

    @Test
    @Order(4)
    void testModifierMotDePasseParEmail() throws SQLException {
        String newPwd = "NEW_PWD_2026";
        se.modifierMotDePasseParEmail(emailMod, newPwd);

        List<Expert> experts = se.recupererExpert();
        Expert found = experts.stream()
                .filter(ex -> ex.getId() == idExpert)
                .findFirst()
                .orElse(null);

        assertNotNull(found);
        assertEquals(newPwd, found.getMotDePasse(), "Le mot de passe n'a pas été mis à jour");

        // Optionnel: vérifier aussi dans utilisateurs (si tu veux)
        // -> il faudrait une méthode de lecture côté ServiceExpert (ou requête ici),
        // mais tu as demandé de tester uniquement les méthodes du service.
    }

    @Test
    @Order(5)
    void testSupprimerExpert() throws SQLException {
        assertTrue(idExpert > 0, "idExpert non initialisé.");

        int deletedId = idExpert;

        Expert toDelete = new Expert();
        toDelete.setId(idExpert);

        se.supprimerExpert(toDelete);
        idExpert = 0; // éviter que @AfterAll réessaie

        List<Expert> experts = se.recupererExpert();
        assertFalse(experts.stream().anyMatch(ex -> ex.getId() == deletedId),
            "L'expert n'a pas été supprimé");
    }
}
//--------------------------------------------------------------------------------------------------------------------
/*
import entities.Expert;
import entities.Role;
import org.junit.jupiter.api.*;
import services.ServiceExpert;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpertServiceTest {

    static ServiceExpert se;
    int idExpert; // ✅ plus besoin de static

    @BeforeAll
    static void setUpAll() {
        se = new ServiceExpert();
    }

    @BeforeEach
    void setUp() throws SQLException {
        // ✅ créer un expert unique pour le test
        String email = "test.expert." + System.currentTimeMillis() + "@gmail.com";
        Expert expert = new Expert(
                "foulen", "benfoulen", 44556677,
                email,
                "motdepasse",
                Role.EXPERT.toString(),
                LocalDate.parse("2026-02-15"),
                "signature44.png",
                "certification44.png"
        );

        se.ajouterExpert(expert);

        // récupérer l'id inséré
        List<Expert> experts = se.recupererExpert();
        Expert inserted = experts.stream()
                .filter(ex -> email.equals(ex.getEmail()))
                .max(Comparator.comparingInt(Expert::getId))
                .orElse(null);

        assertNotNull(inserted, "Expert non inséré en @BeforeEach");
        idExpert = inserted.getId();
        assertTrue(idExpert > 0);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // ✅ nettoyer (si déjà supprimé, on ignore)
        try {
            Expert toDelete = new Expert();
            toDelete.setId(idExpert);
            se.supprimerExpert(toDelete);
        } catch (Exception ignored) {}
    }

    @Test
    void testModifierExpert() throws SQLException {
        Expert updated = new Expert(
                idExpert,
                "foulen_mod", "benfoulen_mod",
                99887766,
                "foulen.mod." + idExpert + "@gmail.com",
                "motdepasse_mod",
                Role.EXPERT.toString(),
                LocalDate.parse("2026-02-16"),
                "signature_mod.png",
                "certification_mod.png"
        );

        se.modifierExpert(updated);

        List<Expert> experts = se.recupererExpert();
        Expert found = experts.stream()
                .filter(ex -> ex.getId() == idExpert)
                .findFirst()
                .orElse(null);

        assertNotNull(found);
        assertEquals("foulen_mod", found.getNom());
        assertEquals("benfoulen_mod", found.getPrenom());
        assertEquals(99887766, found.getCin());
        assertEquals("motdepasse_mod", found.getMotDePasse());
        assertEquals("certification_mod.png", found.getCertification());
    }

    @Test
    void testEmailExiste() throws SQLException {
        // emailExiste vérifie dans utilisateurs pour role EXPERT
        List<Expert> experts = se.recupererExpert();
        Expert found = experts.stream().filter(ex -> ex.getId() == idExpert).findFirst().orElse(null);
        assertNotNull(found);

        assertTrue(se.emailExiste(found.getEmail()));
        assertFalse(se.emailExiste("email.inexistant." + System.currentTimeMillis() + "@gmail.com"));
    }

    @Test
    void testModifierMotDePasseParEmail() throws SQLException {
        List<Expert> experts = se.recupererExpert();
        Expert found = experts.stream().filter(ex -> ex.getId() == idExpert).findFirst().orElse(null);
        assertNotNull(found);

        String newPwd = "NEW_PWD_" + System.currentTimeMillis();
        se.modifierMotDePasseParEmail(found.getEmail(), newPwd);

        List<Expert> experts2 = se.recupererExpert();
        Expert found2 = experts2.stream().filter(ex -> ex.getId() == idExpert).findFirst().orElse(null);
        assertNotNull(found2);
        assertEquals(newPwd, found2.getMotDePasse());
    }

    @Test
    void testSupprimerExpert() throws SQLException {
        Expert toDelete = new Expert();
        toDelete.setId(idExpert);

        se.supprimerExpert(toDelete);

        List<Expert> experts = se.recupererExpert();
        assertFalse(experts.stream().anyMatch(ex -> ex.getId() == idExpert));

        // ✅ pour éviter que @AfterEach réessaie de supprimer
        idExpert = -1;
    }
}*/
