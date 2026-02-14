package entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnonceTest {

    @Test
    void prixFormateDefaultUnitIsJour() {
        Annonce annonce = new Annonce();
        annonce.setPrix(100);
        annonce.setUnitePrix(null);

        String formatted = annonce.getPrixFormate();
        assertTrue(formatted.endsWith(" DT/jour"));
    }
}
