package tests;

import entities.Agriculteur;
import entities.Expert;
import entities.Role;
import services.ServiceAgriculteur;
import services.ServiceExpert;
import utils.MyDatabase;

import java.sql.SQLException;
import java.time.LocalDate;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        MyDatabase db = MyDatabase.getInstance();
        ServiceExpert serviceExpert = new ServiceExpert();
        Expert expert = new Expert("foulen", "benfoulen", 11223344, "foulen@gmail.com", "motdepasse", Role.EXPERT.toString(), LocalDate.parse("2026-02-06"),"signature2.png", "certification.pnj");
        Expert expert2 = new Expert("ayoub", "ben Sami Maatoug", 11223345, "ayoub.maatoug@ipeib.ucar@gmail.tn", "motdepasse11111", Role.EXPERT.toString(), LocalDate.parse("2026-02-06"),"signature2.png", "certification.pnj");

        ServiceAgriculteur serviceAgriculteur = new ServiceAgriculteur();


/*        try {
            serviceExpert.ajouterExpert(expert2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/

/*        try {
            serviceExpert.modifierExpert(expert2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/
/*        try {
            serviceExpert.supprimerExpert(expert2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/
/*        try {
            System.out.println(serviceExpert.recupererExpert());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/

        //______________________________________________________________________________

        Agriculteur agri1 = new Agriculteur("adam", "Maatoug", 99998888, "adam@gmail.com", "pwAdam", Role.AGRICULTEUR.toString(), LocalDate.parse("2026-02-07"),"signatureAdam.png", "cartePro.pnj", "Adresse", "liste des parcelles");
       //agri1.setId(24);

        Agriculteur agri2 = new Agriculteur(23,"anas", "Maatoug", 99998888, "anas@gmail.com", "pwAnas", Role.AGRICULTEUR.toString(), LocalDate.parse("2026-02-07"),"signatureAdam.png", "cartePro.pnj", "Adresse:RueAliDouagi", "liste des parcelles");
        /*try {
            serviceAgriculteur.ajouterAgriculteur(agri1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/


/*        try {
            serviceAgriculteur.modifierAgriculteur(agri2);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }*/

/*        try{
            System.out.println(serviceAgriculteur.recupererAgriculteurs());
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }*/

        try {
            serviceAgriculteur.supprimerAgriculteur(agri11);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }
}