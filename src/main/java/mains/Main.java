package mains;

import entities.*;
import services.ServiceAdmin;
import services.ServiceAgriculteur;
import services.ServiceExpert;
import services.ServiceReclamation;
import utils.MyDatabase;

import java.time.LocalDate;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        MyDatabase db = MyDatabase.getInstance();
        ServiceExpert serviceExpert = new ServiceExpert();
        Expert expert = new Expert("foulen", "benfoulen", 11223344, "foulen@gmail.com", "motdepasse", Role.EXPERT.toString(), LocalDate.parse("2026-02-06"),"signature2.png", "certification.pnj");
        //Expert expert2 = new Expert("ayoub", "ben Sami Maatoug", 11223345, "ayoub.maatoug@ipeib.ucar@gmail.tn", "motdepasse11111", Role.EXPERT.toString(), LocalDate.parse("2026-02-06"),"signature2.png", "certification.pnj");

        ServiceAgriculteur serviceAgriculteur = new ServiceAgriculteur();


/*       try {
            serviceExpert.ajouterExpert(expert2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/
        //Expert expert2 = new Expert(27,"ayoub->adam", "ben Sami Maatoug", 11223345, "ayoub.maatoug@ipeib.ucar@gmail.tn", "motdepasse11111", Role.EXPERT.toString(), LocalDate.parse("2026-02-06"),"signatureAdam.png", "certification.pnj");

       /* try {
            serviceExpert.modifierExpert(expert2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/

        /*try {
            serviceExpert.supprimerExpert(expert2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/
/*        try {
            System.out.println(serviceExpert.recupererExpert());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/

        //________________________________________________________________________________________________________________________

        Agriculteur agri1 = new Agriculteur("adam", "Maatoug", 99998888, "adam@gmail.com", "pwAdam", Role.AGRICULTEUR.toString(), LocalDate.parse("2026-02-07"),"signatureAdam.png", "cartePro.pnj", "Adresse", "liste des parcelles");
       //agri1.setId(24);

        Agriculteur agri2 = new Agriculteur(23,"anas", "Maatoug", 99998888, "anas@gmail.com", "pwAnas", Role.AGRICULTEUR.toString(), LocalDate.parse("2026-02-07"),"signatureAdam.png", "cartePro.pnj", "Adresse:RueAliDouagi", "liste des parcelles");

/*
        try {
            serviceAgriculteur.ajouterAgriculteur(agri1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
*/


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

/*        try {
            serviceAgriculteur.supprimerAgriculteur(agri1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/
        //________________________________________________________________________________________________________________________
        ServiceAdmin serviceAdmin = new ServiceAdmin();
        Admin admin1 = new Admin("Ayoub", "Maatoug", 11429920, "maatougayoub7@gmail.com", "pwayoub", Role.ADMIN.toString(), LocalDate.parse("2026-02-07"), "Signatre Ayoub.png", 200.58);
/*        try {
            serviceAdmin.ajouterAdmin(admin1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/

        Admin admin2 = new Admin(33,"Ayoub222", "Maatoug", 11429920, "maatougayoub7@gmail.com", "pwayoub*004", Role.ADMIN.toString(), LocalDate.parse("2026-02-08"), "Signatre Ayoub.png", 200.58);


        /*try {
            serviceAdmin.modifierAdmin(admin2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/

/*        try {
            System.out.println(serviceAdmin.recupererAdmin());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/

/*        try {
            serviceAdmin.supprimerAdmin(admin2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/

        ServiceReclamation serviceReclamation = new ServiceReclamation();
        Reclamation r1 = new Reclamation(34, Categorie.TECHNIQUE, "reclamation 11", "Voici une réclamation pur le test1" );
        Reclamation r2 = new Reclamation(34, Categorie.AUTRE, "reclamation 22222", "Voici une réclamation pur le test222222" );

/*        try {
            serviceReclamation.ajouterReclamation(r1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/
        Reclamation r3 = new Reclamation(4,34, Categorie.AUTRE, "reclamation 333333", "Voici une réclamation pur le test3333333" );

/*        try {
            serviceReclamation.modifierReclamation(r3);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/

  /*     try {
            System.out.println(serviceReclamation.recupererReclamation());;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/

        /*try {
            serviceReclamation.supprimerReclamation(r3);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/
    }
}