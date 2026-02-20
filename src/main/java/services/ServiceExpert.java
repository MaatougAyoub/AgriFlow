package services;

import entities.Expert;
import entities.Role;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static entities.Role.EXPERT;

public class ServiceExpert implements IServiceExpert <Expert> {
    private Connection connection;

    public ServiceExpert() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouterExpert(Expert expert) throws SQLException {
        String sql = "INSERT INTO utilisateurs (nom, prenom, cin, email, motDePasse, role, dateCreation, signature, certification) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, expert.getNom());
            ps.setString(2, expert.getPrenom());
            ps.setInt(3, expert.getCin());
            ps.setString(4, expert.getEmail());
            ps.setString(5, expert.getMotDePasse());
            ps.setString(6, Role.EXPERT.toString());
            ps.setObject(7, expert.getDateCreation());
            ps.setString(8, expert.getSignature());
            ps.setString(9, expert.getCertification());
            ps.executeUpdate();
        }
        System.out.println("Expert ajoute avec succés!!! ✅");
    }

    public void modifierExpert(Expert expert) throws SQLException {
        String reqUser = "UPDATE utilisateurs SET nom=?, prenom=?, cin=?, email=?, motDePasse=?, role=?, dateCreation=?, signature=?, certification=? WHERE id=?";
        try (PreparedStatement psUser = connection.prepareStatement(reqUser)) {
            psUser.setString(1, expert.getNom());
            psUser.setString(2, expert.getPrenom());
            psUser.setInt(3, expert.getCin());
            psUser.setString(4, expert.getEmail());
            psUser.setString(5, expert.getMotDePasse());
            psUser.setString(6, Role.EXPERT.toString());
            psUser.setObject(7, expert.getDateCreation());
            psUser.setString(8, expert.getSignature());
            psUser.setString(9, expert.getCertification());
            psUser.setInt(10, expert.getId());
            psUser.executeUpdate();
        }

        System.out.println("Expert modifié avec succès!!✅");
    }
    @Override
    public void supprimerExpert (Expert expert) throws SQLException
    {
        // 1️⃣ Supprimer de la table `utilisateurs`
        String reqUser = "DELETE FROM `utilisateurs` WHERE `id` = ?";
        PreparedStatement psUser = connection.prepareStatement(reqUser);
        psUser.setInt(1, expert.getId());
        psUser.executeUpdate();
        psUser.close();

        System.out.println("Expert supprimé avec succès!!✅");
    }

    @Override
    public List<Expert> recupererExpert() throws SQLException {
        List<Expert> expertsList = new ArrayList<>();
        String req = "SELECT * FROM utilisateurs WHERE role = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, Role.EXPERT.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idE = rs.getInt("id");
                    String nomE = rs.getString("nom");
                    String prenomE = rs.getString("prenom");
                    int cinE = rs.getInt("cin");
                    String emailE = rs.getString("email");
                    String motDePasseE = rs.getString("motDePasse");
                    String roleE = rs.getString("role");
                    java.sql.Date sqlDate = rs.getDate("dateCreation");
                    java.time.LocalDate dateCreationE = (sqlDate != null) ? sqlDate.toLocalDate() : null;
                    String signatureE = rs.getString("signature");
                    String certificationE = rs.getString("certification");
                    Expert expert = new Expert(idE, nomE, prenomE, cinE, emailE, motDePasseE, roleE, dateCreationE, signatureE, certificationE);
                    expertsList.add(expert);
                }
            }
        }
        return expertsList;
    }
    public boolean emailExiste(String email) throws SQLException {
        Connection cnx = MyDatabase.getInstance().getConnection(); // ✅ récupérer une connexion active

        String sql = "SELECT 1 FROM utilisateurs WHERE email = ? AND role = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, Role.EXPERT.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void modifierMotDePasseParEmail(String email, String nouveauMotDePasse) throws SQLException {
        // 1) utilisateurs
        String sqlUser = "UPDATE utilisateurs SET motDePasse = ? WHERE email = ? AND role = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlUser)) {
            ps.setString(1, nouveauMotDePasse);
            ps.setString(2, email);
            ps.setString(3, Role.EXPERT.toString());
            ps.executeUpdate();
        }
    }


}






