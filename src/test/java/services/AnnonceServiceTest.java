package services;

import entities.Annonce;
import entities.StatutAnnonce;
import entities.TypeAnnonce;
import entities.User;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnonceServiceTest {

    @Test
    void testAjouterAnnonce() {
        ServiceAnnonce annonceService = new ServiceAnnonce();
        UserService userService = new UserService();

        User user = new User();
        user.setNom("Test");
        user.setPrenom("User");
        user.setEmail("test." + System.currentTimeMillis() + "@example.com");
        user.setTelephone("00000000");
        user.setAdresse("Test");
        user.setRegion("Test");

        assertDoesNotThrow(() -> userService.ajouter(user));

        Annonce annonce = new Annonce();
        annonce.setTitre("Annonce test");
        annonce.setDescription("Annonce de test");
        annonce.setType(TypeAnnonce.LOCATION);
        annonce.setStatut(StatutAnnonce.DISPONIBLE);
        annonce.setPrix(100);
        annonce.setUnitePrix("jour");
        annonce.setProprietaire(user);

        assertDoesNotThrow(() -> annonceService.ajouter(annonce));
        assertTrue(annonce.getId() > 0);

        try {
            if (annonce.getId() > 0) {
                annonceService.supprimer(annonce);
            }
            if (user.getId() > 0) {
                userService.supprimer(user);
            }
        } catch (SQLException e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }
}
