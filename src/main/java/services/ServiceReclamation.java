package services;

import entities.Reclamation;
import entities.Statut;
import entities.Categorie;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation implements IServiceReclamation<Reclamation> {
    private Connection connection;

    public ServiceReclamation() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouterReclamation(Reclamation reclamation) throws SQLException {
        // Remarque: date_creation a DEFAULT CURRENT_TIMESTAMP => on peut l'omettre
        // Remarque: statut a une valeur par défaut => on peut l'omettre si null/empty
        String req = "INSERT INTO reclamations (utilisateur_id, categorie, titre, description, statut, reponse) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, reclamation.getId_utilisateur());
        ps.setString(2, reclamation.getCategorie().toString());
        ps.setString(3, reclamation.getTitre());
        ps.setString(4, reclamation.getDescription());

        // Si statut non fourni, on laisse la valeur par défaut côté DB ("EN ATTENTE")
        if (reclamation.getStatut() == null || reclamation.getStatut().toString().trim().isEmpty()) {
            ps.setString(5, Statut.EN_ATTENTE.toString());
        } else {
            ps.setString(5, reclamation.getStatut().toString());
        }

        // reponse peut être NULL
        if (reclamation.getReponse() == null || reclamation.getReponse().trim().isEmpty()) {
            ps.setNull(6, Types.LONGVARCHAR);
        } else {
            ps.setString(6, reclamation.getReponse());
        }

        ps.executeUpdate();

        // récupérer l'id généré
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            reclamation.setId(generatedKeys.getInt(1));
        }

        generatedKeys.close();
        ps.close();

        System.out.println("Réclamation ajoutée avec succès ✅");
    }

    @Override
    public void modifierReclamation(Reclamation reclamation) throws SQLException {
        // On modifie uniquement les champs "éditables"
        String req = "UPDATE reclamations " +
                "SET utilisateur_id=?, categorie=?, titre=?, description=?, statut=?, reponse=? " +
                "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, reclamation.getId_utilisateur());
        ps.setString(2, reclamation.getCategorie().toString());
        ps.setString(3, reclamation.getTitre());
        ps.setString(4, reclamation.getDescription());

        if (reclamation.getStatut() == null || reclamation.getStatut().toString().trim().isEmpty()) {
            ps.setString(5, Statut.EN_ATTENTE.toString());
        } else {
            ps.setString(5, reclamation.getStatut().toString());
        }

        if (reclamation.getReponse() == null || reclamation.getReponse().trim().isEmpty()) {
            ps.setNull(6, Types.LONGVARCHAR);
        } else {
            ps.setString(6, reclamation.getReponse());
        }

        ps.setInt(7, reclamation.getId());

        ps.executeUpdate();
        ps.close();

        System.out.println("Réclamation modifiée avec succès ✅");
    }

    @Override
    public void supprimerReclamation(Reclamation reclamation) throws SQLException {
        String req = "DELETE FROM reclamations WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, reclamation.getId());
        ps.executeUpdate();
        ps.close();

        System.out.println("Réclamation supprimée avec succès ✅");
    }

    @Override
    public List<Reclamation> recupererReclamation() throws SQLException {
        List<Reclamation> list = new ArrayList<>();

        String req = "SELECT id, utilisateur_id, categorie, titre, description, date_creation, statut, reponse " +
                "FROM reclamations";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int id = rs.getInt("id");
            int utilisateurId = rs.getInt("utilisateur_id");
            //String categorie = rs.getString("categorie");
            Categorie categorie = Categorie.valueOf(rs.getString("categorie"));
            String titre = rs.getString("titre");
            String description = rs.getString("description");

            // DATETIME -> LocalDateTime
            Timestamp ts = rs.getTimestamp("date_creation");
            LocalDateTime dateAjout = (ts != null) ? ts.toLocalDateTime() : null;

            //String statut = rs.getString("statut");
            Statut statut = Statut.valueOf(rs.getString("statut"));
            String reponse = rs.getString("reponse");

            Reclamation r = new Reclamation(id, utilisateurId, categorie, titre, description, dateAjout, statut, reponse);
            list.add(r);
        }

        rs.close();
        st.close();
        return list;
    }

    public List<entities.ReclamationRow> recupererReclamationAvecUtilisateur() throws SQLException {
        List<entities.ReclamationRow> list = new ArrayList<>();

        // JOIN reclamations + utilisateurs
        String sql = """
        SELECT r.id, r.utilisateur_id, u.nom, u.prenom, u.role, u.email,
               r.categorie, r.titre, r.description, r.date_creation, r.statut, r.reponse
        FROM reclamations r
        JOIN utilisateurs u ON u.id = r.utilisateur_id
        ORDER BY r.date_creation DESC
        """;

        // ⚠️ important: si ton MyDatabase utilise une seule connection, ne pas la fermer ici.
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int id = rs.getInt("id");
            int utilisateurId = rs.getInt("utilisateur_id");

            String nom = rs.getString("nom");
            String prenom = rs.getString("prenom");
            String role = rs.getString("role");
            String email = rs.getString("email");

            String categorie = rs.getString("categorie");
            String titre = rs.getString("titre");
            String description = rs.getString("description");

            Timestamp ts = rs.getTimestamp("date_creation");
            java.time.LocalDateTime dateCreation = (ts != null) ? ts.toLocalDateTime() : null;

            String statut = rs.getString("statut");
            String reponse = rs.getString("reponse");

            list.add(new entities.ReclamationRow(
                    id, utilisateurId, nom, prenom, role, email,
                    categorie, titre, description, dateCreation,
                    statut, reponse
            ));
        }

        rs.close();
        st.close();
        return list;
    }

/*
    public void repondreAReclamation(int reclamationId, String reponseFormatee) throws SQLException {
        String sql = "UPDATE reclamations SET reponse = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, reponseFormatee);
        ps.setInt(2, reclamationId);
        ps.executeUpdate();
        ps.close();
    }*/


    // ===================== AJOUTS (pour Répondre + concaténer) =====================

    public String getReponseById(int reclamationId) throws SQLException {
        String sql = "SELECT reponse FROM reclamations WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, reclamationId);
        ResultSet rs = ps.executeQuery();

        String rep = null;
        if (rs.next()) {
            rep = rs.getString("reponse");
        }

        rs.close();
        ps.close();
        return rep;
    }

    /**
     * ✅ Ajoute une réponse en la concaténant à la suite (sur une nouvelle ligne),
     * au lieu d’écraser l’ancienne.
     */
    public void ajouterReponseConcatenee(int reclamationId, String nouvelleReponseFormatee) throws SQLException {
        String ancienne = getReponseById(reclamationId);

        String finalText;
        if (ancienne == null || ancienne.trim().isEmpty()) {
            finalText = nouvelleReponseFormatee;
        } else {
            finalText = ancienne + "\n" + nouvelleReponseFormatee;
        }

        String sql = "UPDATE reclamations SET reponse = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, finalText);
        ps.setInt(2, reclamationId);
        ps.executeUpdate();
        ps.close();
    }
}
