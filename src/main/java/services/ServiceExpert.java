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
        // 1️⃣ Insérer dans la table `utilisateurs
        String reqUser = "INSERT INTO `utilisateurs`(`nom`, `prenom`, `cin`, `email`, `motDePasse`, `role`, `dateCreation`, `signature`)" +
                "VALUES ('" + expert.getNom() + "','" + expert.getPrenom() + "','" + expert.getCin() + "','" + expert.getEmail() + "','" + expert.getMotDePasse() + "','" + Role.EXPERT + "','" + expert.getDateCreation() + "','" + expert.getSignature() + "')";
        Statement st = connection.createStatement();
        st.executeUpdate(reqUser, Statement.RETURN_GENERATED_KEYS);

        // 2️⃣ Récupérer l'ID auto-généré
        ResultSet generatedKeys = st.getGeneratedKeys();
        int userId = 0;
        if (generatedKeys.next()) {
            userId = generatedKeys.getInt(1);
        }

        // 3️⃣ Insérer dans la table `experts` avec l'ID récupéré
        String reqExpert = "INSERT INTO `experts`(`id`, `nom`, `prenom`, `cin`, `email`, `motDePasse`, `role`, `dateCreation`, `signature`, `certification`) " +
                "VALUES ('" + userId + "','" + expert.getNom() + "','" + expert.getPrenom() + "','" + expert.getCin() + "','" + expert.getEmail() + "','" + expert.getMotDePasse() + "','" + Role.EXPERT + "','" + expert.getDateCreation() + "','" + expert.getSignature() + "','" + expert.getCertification() + "')";

        st.executeUpdate(reqExpert);
        st.close();
        System.out.println("Expert ajoute ave" +
                "c succés!!! ✅");
    }

    public void modifierExpert(Expert expert) throws SQLException {
        // 1️⃣ Mettre à jour la table `utilisateurs`
        String reqUser = "UPDATE utilisateurs SET nom=?, prenom=?, cin=?, email=?, motDePasse=?, role=?, dateCreation=?, signature=? WHERE id=?";
        PreparedStatement psUser = connection.prepareStatement(reqUser);
        psUser.setString(1, expert.getNom());
        psUser.setString(2, expert.getPrenom());
        psUser.setInt(3, expert.getCin());
        psUser.setString(4, expert.getEmail());
        psUser.setString(5, expert.getMotDePasse());
        psUser.setString(6, Role.EXPERT.toString());
        psUser.setObject(7, expert.getDateCreation());
        psUser.setString(8, expert.getSignature());
        psUser.setInt(9, expert.getId()); // ✅ WHERE id=?
        psUser.executeUpdate();
        psUser.close();

        // 2️⃣ Mettre à jour la table `experts`
        String reqExpert = "UPDATE experts SET nom=?, prenom=?, cin=?, email=?, motDePasse=?, role=?, dateCreation=?, signature=?, certification=? WHERE id=?";
        PreparedStatement psExpert = connection.prepareStatement(reqExpert);
        psExpert.setString(1, expert.getNom());
        psExpert.setString(2, expert.getPrenom());
        psExpert.setInt(3, expert.getCin());
        psExpert.setString(4, expert.getEmail());
        psExpert.setString(5, expert.getMotDePasse());
        psExpert.setString(6, Role.EXPERT.toString());
        psExpert.setObject(7, expert.getDateCreation());
        psExpert.setString(8, expert.getSignature());
        psExpert.setString(9, expert.getCertification()); // ✅ Certification
        psExpert.setInt(10, expert.getId()); //  WHERE id=?
        psExpert.executeUpdate();
        psExpert.close();

        System.out.println("Expert modifié avec succès!!✅");
    }
    @Override
    public void supprimerExpert (Expert expert) throws SQLException
    {
        /* // 1️⃣ Supprimer de la table `experts` en premier (clé étrangère)
        String reqExpert = "DELETE FROM `experts` WHERE `id` = ?";
        PreparedStatement psExpert = connection.prepareStatement(reqExpert);
        psExpert.setInt(1, expert.getId());
        psExpert.executeUpdate();
        psExpert.close();*/
        /*⛔⛔Remarque importante: j'ai pas besoin d'une requete de suppression pour la table "experts" car c'est automatique grace à
        CONSTRAINT fk_experts_utilisateurs
        FOREIGN KEY (id) REFERENCES utilisateurs(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
         */
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
        String req = "SELECT * FROM experts ";
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
            //ic j'ai besoin de la variable dateCreation de type LocalDate(voir le constructeur dans la classe Expert)
            java.sql.Date sqlDate = rs.getDate(8);
            java.time.LocalDate dateCreationE = (sqlDate != null) ? sqlDate.toLocalDate() : null;
            String signatureE = rs.getString(9);
            String certificationE = rs.getString(10);
            Expert expert = new Expert(idE, nomE, prenomE, cinE, emailE, motDePasseE, roleE, dateCreationE, signatureE,certificationE);
            expertsList.add(expert);
        }
        rs.close();
        st.close();
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
        Connection cnx = MyDatabase.getInstance().getConnection(); // ✅ récupérer une connexion active

        // 1) utilisateurs
        String sqlUser = "UPDATE utilisateurs SET motDePasse = ? WHERE email = ? AND role = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlUser)) {
            ps.setString(1, nouveauMotDePasse);
            ps.setString(2, email);
            ps.setString(3, Role.EXPERT.toString());
            ps.executeUpdate();
        }

        // 2) experts
        String sqlExpert = "UPDATE experts SET motDePasse = ? WHERE email = ? AND role = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlExpert)) {
            ps.setString(1, nouveauMotDePasse);
            ps.setString(2, email);
            ps.setString(3, Role.EXPERT.toString());
            ps.executeUpdate();
        }
    }


}






