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
        // 1️⃣ Insérer dans la table `utilisateurs
        String reqUser = "INSERT INTO `utilisateurs`(`nom`, `prenom`, `cin`, `email`, `motDePasse`, `role`, `dateCreation`, `signature`)" +
                "VALUES ('" + admin.getNom() + "','" + admin.getPrenom() + "','" + admin.getCin() + "','" + admin.getEmail() + "','" + admin.getMotDePasse() + "','" + Role.ADMIN + "','" + admin.getDateCreation() + "','" + admin.getSignature() + "')";
        Statement st = connection.createStatement();
        st.executeUpdate(reqUser, Statement.RETURN_GENERATED_KEYS);

        // 2️⃣ Récupérer l'ID auto-généré
        ResultSet generatedKeys = st.getGeneratedKeys();
        int userId = 0;
        if (generatedKeys.next()) {
            userId = generatedKeys.getInt(1);
        }

        // 3️⃣ Insérer dans la table `Admins` avec l'ID récupéré
        String reqAdmin = "INSERT INTO `Admins`(`id`, `nom`, `prenom`, `cin`, `email`, `motDePasse`, `role`, `dateCreation`, `signature`, `revenu`) " +
                "VALUES ('" + userId + "','" + admin.getNom() + "','" + admin.getPrenom() + "','" + admin.getCin() + "','" + admin.getEmail() + "','" + admin.getMotDePasse() + "','" + Role.ADMIN + "','" + admin.getDateCreation() + "','" + admin.getSignature() + "','" + admin.getRevenus() + "')";

        st.executeUpdate(reqAdmin);
        st.close();
        System.out.println("Admin ajoute avec succés!!! ✅");
    }

    @Override
    public void modifierAdmin (Admin admin) throws SQLException{
        // 1️⃣ Mettre à jour la table `utilisateurs`
        String reqUser = "UPDATE utilisateurs SET nom=?, prenom=?, cin=?, email=?, motDePasse=?, role=?, dateCreation=?, signature=? WHERE id=?";
        PreparedStatement psUser = connection.prepareStatement(reqUser);
        psUser.setString(1, admin.getNom());
        psUser.setString(2, admin.getPrenom());
        psUser.setInt(3, admin.getCin());
        psUser.setString(4, admin.getEmail());
        psUser.setString(5, admin.getMotDePasse());
        psUser.setString(6, Role.ADMIN.toString());
        psUser.setObject(7, admin.getDateCreation());
        psUser.setString(8, admin.getSignature());
        psUser.setInt(9, admin.getId()); //  WHERE id=?
        psUser.executeUpdate();
        psUser.close();

        // 2️⃣ Mettre à jour la table `Admins`
        String reqAdmin = "UPDATE admins SET nom=?, prenom=?, cin=?, email=?, motDePasse=?, role=?, dateCreation=?, signature=?, revenu=? WHERE id=?";
        PreparedStatement psAdmin = connection.prepareStatement(reqAdmin);
        psAdmin.setString(1, admin.getNom());
        psAdmin.setString(2, admin.getPrenom());
        psAdmin.setInt(3, admin.getCin());
        psAdmin.setString(4, admin.getEmail());
        psAdmin.setString(5, admin.getMotDePasse());
        psAdmin.setString(6, Role.ADMIN.toString());
        psAdmin.setObject(7, admin.getDateCreation());
        psAdmin.setString(8, admin.getSignature());
        psAdmin.setDouble(9, admin.getRevenus());
        psAdmin.setInt(10, admin.getId()); //  WHERE id=?
        psAdmin.executeUpdate();
        psAdmin.close();

        System.out.println("Admin modifié avec succès!!!✅");
    }

    @Override
    public List<Admin> recupererAdmin() throws SQLException {
        List<Admin> adminsList = new ArrayList<>();
        String req = "SELECT * FROM admins ";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            int idE = rs.getInt("id");
            String nomE = rs.getString(2);
            String prenomE = rs.getString(3);
            int cinE = rs.getInt(4);
            String emailE = rs.getString(5);
            String motDePasseE = rs.getString(6);
            String roleE = rs.getString(7);
            //ic j'ai besoin de la variable dateCreation de type LocalDate(voir le constructeur dans la classe Admin)
            java.sql.Date sqlDate = rs.getDate(8);
            java.time.LocalDate dateCreationE = (sqlDate != null) ? sqlDate.toLocalDate() : null;
            String signatureE = rs.getString(9);
            Double revenusE = rs.getDouble(10);
            Admin admin = new Admin(idE, nomE, prenomE, cinE, emailE, motDePasseE, roleE, dateCreationE, signatureE,revenusE);
            adminsList.add(admin);
        }
        rs.close();
        st.close();
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
