package services;

import entities.User;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Service CRUD mta3 les utilisateurs (table 'utilisateurs')
// hedhi el table mta3 Ayoub (module Gestion Utilisateurs) - n9rawha ken bech njibou les users
public class UserService implements IService<User> {

    private Connection cnx;

    public UserService() {
        // njibou el connexion mel Singleton
        this.cnx = MyDatabase.getInstance().getConnection();
    }

    // ===== AJOUTER USER = INSERT INTO utilisateurs =====
    @Override
    public void ajouter(User user) throws SQLException {
        String query = "INSERT INTO utilisateurs (nom, prenom, cin, email, motDePasse, role, dateCreation, signature) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setInt(3, user.getCin());
            pst.setString(4, user.getEmail());
            pst.setString(5, user.getMotDePasse());
            pst.setString(6, user.getRole() != null ? user.getRole() : "AGRICULTEUR");
            pst.setDate(7, Date.valueOf(user.getDateCreation() != null ? user.getDateCreation() : LocalDate.now()));
            pst.setString(8, user.getSignature() != null ? user.getSignature() : "");
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
            System.out.println("Utilisateur ajouté : " + user.getNom());
        }
    }

    // ===== MODIFIER USER = UPDATE utilisateurs SET ... WHERE id=? =====
    @Override
    public void modifier(User user) throws SQLException {
        String query = "UPDATE utilisateurs SET nom=?, prenom=?, cin=?, email=?, motDePasse=?, role=?, signature=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setInt(3, user.getCin());
            pst.setString(4, user.getEmail());
            pst.setString(5, user.getMotDePasse());
            pst.setString(6, user.getRole());
            pst.setString(7, user.getSignature());
            pst.setInt(8, user.getId());
            pst.executeUpdate();
        }
    }

    // ===== SUPPRIMER USER = DELETE FROM utilisateurs WHERE id=? =====
    @Override
    public void supprimer(User user) throws SQLException {
        String query = "DELETE FROM utilisateurs WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, user.getId());
            pst.executeUpdate();
        }
    }

    // ===== RECUPERER PAR ID =====
    @Override
    public User recupererParId(int id) throws SQLException {
        String query = "SELECT * FROM utilisateurs WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        }
        return null;
    }

    // ===== RECUPERER TOUT = SELECT * FROM utilisateurs =====
    @Override
    public List<User> recuperer() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM utilisateurs ORDER BY nom, prenom";
        try (PreparedStatement pst = cnx.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSet(rs));
            }
        }
        return users;
    }

    // n7awlou el ResultSet (mel base) l objet User (mapping kol colonne -> attribut)
    private User mapResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setCin(rs.getInt("cin"));
        user.setEmail(rs.getString("email"));
        user.setMotDePasse(rs.getString("motDePasse"));
        user.setRole(rs.getString("role"));
        Date dc = rs.getDate("dateCreation");
        if (dc != null) user.setDateCreation(dc.toLocalDate());
        user.setSignature(rs.getString("signature"));
        return user;
    }
}