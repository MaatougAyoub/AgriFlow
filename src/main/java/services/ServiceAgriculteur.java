package services;

import entities.Agriculteur;
import entities.Role;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceAgriculteur implements IServiceAgriculteur <Agriculteur>{
    private Connection connection;
    public ServiceAgriculteur() {
        connection = MyDatabase.getInstance().getConnection();
    }

    public void ajouterAgriculteur (Agriculteur agriculteur) throws SQLException {
        String sql = "INSERT INTO utilisateurs (nom, prenom, cin, email, motDePasse, role, dateCreation, signature, carte_pro, adresse, parcelles) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, agriculteur.getNom());
            ps.setString(2, agriculteur.getPrenom());
            ps.setInt(3, agriculteur.getCin());
            ps.setString(4, agriculteur.getEmail());
            ps.setString(5, agriculteur.getMotDePasse());
            ps.setString(6, Role.AGRICULTEUR.toString());
            ps.setObject(7, agriculteur.getDateCreation());
            ps.setString(8, agriculteur.getSignature());
            ps.setString(9, agriculteur.getCarte_pro());
            ps.setString(10, agriculteur.getAdresse());
            ps.setString(11, agriculteur.getParcelles());
            ps.executeUpdate();
        }
        System.out.println("Agriculteur ajouté avec succés !!! ✅");
    }

    @Override
    public void modifierAgriculteur (Agriculteur agriculteur) throws SQLException{
        String reqUser = "UPDATE utilisateurs SET nom=?, prenom=?, cin=?, email=?, motDePasse=?, role=?, dateCreation=?, signature=?, carte_pro=?, adresse=?, parcelles=? WHERE id=?";
        try (PreparedStatement psUser = connection.prepareStatement(reqUser)) {
            psUser.setString(1, agriculteur.getNom());
            psUser.setString(2, agriculteur.getPrenom());
            psUser.setInt(3, agriculteur.getCin());
            psUser.setString(4, agriculteur.getEmail());
            psUser.setString(5, agriculteur.getMotDePasse());
            psUser.setString(6, Role.AGRICULTEUR.toString());
            psUser.setObject(7, agriculteur.getDateCreation());
            psUser.setString(8, agriculteur.getSignature());
            psUser.setString(9, agriculteur.getCarte_pro());
            psUser.setString(10, agriculteur.getAdresse());
            psUser.setString(11, agriculteur.getParcelles());
            psUser.setInt(12, agriculteur.getId());
            psUser.executeUpdate();
        }

        System.out.println("Agriculteur modifié avec succès! ✅");
    }

    @Override
    public List<Agriculteur> recupererAgriculteurs() throws SQLException {
        List<Agriculteur> agriculteursList = new ArrayList<>();
        String req = "SELECT * FROM utilisateurs WHERE role = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, Role.AGRICULTEUR.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idA = rs.getInt("id");
                    String nomA = rs.getString("nom");
                    String prenomA = rs.getString("prenom");
                    int cinA = rs.getInt("cin");
                    String emailA = rs.getString("email");
                    String motDePasseA = rs.getString("motDePasse");
                    String roleA = rs.getString("role");
                    java.sql.Date sqlDate = rs.getDate("dateCreation");
                    java.time.LocalDate dateCreationA = (sqlDate != null) ? sqlDate.toLocalDate() : null;
                    String signatureA = rs.getString("signature");
                    String carte_proA = rs.getString("carte_pro");
                    String adresseA = rs.getString("adresse");
                    String parcellesA = rs.getString("parcelles");

                    Agriculteur agriculteur = new Agriculteur(idA, nomA, prenomA, cinA, emailA, motDePasseA, roleA, dateCreationA, signatureA, carte_proA, adresseA, parcellesA);
                    agriculteursList.add(agriculteur);
                }
            }
        }
        return agriculteursList;
    }

    public void supprimerAgriculteur (Agriculteur agriculteur) throws SQLException{
        // 1️⃣ Supprimer de la table `utilisateurs`
        String reqUser = "DELETE FROM `utilisateurs` WHERE `id` = ?";
        PreparedStatement psUser = connection.prepareStatement(reqUser);
        psUser.setInt(1, agriculteur.getId());
        psUser.executeUpdate();
        psUser.close();

        System.out.println("Agriculteur supprimé avec succès!! ✅");
    }

    public boolean emailExiste(String email) throws SQLException {
        Connection cnx = MyDatabase.getInstance().getConnection(); //  récupérer une connexion active

        String sql = "SELECT 1 FROM utilisateurs WHERE email = ? AND role = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, Role.AGRICULTEUR.toString());
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
            ps.setString(3, Role.AGRICULTEUR.toString());
            ps.executeUpdate();
        }
    }

}
