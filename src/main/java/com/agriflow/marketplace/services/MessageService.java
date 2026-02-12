package com.agriflow.marketplace.services;

import com.agriflow.marketplace.models.*;
import com.agriflow.marketplace.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer la messagerie P2P entre agriculteurs.
 * Communication directe sans intervention administrateur.
 */
public class MessageService implements IService<Message> {

    private Connection cnx;
    private UserService userService;
    private AnnonceService annonceService;

    public MessageService() {
        this.cnx = MyDatabase.getInstance().getCnx();
        this.userService = new UserService();
        this.annonceService = new AnnonceService();
    }

    @Override
    public void ajouter(Message message) throws SQLException {
        String query = "INSERT INTO messages (expediteur_id, destinataire_id, sujet, contenu, " +
                       "annonce_id, reservation_id, lu) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, message.getExpediteur().getId());
            pst.setInt(2, message.getDestinataire().getId());
            pst.setString(3, message.getSujet());
            pst.setString(4, message.getContenu());
            pst.setObject(5, message.getAnnonce() != null ? message.getAnnonce().getId() : null);
            pst.setObject(6, message.getReservation() != null ? message.getReservation().getId() : null);
            pst.setBoolean(7, message.isLu());

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        message.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Message envoyé de " + message.getExpediteur().getNomComplet() +
                                   " à " + message.getDestinataire().getNomComplet());
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'envoi du message : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(Message message) throws SQLException {
        String query = "UPDATE messages SET lu = ?, date_lecture = ? WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setBoolean(1, message.isLu());
            pst.setTimestamp(2, message.getDateLecture() != null ?
                             Timestamp.valueOf(message.getDateLecture()) : null);
            pst.setInt(3, message.getId());

            pst.executeUpdate();
            System.out.println("✅ Message mis à jour : " + message.getId());
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification du message : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void supprimer(Message message) throws SQLException {
        String query = "DELETE FROM messages WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, message.getId());
            pst.executeUpdate();
            System.out.println("✅ Message supprimé : " + message.getId());
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du message : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Message> recuperer() throws SQLException {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages ORDER BY date_envoi DESC";

        try (PreparedStatement pst = cnx.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }

            System.out.println("✅ " + messages.size() + " message(s) récupéré(s).");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des messages : " + e.getMessage());
            throw e;
        }

        return messages;
    }

    @Override
    public Message recupererParId(int id) throws SQLException {
        String query = "SELECT * FROM messages WHERE id = ?";
        Message message = null;

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    message = mapResultSetToMessage(rs);
                    System.out.println("✅ Message trouvé : " + message.getId());
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du message : " + e.getMessage());
            throw e;
        }

        return message;
    }

    /**
     * Récupère tous les messages reçus par un utilisateur.
     *
     * @param userId ID de l'utilisateur destinataire
     * @return Liste des messages reçus
     */
    public List<Message> recupererMessagesRecus(int userId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE destinataire_id = ? ORDER BY date_envoi DESC";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }

            System.out.println("✅ " + messages.size() + " message(s) reçu(s) pour l'utilisateur " + userId);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des messages reçus : " + e.getMessage());
            throw e;
        }

        return messages;
    }

    /**
     * Récupère tous les messages envoyés par un utilisateur.
     *
     * @param userId ID de l'utilisateur expéditeur
     * @return Liste des messages envoyés
     */
    public List<Message> recupererMessagesEnvoyes(int userId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE expediteur_id = ? ORDER BY date_envoi DESC";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }

            System.out.println("✅ " + messages.size() + " message(s) envoyé(s) par l'utilisateur " + userId);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des messages envoyés : " + e.getMessage());
            throw e;
        }

        return messages;
    }

    /**
     * Récupère la conversation entre deux utilisateurs.
     *
     * @param userId1 ID du premier utilisateur
     * @param userId2 ID du second utilisateur
     * @return Liste des messages échangés entre les deux utilisateurs
     */
    public List<Message> recupererConversation(int userId1, int userId2) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE " +
                       "(expediteur_id = ? AND destinataire_id = ?) OR " +
                       "(expediteur_id = ? AND destinataire_id = ?) " +
                       "ORDER BY date_envoi ASC";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId1);
            pst.setInt(2, userId2);
            pst.setInt(3, userId2);
            pst.setInt(4, userId1);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }

            System.out.println("✅ Conversation récupérée : " + messages.size() + " message(s)");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de la conversation : " + e.getMessage());
            throw e;
        }

        return messages;
    }

    /**
     * Récupère les messages non lus d'un utilisateur.
     *
     * @param userId ID de l'utilisateur
     * @return Liste des messages non lus
     */
    public List<Message> recupererMessagesNonLus(int userId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE destinataire_id = ? AND lu = FALSE " +
                       "ORDER BY date_envoi DESC";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }

            System.out.println("✅ " + messages.size() + " message(s) non lu(s)");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des messages non lus : " + e.getMessage());
            throw e;
        }

        return messages;
    }

    /**
     * Marque un message comme lu.
     *
     * @param messageId ID du message
     */
    public void marquerCommeLu(int messageId) throws SQLException {
        Message message = recupererParId(messageId);
        if (message != null && !message.isLu()) {
            message.marquerCommeLu();
            modifier(message);
        }
    }

    /**
     * Compte le nombre de messages non lus pour un utilisateur.
     *
     * @param userId ID de l'utilisateur
     * @return Nombre de messages non lus
     */
    public int compterMessagesNonLus(int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM messages WHERE destinataire_id = ? AND lu = FALSE";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du comptage des messages non lus : " + e.getMessage());
            throw e;
        }

        return 0;
    }

    /**
     * Envoie un message rapide entre deux utilisateurs.
     *
     * @param expediteurId ID de l'expéditeur
     * @param destinataireId ID du destinataire
     * @param sujet Sujet du message
     * @param contenu Contenu du message
     * @return Le message créé
     */
    public Message envoyerMessage(int expediteurId, int destinataireId, String sujet, String contenu) throws SQLException {
        User expediteur = userService.recupererParId(expediteurId);
        User destinataire = userService.recupererParId(destinataireId);

        if (expediteur == null || destinataire == null) {
            throw new SQLException("Utilisateur introuvable");
        }

        Message message = new Message();
        message.setExpediteur(expediteur);
        message.setDestinataire(destinataire);
        message.setSujet(sujet);
        message.setContenu(contenu);

        ajouter(message);
        return message;
    }

    /**
     * Mappe un ResultSet vers un objet Message.
     */
    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setSujet(rs.getString("sujet"));
        message.setContenu(rs.getString("contenu"));
        message.setLu(rs.getBoolean("lu"));

        Timestamp dateLecture = rs.getTimestamp("date_lecture");
        if (dateLecture != null) {
            message.setDateLecture(dateLecture.toLocalDateTime());
        }

        Timestamp dateEnvoi = rs.getTimestamp("date_envoi");
        if (dateEnvoi != null) {
            message.setDateEnvoi(dateEnvoi.toLocalDateTime());
        }

        // Récupération de l'expéditeur
        int expediteurId = rs.getInt("expediteur_id");
        try {
            User expediteur = userService.recupererParId(expediteurId);
            message.setExpediteur(expediteur);
        } catch (SQLException e) {
            System.err.println("⚠️ Impossible de récupérer l'expéditeur ID: " + expediteurId);
        }

        // Récupération du destinataire
        int destinataireId = rs.getInt("destinataire_id");
        try {
            User destinataire = userService.recupererParId(destinataireId);
            message.setDestinataire(destinataire);
        } catch (SQLException e) {
            System.err.println("⚠️ Impossible de récupérer le destinataire ID: " + destinataireId);
        }

        // Récupération de l'annonce (si liée)
        int annonceId = rs.getInt("annonce_id");
        if (!rs.wasNull()) {
            try {
                Annonce annonce = annonceService.recupererParId(annonceId);
                message.setAnnonce(annonce);
            } catch (SQLException e) {
                System.err.println("⚠️ Impossible de récupérer l'annonce ID: " + annonceId);
            }
        }

        return message;
    }
}
