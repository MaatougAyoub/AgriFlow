import entities.Agriculteur;
import entities.Role;
import org.junit.jupiter.api.*;
import services.ServiceAgriculteur;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AgriculteurServiceTest {

    static ServiceAgriculteur sa;
    static int idAgriculteur;

    @BeforeAll
    static void setUp() {
        sa = new ServiceAgriculteur();
    }

    @Test
    @Order(1)
    void testAjouterAgriculteur() throws SQLException {
        Agriculteur a = new Agriculteur(
                "agri_test", "user_test",
                11223344,
                "agri.test@gmail.com",
                "pwd_test",
                Role.AGRICULTEUR.toString(),
                LocalDate.parse("2026-02-15"),
                "signature_agri.png",
                "carte_pro_agri.png",
                "Tunis",
                "Parcelle A, Parcelle B"
        );

        sa.ajouterAgriculteur(a);

        List<Agriculteur> agris = sa.recupererAgriculteurs();

        Agriculteur inserted = agris.stream()
                .filter(x -> "agri_test".equals(x.getNom()) && "agri.test@gmail.com".equals(x.getEmail()))
                .max(Comparator.comparingInt(Agriculteur::getId))
                .orElse(null);

        assertNotNull(inserted, "Agriculteur non inséré");
        idAgriculteur = inserted.getId();
        System.out.println("Inserted agriculteur id = " + idAgriculteur);
        assertTrue(idAgriculteur > 0);
    }

    @Test
    @Order(2)
    void testModifierAgriculteur() throws SQLException {
        assertTrue(idAgriculteur > 0, "idAgriculteur non initialisé. Exécute testAjouterAgriculteur d'abord.");

        Agriculteur updated = new Agriculteur(
                idAgriculteur,
                "agri_test_mod", "user_test_mod",
                55667788,
                "agri.test.mod@gmail.com",
                "pwd_mod",
                Role.AGRICULTEUR.toString(),
                LocalDate.parse("2026-02-16"),
                "signature_mod.png",
                "carte_pro_mod.png",
                "Bizerte",
                "Parcelle X"
        );

        sa.modifierAgriculteur(updated);

        List<Agriculteur> agris = sa.recupererAgriculteurs();
        Agriculteur found = agris.stream()
                .filter(x -> x.getId() == idAgriculteur)
                .findFirst()
                .orElse(null);

        assertNotNull(found, "Agriculteur introuvable après modification");
        assertEquals("agri_test_mod", found.getNom());
        assertEquals("user_test_mod", found.getPrenom());
        assertEquals(55667788, found.getCin());
        assertEquals("agri.test.mod@gmail.com", found.getEmail());
        assertEquals("pwd_mod", found.getMotDePasse());
        assertEquals("signature_mod.png", found.getSignature());
        assertEquals("carte_pro_mod.png", found.getCarte_pro());
        assertEquals("Bizerte", found.getAdresse());
        assertEquals("Parcelle X", found.getParcelles());
    }

    @Test
    @Order(3)
    void testEmailExiste() throws SQLException {
        assertTrue(sa.emailExiste("agri.test.mod@gmail.com"), "emailExiste devrait retourner true");
        assertFalse(sa.emailExiste("email.inexistant." + System.currentTimeMillis() + "@gmail.com"),
                "emailExiste devrait retourner false");
    }

    @Test
    @Order(4)
    void testModifierMotDePasseParEmail() throws SQLException {
        String newPwd = "NEW_PWD_AGRI_2026";
        sa.modifierMotDePasseParEmail("agri.test.mod@gmail.com", newPwd);

        List<Agriculteur> agris = sa.recupererAgriculteurs();
        Agriculteur found = agris.stream()
                .filter(x -> x.getId() == idAgriculteur)
                .findFirst()
                .orElse(null);

        assertNotNull(found);
        assertEquals(newPwd, found.getMotDePasse(), "Le mot de passe dans agriculteurs n'a pas été mis à jour");
    }

    @Test
    @Order(5)
    void testSupprimerAgriculteur() throws SQLException {
        assertTrue(idAgriculteur > 0, "idAgriculteur non initialisé.");

        Agriculteur toDelete = new Agriculteur();
        toDelete.setId(idAgriculteur);

        sa.supprimerAgriculteur(toDelete);

        List<Agriculteur> agris = sa.recupererAgriculteurs();
        assertFalse(agris.stream().anyMatch(x -> x.getId() == idAgriculteur),
                "L'agriculteur n'a pas été supprimé de la table agriculteurs");
    }
}