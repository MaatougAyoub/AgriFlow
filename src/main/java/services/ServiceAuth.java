/*C’est le service qui :

        vérifie email + motDePasse dans utilisateurs
        récupère le rôle
        récupère les infos spécifiques dans la table correspondante (agriculteurs / experts / admins)
        renvoie tout dans une Map (simple à afficher)*/
package services;

import entities.Role;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ServiceAuth {

    public Map<String, Object> loginAndFetchProfile(String email, String motDePasse) throws Exception {
        Connection cnx = MyDatabase.getInstance().getConnection();

        // 1) vérifier utilisateurs
        String sql = "SELECT * FROM utilisateurs WHERE email = ? AND motDePasse = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, motDePasse);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Map<String, Object> data = new HashMap<>();
                int id = rs.getInt("id");
                String roleStr = rs.getString("role");

                data.put("id", id);
                data.put("nom", rs.getString("nom"));
                data.put("prenom", rs.getString("prenom"));
                data.put("cin", rs.getInt("cin"));
                data.put("email", rs.getString("email"));
                data.put("motDePasse", rs.getString("motDePasse"));
                data.put("role", roleStr);
                data.put("dateCreation", rs.getDate("dateCreation"));
                data.put("signature", rs.getString("signature"));

                // 2) infos spécifiques selon rôle
                Role role = Role.valueOf(roleStr);
                if (role == Role.AGRICULTEUR) {
                    fillAgriculteur(cnx, id, data);
                } else if (role == Role.EXPERT) {
                    fillExpert(cnx, id, data);
                } else if (role == Role.ADMIN) {
                    fillAdmin(cnx, id, data);
                }
                return data;
            }
        }
    }

    private void fillAgriculteur(Connection cnx, int id, Map<String, Object> data) throws Exception {
        String sql = "SELECT adresse, carte_pro, parcelles FROM agriculteurs WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.put("adresse", rs.getString("adresse"));
                    data.put("carte_pro", rs.getString("carte_pro"));
                    data.put("parcelles", rs.getString("parcelles"));
                }
            }
        }
    }

    private void fillExpert(Connection cnx, int id, Map<String, Object> data) throws Exception {
        String sql = "SELECT certification FROM experts WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.put("certification", rs.getString("certification"));
                }
            }
        }
    }

    private void fillAdmin(Connection cnx, int id, Map<String, Object> data) throws Exception {
        String sql = "SELECT revenu FROM admins WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.put("revenu", rs.getDouble("revenu"));
                }
            }
        }
    }
}