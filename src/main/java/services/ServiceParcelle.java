package services;

import utils.MyConnection;
import java.sql.*;

public class ServiceParcelle {
    private final Connection connection;

    public ServiceParcelle() {
        connection = MyConnection.getInstance().getConnection();
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