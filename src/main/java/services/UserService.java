package services;

import entities.User;
import utils.MyDatabase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service CRUD pour gérer les utilisateurs du Marketplace.
 * Implémente l'interface IService avec des PreparedStatement JDBC.
 */
public class UserService implements IService<User> {

    private Connection cnx;

    public UserService() {
        this.cnx = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(User user) throws SQLException {
        String query = "INSERT INTO users (nom, prenom, email, password, telephone, adresse, region) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getEmail());
            pst.setString(4, "password123");
            pst.setString(5, user.getTelephone());
            pst.setString(6, user.getAdresse());
            pst.setString(7, user.getRegion());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
            System.out.println("✅ Utilisateur ajouté : " + user.getNom());
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(User user) throws SQLException {
        String query = "UPDATE users SET nom=?, prenom=?, email=?, telephone=?, adresse=?, region=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getTelephone());
            pst.setString(5, user.getAdresse());
            pst.setString(6, user.getRegion());
            pst.setInt(7, user.getId());
            pst.executeUpdate();
            System.out.println("✅ Utilisateur modifié : " + user.getNom());
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void supprimer(User user) throws SQLException {
        String query = "DELETE FROM users WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, user.getId());
            pst.executeUpdate();
            System.out.println("✅ Utilisateur supprimé : " + user.getNom());
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public User recupererParId(int id) throws SQLException {
        String query = "SELECT * FROM users WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
            throw e;
        }
        return null;
    }

    @Override
    public List<User> recuperer() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users ORDER BY nom, prenom";
        try (PreparedStatement pst = cnx.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSet(rs));
            }
            System.out.println("✅ " + users.size() + " utilisateur(s) récupéré(s).");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des utilisateurs : " + e.getMessage());
            throw e;
        }
        return users;
    }

    // ===== GESTION DES SIGNATURES =====

    public boolean enregistrerSignature(int userId, File signatureFile) {
        try {
            byte[] signatureBytes = Files.readAllBytes(signatureFile.toPath());
            return enregistrerSignature(userId, signatureBytes);
        } catch (IOException e) {
            System.err.println("Erreur lecture fichier: " + e.getMessage());
            return false;
        }
    }

    public boolean enregistrerSignature(int userId, byte[] signatureBytes) {
        String query = "UPDATE users SET signature_image = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setBytes(1, signatureBytes);
            pst.setInt(2, userId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            return false;
        }
    }

    public byte[] getSignature(int userId) {
        String query = "SELECT signature_image FROM users WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getBytes("signature_image");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
        }
        return null;
    }

    private User mapResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setTelephone(rs.getString("telephone"));
        user.setAdresse(rs.getString("adresse"));
        user.setRegion(rs.getString("region"));
        user.setPhotoProfil(rs.getString("photo_profil"));
        try {
            user.setSignatureImage(rs.getBytes("signature_image"));
        } catch (SQLException e) {}
        return user;
    }
}
