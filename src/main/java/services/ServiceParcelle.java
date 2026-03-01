package services;

import entities.Parcelle;
import utils.MyDatabase;

//import utils.MyConnection;
import utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceParcelle implements IServiceB<Parcelle> {

    private final Connection connection;

    public ServiceParcelle() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Parcelle p) throws SQLException {

        String req = "INSERT INTO parcelle(agriculteur_id, nom, superficie, type_terre, localisation) "
                + "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(req);

        ps.setInt(1, p.getAgriculteurId());
        ps.setString(2, p.getNom());
        ps.setDouble(3, p.getSuperficie());
        ps.setString(4, p.getTypeTerre().name());
        ps.setString(5, p.getLocalisation());

        ps.executeUpdate();
        System.out.println("parcelle ajoutée");
    }

    @Override
    public void modifier(Parcelle p) throws SQLException {

        String req = "UPDATE parcelle SET agriculteur_id=?, nom=?, superficie=?, type_terre=?, localisation=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);

        ps.setInt(1, p.getAgriculteurId());
        ps.setString(2, p.getNom());
        ps.setDouble(3, p.getSuperficie());
        ps.setString(4, p.getTypeTerre().name());
        ps.setString(5, p.getLocalisation());
        ps.setInt(6, p.getId());

        ps.executeUpdate();
        System.out.println("parcelle modifiée");
    }

    @Override
    public void supprimer(Parcelle p) throws SQLException {

        String req = "DELETE FROM parcelle WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, p.getId());
        ps.executeUpdate();

        System.out.println("parcelle supprimée");
    }

    @Override
    public List<Parcelle> recuperer() throws SQLException {

        List<Parcelle> parcelles = new ArrayList<>();

        String req = "SELECT * FROM parcelle";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Parcelle p = new Parcelle(
                    rs.getInt("id"),
                    rs.getInt("agriculteur_id"),
                    rs.getString("nom"),
                    rs.getDouble("superficie"),
                    Parcelle.TypeTerre.valueOf(rs.getString("type_terre")),
                    rs.getString("localisation"),
                    rs.getTimestamp("date_creation")
            );
            parcelles.add(p);
        }

        return parcelles;
    }

    // Met à jour la table parcelle
    public boolean modifierLocalisation(int idParcelle, String nouvelleLocalisation) {
        String sql = "UPDATE parcelle SET localisation = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, nouvelleLocalisation);
            pst.setInt(2, idParcelle);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String recupererLocalisation(int idParcelle) throws SQLException {
        String sql = "SELECT localisation FROM parcelle WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, idParcelle);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("localisation"); // Retourne "52.839,-1.889"
            }
        }
        return null;
    }

}
