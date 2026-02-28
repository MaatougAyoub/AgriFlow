import entities.Admin;
import entities.Role;
import org.junit.jupiter.api.*;
import services.ServiceAdmin;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminServiceTest {

    static ServiceAdmin sa;
    static int idAdmin;
    static String emailAdd;
    static String emailMod;
    static int cinAdd;
    static int cinMod;

    @BeforeAll
    static void setUp() {
        sa = new ServiceAdmin();

        String suffix = String.valueOf(System.currentTimeMillis());
        emailAdd = "admin.test." + suffix + "@gmail.com";
        emailMod = "admin.test.mod." + suffix + "@gmail.com";
        cinAdd = (int) (System.currentTimeMillis() % 1_000_000_000L);
        if (cinAdd < 10000000) cinAdd += 10000000;
        cinMod = cinAdd + 1;
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (idAdmin > 0) {
            Admin toDelete = new Admin();
            toDelete.setId(idAdmin);
            sa.supprimerAdmin(toDelete);
        }
    }

    @Test
    @Order(1)
    void testAjouterAdmin() throws SQLException {
        Admin admin = new Admin(
                "admin_test", "user_test",
                cinAdd,
                emailAdd,
                "pwd_test",
                Role.ADMIN.toString(),
                LocalDate.parse("2026-02-15"),
                "signature_admin.png",
                1500.0
        );

        sa.ajouterAdmin(admin);

        List<Admin> admins = sa.recupererAdmin();

        Admin inserted = admins.stream()
            .filter(a -> emailAdd.equals(a.getEmail()))
            .max(Comparator.comparingInt(Admin::getId))
                .orElse(null);

        assertNotNull(inserted, "Admin non inséré");
        idAdmin = inserted.getId();
        System.out.println("Inserted admin id = " + idAdmin);
        assertTrue(idAdmin > 0);
    }

    @Test
    @Order(2)
    void testModifierAdmin() throws SQLException {
        assertTrue(idAdmin > 0, "idAdmin non initialisé. Exécute testAjouterAdmin d'abord.");

        Admin updated = new Admin(
                idAdmin,
                "admin_test_mod", "user_test_mod",
            cinMod,
            emailMod,
                "pwd_mod",
                Role.ADMIN.toString(),
                LocalDate.parse("2026-02-16"),
                "signature_mod.png",
                2500.5
        );

        sa.modifierAdmin(updated);

        List<Admin> admins = sa.recupererAdmin();
        Admin found = admins.stream()
                .filter(a -> a.getId() == idAdmin)
                .findFirst()
                .orElse(null);

        assertNotNull(found, "Admin introuvable après modification");
        assertEquals("admin_test_mod", found.getNom());
        assertEquals("user_test_mod", found.getPrenom());
        assertEquals(cinMod, found.getCin());
        assertEquals(emailMod, found.getEmail());
        assertEquals("pwd_mod", found.getMotDePasse());
        assertEquals("signature_mod.png", found.getSignature());
        assertEquals(2500.5, found.getRevenus(), 0.0001);
    }

    @Test
    @Order(3)
    void testRecupererAdmin() throws SQLException {
        List<Admin> admins = sa.recupererAdmin();
        assertNotNull(admins);
        assertTrue(admins.size() > 0, "La liste des admins ne devrait pas être vide");
        assertTrue(admins.stream().anyMatch(a -> a.getId() == idAdmin),
                "L'admin inséré doit exister dans recupererAdmin()");
    }

    @Test
    @Order(4)
    void testSupprimerAdmin() throws SQLException {
        assertTrue(idAdmin > 0, "idAdmin non initialisé.");

        int deletedId = idAdmin;

        Admin toDelete = new Admin();
        toDelete.setId(idAdmin);

        sa.supprimerAdmin(toDelete);
        idAdmin = 0; // éviter que @AfterAll réessaie

        List<Admin> admins = sa.recupererAdmin();
        assertFalse(admins.stream().anyMatch(a -> a.getId() == deletedId),
            "L'admin n'a pas été supprimé");
    }
}
