/*
package services;

import entities.Utilisateur;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtilisateur {
    private final Connection connection;

    public ServiceUtilisateur() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    public List<Utilisateur> recupererTous() throws SQLException {
        List<Utilisateur> list = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, cin, email, motDePasse, role, dateCreation, signature FROM utilisateurs ORDER BY id DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int id = rs.getInt("id");
            String nom = rs.getString("nom");
            String prenom = rs.getString("prenom");
            int cin = rs.getInt("cin");
            String email = rs.getString("email");
            String motDePasse = rs.getString("motDePasse");
            String role = rs.getString("role");

            Date dc = rs.getDate("dateCreation");
            LocalDate dateCreation = (dc != null) ? dc.toLocalDate() : null;

            String signature = rs.getString("signature");

            list.add(new Utilisateur(id, nom, prenom, cin, email, motDePasse, role, dateCreation, signature));
        }

        rs.close();
        st.close();
        return list;
    }

    public void supprimerParId(int id) throws SQLException {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }
}*/
