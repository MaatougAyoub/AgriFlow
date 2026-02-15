package entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReservationTest {

    @Test
    void calculerPrixTotalLocationUsesDuration() {
        Annonce annonce = new Annonce();
        annonce.setType(TypeAnnonce.LOCATION);
        annonce.setPrix(50);

        Reservation reservation = new Reservation();
        reservation.setAnnonce(annonce);
        reservation.setDateDebut(LocalDate.of(2026, 2, 1));
        reservation.setDateFin(LocalDate.of(2026, 2, 3));

        reservation.calculerPrixTotal();

        assertEquals(150.0, reservation.getPrixTotal(), 0.0001);
    }

    @Test
    void calculerPrixTotalVenteUsesQuantity() {
        Annonce annonce = new Annonce();
        annonce.setType(TypeAnnonce.VENTE);
        annonce.setPrix(20);

        Reservation reservation = new Reservation();
        reservation.setAnnonce(annonce);
        reservation.setQuantite(4);

        reservation.calculerPrixTotal();

        assertEquals(80.0, reservation.getPrixTotal(), 0.0001);
    }
}
