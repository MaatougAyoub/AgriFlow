package services;

import entities.Role;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class ServiceProfil {

    public void updateProfil(Map<String, Object> data) throws SQLException {
        Connection cnx = MyDatabase.getInstance().getConnection();

        int id = (int) data.get("id");
        Role role = Role.valueOf(String.valueOf(data.get("role")));

        // 1) Update utilisateurs (commun)
        String sqlUser = "UPDATE utilisateurs SET nom=?, prenom=?, email=?, signature=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sqlUser)) {
            ps.setString(1, String.valueOf(data.get("nom")));
            ps.setString(2, String.valueOf(data.get("prenom")));
            ps.setString(3, String.valueOf(data.get("email")));
            ps.setString(4, String.valueOf(data.get("signature")));
            ps.setInt(5, id);
            ps.executeUpdate();
        }

        // 2) Update spécifique selon rôle (dans la même table `utilisateurs`)
        if (role == Role.AGRICULTEUR) {
            String sqlAgri = "UPDATE utilisateurs SET adresse=?, parcelles=?, carte_pro=? WHERE id=?";
            try (PreparedStatement ps = cnx.prepareStatement(sqlAgri)) {
                ps.setString(1, data.get("adresse") == null ? null : String.valueOf(data.get("adresse")));
                ps.setString(2, data.get("parcelles") == null ? null : String.valueOf(data.get("parcelles")));
                ps.setString(3, data.get("carte_pro") == null ? null : String.valueOf(data.get("carte_pro")));
                ps.setInt(4, id);
                ps.executeUpdate();
            }
        } else if (role == Role.EXPERT) {
            String sqlExpert = "UPDATE utilisateurs SET certification=? WHERE id=?";
            try (PreparedStatement ps = cnx.prepareStatement(sqlExpert)) {
                ps.setString(1, data.get("certification") == null ? null : String.valueOf(data.get("certification")));
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        } else if (role == Role.ADMIN) {
            String sqlAdmin = "UPDATE utilisateurs SET revenu=? WHERE id=?";
            try (PreparedStatement ps = cnx.prepareStatement(sqlAdmin)) {
                Double revenu = data.get("revenu") == null ? null : Double.valueOf(String.valueOf(data.get("revenu")));
                if (revenu == null) ps.setNull(1, java.sql.Types.DOUBLE);
                else ps.setDouble(1, revenu);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        }
    }
}