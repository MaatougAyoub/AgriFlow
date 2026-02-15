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
        // 1️⃣ Insérer dans la table `utilisateurs
        String reqUser = "INSERT INTO `utilisateurs`(`nom`, `prenom`, `cin`, `email`, `motDePasse`, `role`, `dateCreation`, `signature`)" +
                    "VALUES ('" + agriculteur.getNom() + "','" + agriculteur.getPrenom() + "','" + agriculteur.getCin() + "','" + agriculteur.getEmail() + "','" + agriculteur.getMotDePasse() + "','" + Role.AGRICULTEUR + "','" + agriculteur.getDateCreation() + "','" + agriculteur.getSignature() + "')";
        Statement st = connection.createStatement();
        st.executeUpdate(reqUser, Statement.RETURN_GENERATED_KEYS);

        // 2️⃣ Récupérer l'ID auto-généré
        ResultSet generatedKeys = st.getGeneratedKeys();
        int userId = 0;
        if (generatedKeys.next()) {
            userId = generatedKeys.getInt(1);
        }

        // 3️⃣ Insérer dans la table `agriculteurs` avec l'ID récupéré
        String reqAgriculteur = "INSERT INTO `agriculteurs`(`id`, `nom`, `prenom`, `cin`, `email`, `motDePasse`, `role`, `dateCreation`, `signature`, `carte_pro`, `adresse`, `parcelles`) " +
                    "VALUES ('" + userId + "','" + agriculteur.getNom() + "','" + agriculteur.getPrenom() + "','" + agriculteur.getCin() + "','" + agriculteur.getEmail() + "','" + agriculteur.getMotDePasse() + "','" + Role.AGRICULTEUR+ "','" + agriculteur.getDateCreation() + "','" + agriculteur.getSignature() + "','" + agriculteur.getCarte_pro() + "','" + agriculteur.getAdresse()+ "','" + agriculteur.getParcelles() + "')";

        st.executeUpdate(reqAgriculteur);
        st.close();
        System.out.println("Agriculteur ajouté avec succés !!! ✅");
    }

    @Override
    public void modifierAgriculteur (Agriculteur agriculteur) throws SQLException{
        // 1️⃣ Mettre à jour la table `utilisateurs`
        String reqUser = "UPDATE utilisateurs SET nom=?, prenom=?, cin=?, email=?, motDePasse=?, role=?, dateCreation=?, signature=? WHERE id=?";
        PreparedStatement psUser = connection.prepareStatement(reqUser);
        psUser.setString(1, agriculteur.getNom());
        psUser.setString(2, agriculteur.getPrenom());
        psUser.setInt(3, agriculteur.getCin());
        psUser.setString(4, agriculteur.getEmail());
        psUser.setString(5, agriculteur.getMotDePasse());
        psUser.setString(6, Role.AGRICULTEUR.toString());
        psUser.setObject(7, agriculteur.getDateCreation());
        psUser.setString(8, agriculteur.getSignature());
        psUser.setInt(9, agriculteur.getId()); //  WHERE id=?
        psUser.executeUpdate();
        psUser.close();


        // 2️⃣ Mettre à jour la table `agriculteurs`
        String reqAgriculteur = "UPDATE agriculteurs SET nom=?, prenom=?, cin=?, email=?, motDePasse=?, role=?, dateCreation=?, signature=?, carte_pro=?, adresse=?, parcelles=? WHERE id=?";
        PreparedStatement psAgriculteur = connection.prepareStatement(reqAgriculteur);
        psAgriculteur.setString(1, agriculteur.getNom());
        psAgriculteur.setString(2, agriculteur.getPrenom());
        psAgriculteur.setInt(3, agriculteur.getCin());
        psAgriculteur.setString(4, agriculteur.getEmail());
        psAgriculteur.setString(5, agriculteur.getMotDePasse());
        psAgriculteur.setString(6, Role.AGRICULTEUR.toString());
        psAgriculteur.setObject(7, agriculteur.getDateCreation());
        psAgriculteur.setString(8, agriculteur.getSignature());
        psAgriculteur.setString(9, agriculteur.getCarte_pro());
        psAgriculteur.setString(10, agriculteur.getAdresse());
        psAgriculteur.setString(11, agriculteur.getParcelles());
        psAgriculteur.setInt(12, agriculteur.getId()); // ✅ WHERE id=?
        psAgriculteur.executeUpdate();
        psAgriculteur.close();

        System.out.println("Agriculteur modifié avec succès! ✅");
    }

    @Override
    public List<Agriculteur> recupererAgriculteurs() throws SQLException {
        List<Agriculteur> agriculteursList = new ArrayList<>();
        String req = "SELECT * FROM agriculteurs ";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            int idA = rs.getInt("id");
            String nomA = rs.getString(2);
            String prenomE = rs.getString(3);
            int cinA = rs.getInt(4);
            String emailA = rs.getString(5);
            String motDePasseA = rs.getString(6);
            String roleA = rs.getString(7);
            //ic j'ai besoin de la variable dateCreation de type LocalDate(voir le constructeur dans la classe Agriculteur)
            java.sql.Date sqlDate = rs.getDate(8);
            java.time.LocalDate dateCreationA = (sqlDate != null) ? sqlDate.toLocalDate() : null;
            String signatureA = rs.getString(9);
            String carte_proA = rs.getString(10);
            String adresseA = rs.getString(11);
            String parcellesA = rs.getString(12);
            Agriculteur agriculteur = new Agriculteur(idA, nomA, prenomE, cinA, emailA, motDePasseA, roleA, dateCreationA, signatureA, carte_proA, adresseA, parcellesA);
            agriculteursList.add(agriculteur);
        }
        rs.close();
        st.close();
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
        Connection cnx = MyDatabase.getInstance().getConnection(); // ✅ récupérer une connexion active

        // 1) utilisateurs
        String sqlUser = "UPDATE utilisateurs SET motDePasse = ? WHERE email = ? AND role = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlUser)) {
            ps.setString(1, nouveauMotDePasse);
            ps.setString(2, email);
            ps.setString(3, Role.AGRICULTEUR.toString());
            ps.executeUpdate();
        }

        // 2) agriculteurs
        String sqlAgri = "UPDATE agriculteurs SET motDePasse = ? WHERE email = ? AND role = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlAgri)) {
            ps.setString(1, nouveauMotDePasse);
            ps.setString(2, email);
            ps.setString(3, Role.AGRICULTEUR.toString());
            ps.executeUpdate();
        }
    }

}
