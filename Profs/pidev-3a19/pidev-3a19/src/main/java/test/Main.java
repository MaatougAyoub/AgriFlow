package test;

import Entites.personne;
import Services.PersonneService;
import Utils.Mydatabase;

public class Main {


    public static void main(String[] args) {
        Mydatabase.getInstance();

        PersonneService ps=new PersonneService();

        personne p=new personne(6,"aahmed2","salahhh",10);

       // ps.ajouterPersonne(p);
       // ps.supprimerPersonne(1);
        //ps.supprimerPersonne(2);


        ps.modifierPersonne(p);
        System.out.println( ps.afficherPersonne());
    }
}
