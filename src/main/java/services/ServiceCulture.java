package services;

import entities.Culture;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCulture implements IService<Culture> {

    private  Connection connection;

    public ServiceCulture() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Culture c) throws SQLException {

        String req = "INSERT INTO culture(parcelle_id, proprietaire_id, nom, type_culture, " +
                "superficie, etat, date_recolte, recolte_estime) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(req);

        ps.setInt(1, c.getParcelleId());
        ps.setInt(2, c.getProprietaireId());
        ps.setString(3, c.getNom());
        ps.setString(4, c.getTypeCulture().name());
        ps.setDouble(5, c.getSuperficie());
        ps.setString(6, c.getEtat().name());
        ps.setDate(7, c.getDateRecolte());
        ps.setDouble(8, c.getRecolteEstime());

        ps.executeUpdate();

    }

    @Override
    public void modifier(Culture c) throws SQLException {
        String req = "UPDATE culture SET parcelle_id=?, proprietaire_id=?, nom=?, type_culture=?," +
                " superficie=?, etat=?, date_recolte=?, recolte_estime=? " +
                "WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, c.getParcelleId());
        ps.setInt(2, c.getProprietaireId());
        ps.setString(3, c.getNom());
        ps.setString(4, c.getTypeCulture().name());
        ps.setDouble(5, c.getSuperficie());
        ps.setString(6, c.getEtat().name());
        ps.setDate(7, c.getDateRecolte());
        ps.setDouble(8, c.getRecolteEstime());
        ps.setInt(9, c.getId());

        ps.executeUpdate();
    }

    @Override
    public void supprimer(Culture c) throws SQLException {
        String req = "DELETE FROM culture WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, c.getId());
        ps.executeUpdate();
        System.out.println("culture supprimée");
    }

    @Override
    public List<Culture> recuperer() throws SQLException {
        List<Culture> cultures = new ArrayList<>();
        String req = "SELECT * FROM culture";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {

            Culture culture = new Culture(
                    rs.getInt("id"),
                    rs.getInt("parcelle_id"),
                    rs.getInt("proprietaire_id"),
                    rs.getString("nom"),
                    Culture.TypeCulture.valueOf(rs.getString("type_culture")),
                    rs.getDouble("superficie"),
                    Culture.Etat.valueOf(rs.getString("etat")),
                    rs.getDate("date_recolte"),
                    rs.getDouble("recolte_estime"),
                    rs.getTimestamp("date_creation")
            );
            cultures.add(culture);
        }
        return cultures;
    }
}
