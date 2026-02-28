/*C’est le service qui :

    vérifie email + motDePasse dans utilisateurs
    récupère le rôle
    récupère les infos spécifiques (colonnes NULLABLE dans utilisateurs)
    renvoie tout dans une Map (simple à afficher)*/
package services;

import entities.Role;
import utils.MyDatabase;
import utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ServiceAuth {

    public Map<String, Object> loginAndFetchProfile(String email, String motDePasse) throws Exception {
        Connection cnx = MyDatabase.getInstance().getConnection();

        // 1) Récupérer l'utilisateur par email (on vérifie ensuite le mot de passe haché)
        String sql = "SELECT * FROM utilisateurs WHERE email = ? AND (UPPER(TRIM(role)) = 'ADMIN' OR UPPER(TRIM(verification_status)) = 'APPROVED')";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String storedHash = rs.getString("motDePasse");
                if (storedHash == null || !PasswordUtils.verifyPassword(motDePasse, storedHash)) {
                    return null; // mot de passe invalide
                }

                Map<String, Object> data = new HashMap<>();
                int id = rs.getInt("id");
                String roleStr = rs.getString("role");

                data.put("id", id);
                data.put("nom", rs.getString("nom"));
                data.put("prenom", rs.getString("prenom"));
                data.put("cin", rs.getInt("cin"));
                data.put("email", rs.getString("email"));
                data.put("motDePasse", storedHash);
                data.put("role", roleStr);
                data.put("dateCreation", rs.getDate("dateCreation"));
                data.put("signature", rs.getString("signature"));
                data.put("verification_status", rs.getString("verification_status"));
                data.put("verification_reason", rs.getString("verification_reason"));
                Object scoreObj = rs.getObject("verification_score");
                data.put("verification_score", scoreObj == null ? null : rs.getDouble("verification_score"));

                // champs spécifiques (peuvent être NULL)
                data.put("adresse", rs.getString("adresse"));
                data.put("carte_pro", rs.getString("carte_pro"));
                data.put("parcelles", rs.getString("parcelles"));
                data.put("certification", rs.getString("certification"));

                Object revObj = rs.getObject("revenu");
                data.put("revenu", revObj == null ? null : rs.getDouble("revenu"));
                return data;
            }
        }
    }
}