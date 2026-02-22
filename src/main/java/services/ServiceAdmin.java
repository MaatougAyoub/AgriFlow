package services;

import entities.Admin;
import entities.Role;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceAdmin implements IServiceAdmin<Admin>{
    private Connection connection;
    public ServiceAdmin() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouterAdmin (Admin admin) throws SQLException{
        String sql = "INSERT INTO utilisateurs (nom, prenom, cin, email, motDePasse, role, dateCreation, signature, revenu, verification_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, admin.getNom());
            ps.setString(2, admin.getPrenom());
            ps.setInt(3, admin.getCin());
            ps.setString(4, admin.getEmail());
            ps.setString(5, admin.getMotDePasse());
            ps.setString(6, Role.ADMIN.toString());
            ps.setObject(7, admin.getDateCreation());
            ps.setString(8, admin.getSignature());
            if (admin.getRevenus() == null) ps.setNull(9, Types.DOUBLE);
            else ps.setDouble(9, admin.getRevenus());
            ps.setString(10, "APPROVED");
            ps.executeUpdate();
        }
        System.out.println("Admin ajoute avec succés!!! ✅");
    }

    @Override
    public void modifierAdmin (Admin admin) throws SQLException{
        String reqUser = "UPDATE utilisateurs SET nom=?, prenom=?, cin=?, email=?, motDePasse=?, role=?, dateCreation=?, signature=?, revenu=? WHERE id=?";
        try (PreparedStatement psUser = connection.prepareStatement(reqUser)) {
            psUser.setString(1, admin.getNom());
            psUser.setString(2, admin.getPrenom());
            psUser.setInt(3, admin.getCin());
            psUser.setString(4, admin.getEmail());
            psUser.setString(5, admin.getMotDePasse());
            psUser.setString(6, Role.ADMIN.toString());
            psUser.setObject(7, admin.getDateCreation());
            psUser.setString(8, admin.getSignature());
            if (admin.getRevenus() == null) psUser.setNull(9, Types.DOUBLE);
            else psUser.setDouble(9, admin.getRevenus());
            psUser.setInt(10, admin.getId());
            psUser.executeUpdate();
        }

        System.out.println("Admin modifié avec succès!!!✅");
    }

    @Override
    public List<Admin> recupererAdmin() throws SQLException {
        List<Admin> adminsList = new ArrayList<>();
        String req = "SELECT * FROM utilisateurs WHERE UPPER(TRIM(role)) = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, Role.ADMIN.toString().toUpperCase());
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

                    Object revObj = rs.getObject("revenu");
                    Double revenusE = (revObj == null) ? null : rs.getDouble("revenu");

                    Admin admin = new Admin(idE, nomE, prenomE, cinE, emailE, motDePasseE, roleE, dateCreationE, signatureE, revenusE);
                    admin.setVerificationStatus(rs.getString("verification_status"));
                    admin.setVerificationReason(rs.getString("verification_reason"));
                    Object scoreObj = rs.getObject("verification_score");
                    admin.setVerificationScore(scoreObj == null ? null : rs.getDouble("verification_score"));
                    adminsList.add(admin);
                }
            }
        }
        return adminsList;
    }

    @Override
    public void supprimerAdmin (Admin admin) throws SQLException {
        String reqUser = "DELETE FROM `utilisateurs` WHERE `id` = ?";
        PreparedStatement psUser = connection.prepareStatement(reqUser);
        psUser.setInt(1, admin.getId());
        psUser.executeUpdate();
        psUser.close();

        System.out.println("Admin supprimé avec succès!!✅");
    }


}
