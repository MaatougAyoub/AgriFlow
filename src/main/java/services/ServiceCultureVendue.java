package services;

import entities.CultureVendue;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCultureVendue implements IService<CultureVendue> {

    private Connection connection;

    public ServiceCultureVendue() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(CultureVendue v) throws SQLException {

        String req = "INSERT INTO culture_vendue(id_culture, id_acheteur, date_vente, prix_vente) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(req);

        ps.setInt(1, v.getIdCulture());

        if (v.getIdAcheteur() != null)
            ps.setInt(2, v.getIdAcheteur());
        else
            ps.setNull(2, Types.INTEGER);

        if (v.getDateVente() != null)
            ps.setDate(3, v.getDateVente());
        else
            ps.setNull(3, Types.DATE);

        ps.setDouble(4, v.getPrixVente());

        ps.executeUpdate();
        System.out.println("culture mise en vente");
    }

    @Override
    public void modifier(CultureVendue v) throws SQLException {

        String req = "UPDATE culture_vendue SET id_culture=?, id_acheteur=?, date_vente=?, prix_vente=? WHERE id_vente=?";
        PreparedStatement ps = connection.prepareStatement(req);

        ps.setInt(1, v.getIdCulture());

        if (v.getIdAcheteur() != null)
            ps.setInt(2, v.getIdAcheteur());
        else
            ps.setNull(2, Types.INTEGER);

        if (v.getDateVente() != null)
            ps.setDate(3, v.getDateVente());
        else
            ps.setNull(3, Types.DATE);

        ps.setDouble(4, v.getPrixVente());
        ps.setInt(5, v.getIdVente());

        ps.executeUpdate();
        System.out.println("vente modifiée");
    }

    @Override
    public void supprimer(CultureVendue v) throws SQLException {

        String req = "DELETE FROM culture_vendue WHERE id_vente=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, v.getIdVente());
        ps.executeUpdate();

        System.out.println("vente supprimée");
    }

    @Override
    public List<CultureVendue> recuperer() throws SQLException {

        List<CultureVendue> ventes = new ArrayList<>();

        String req = "SELECT * FROM culture_vendue";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {

            Integer acheteur = (rs.getObject("id_acheteur") == null) ? null : rs.getInt("id_acheteur");

            CultureVendue v = new CultureVendue(
                    rs.getInt("id_vente"),
                    rs.getInt("id_culture"),
                    acheteur,
                    rs.getDate("date_vente"),
                    rs.getTimestamp("date_publication"),
                    rs.getDouble("prix_vente")
            );

            ventes.add(v);
        }

        return ventes;
    }
}
