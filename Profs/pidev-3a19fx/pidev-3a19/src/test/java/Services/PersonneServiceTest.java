package Services;

import Entites.personne;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonneServiceTest {

    @Test
    void ajouterPersonne() {

      personne p =new personne("mohamed","ali",25);

      PersonneService ps=new PersonneService();
      ps.ajouterPersonne(p);
      List<personne> personnes=new ArrayList<>();
      personnes.addAll(ps.afficherPersonne());
     boolean check= personnes.stream().anyMatch(x->x.getPrenom().equals("ali")&&x.getNom().equals("mohamed"));

      assertTrue(check);
    }

    @Test
    void modifierPersonne() {
    }

    @Test
    void supprimerPersonne() {
    }

    @Test
    void afficherPersonne() {
    }
}