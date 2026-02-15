import entities.Culture;
import entities.Parcelle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import services.ServiceParcelle;

import java.time.LocalDateTime;
import java.util.stream.*;
import java.util.List;
import java.sql.Timestamp;


import java.sql.SQLException;


public class CultureServiceTest {
    static ServiceParcelle sp;
    @BeforeAll
 static void setup() {
     sp = new ServiceParcelle();
 }
 @Test
    void testAjoutCulture() throws SQLException {
     Timestamp ts = Timestamp.valueOf(LocalDateTime.of(2022, 3, 2, 0, 0));

     Parcelle p =new Parcelle(2,33,"p1",500,
            Parcelle.TypeTerre.SABLEUSE,"Tunis",ts);
    sp.ajouter(p);

    List<Parcelle> parcelles = sp.recuperer();
     assertTrue(parcelles.stream().anyMatch(
             par->par.getNom().equals("p1")));
    }



}
