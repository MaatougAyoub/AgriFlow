package Services;

import Entites.personne;
import Iservices.IpersonneServices;
import Utils.Mydatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonneService implements IpersonneServices {

    Connection con;
  public   PersonneService(){

        con= Mydatabase.getInstance().getConnextion();
    }
  /*  @Override
    public void ajouterPersonne(personne p) {

        String req="INSERT INTO personne (nom, prenom, age) VALUES ('ali', 'mohamed', 10)";

        try {
            Statement ste=con.createStatement();
            ste.executeUpdate(req);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

    @Override
    public void ajouterPersonne(personne p) {

        String req="INSERT INTO personne (nom, prenom, age) VALUES (?, ?, ?)";

        try {
            PreparedStatement ste=con.prepareStatement(req);
            ste.setString(1,p.getNom());
            ste.setString(2,p.getPrenom());
            ste.setInt(3,p.getAge());
            ste.executeUpdate();


            System.out.println("personne ajoute");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void modifierPersonne(personne p) {

        String req="UPDATE personne SET prenom=? WHERE id=?";

        try {
            PreparedStatement ste=con.prepareStatement(req);
            ste.setString(1, p.getPrenom());
            ste.setInt(2,p.getId());
            ste.executeUpdate();
            System.out.println("user is updated");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void supprimerPersonne(int id) {

        String req="DELETE FROM personne WHERE id= ?";
        try {
            PreparedStatement ste=con.prepareStatement(req);
            ste.setInt(1,id);
            ste.executeUpdate();
            System.out.println("user with the id = "+id+" is deleted");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<personne> afficherPersonne()  {
        List<personne> personnes=new ArrayList<>();
        String req="SELECT * FROM personne";
        try {
            Statement ste=con.createStatement();
            ResultSet rs= ste.executeQuery(req);

            while (rs.next()){
                personne p=new personne(rs.getInt("id"),rs.getString("nom"), rs.getString("prenom"),rs.getInt("age") );

                personnes.add(p);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return personnes;
    }
}
