package services;

import entities.Annonce;
import entities.Reservation;
import entities.StatutReservation;
import entities.User;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// Service CRUD mta3 les Reservations - meme principe ki AnnonceService
// kol reservation t link annonce + demandeur + proprietaire
public class ServiceReservation implements IService<Reservation> {

    // commission 10% 3la kol reservation (business logic)
    private static final double COMMISSION_TAUX = 0.10;

    private final Connection cnx;
    private final AnnonceService annonceService; // bech njibou l annonce
    private final UserService userService; // bech njibou el users

    public ServiceReservation() {
        this.cnx = MyDatabase.getInstance().getConnection();
        this.annonceService = new AnnonceService();
        this.userService = new UserService();
    }

    // ===== AJOUTER RESERVATION = INSERT INTO reservations =====
    // 9bal ma na3mlou INSERT, nvalidaw (dates s7a7, prix > 0, etc)
    // w nzidou commission 10% 3al prix
    @Override
    public void ajouter(Reservation reservation) throws SQLException {
        // njibou el proprietaire mel annonce automatiquement ken ma7attouch
        autoSetProprietaire(reservation);
        validateReservationForInsert(reservation);

        if (reservation.getQuantite() <= 0) {
            reservation.setQuantite(1);
        }

        double basePrix = reservation.getPrixTotal();
        if (basePrix <= 0) {
            reservation.calculerPrixTotal();
            basePrix = reservation.getPrixTotal();
        }
        if (basePrix <= 0) {
            throw new SQLException("Prix total invalide.");
        }

        // n7asbou el commission (10% mel prix) w nzidouha
        double commission = calculerCommission(basePrix);
        double prixTotalAvecCommission = basePrix + commission;
        reservation.setPrixTotal(prixTotalAvecCommission);

        StatutReservation statut = reservation.getStatut() != null ? reservation.getStatut()
                : StatutReservation.EN_ATTENTE;

        String query = "INSERT INTO reservations (annonce_id, demandeur_id, proprietaire_id, date_debut, date_fin, " +
                "quantite, prix_total, caution, statut, message_demande) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, reservation.getAnnonce().getId());
            pst.setInt(2, reservation.getDemandeur().getId());
            pst.setInt(3, reservation.getProprietaire().getId());
            pst.setDate(4, Date.valueOf(reservation.getDateDebut()));
            pst.setDate(5, Date.valueOf(reservation.getDateFin()));
            pst.setInt(6, reservation.getQuantite());
            pst.setDouble(7, reservation.getPrixTotal());
            pst.setDouble(8, reservation.getCaution());
            pst.setString(9, statut.name());
            pst.setString(10, reservation.getMessageDemande());

            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    reservation.setId(rs.getInt(1));
                }
            }
        }
    }

    // ===== MODIFIER RESERVATION = UPDATE reservations SET ... WHERE id=? =====
    @Override
    public void modifier(Reservation reservation) throws SQLException {
        autoSetProprietaire(reservation);
        validateReservationForUpdate(reservation);
        String query = "UPDATE reservations SET annonce_id=?, demandeur_id=?, proprietaire_id=?, date_debut=?, date_fin=?, "
                +
                "quantite=?, prix_total=?, caution=?, statut=?, message_demande=?, reponse_proprietaire=?, " +
                "date_reponse=?, contrat_url=?, contrat_signe=?, date_signature_contrat=? WHERE id=?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reservation.getAnnonce().getId());
            pst.setInt(2, reservation.getDemandeur().getId());
            pst.setInt(3, reservation.getProprietaire().getId());
            pst.setDate(4, reservation.getDateDebut() != null ? Date.valueOf(reservation.getDateDebut()) : null);
            pst.setDate(5, reservation.getDateFin() != null ? Date.valueOf(reservation.getDateFin()) : null);
            pst.setInt(6, reservation.getQuantite());
            pst.setDouble(7, reservation.getPrixTotal());
            pst.setDouble(8, reservation.getCaution());
            pst.setString(9, reservation.getStatut() != null ? reservation.getStatut().name()
                    : StatutReservation.EN_ATTENTE.name());
            pst.setString(10, reservation.getMessageDemande());
            pst.setString(11, reservation.getReponseProprietaire());
            pst.setTimestamp(12,
                    reservation.getDateReponse() != null ? Timestamp.valueOf(reservation.getDateReponse()) : null);
            pst.setString(13, reservation.getContratUrl());
            pst.setBoolean(14, reservation.isContratSigne());
            pst.setTimestamp(15,
                    reservation.getDateSignatureContrat() != null
                            ? Timestamp.valueOf(reservation.getDateSignatureContrat())
                            : null);
            pst.setInt(16, reservation.getId());

            pst.executeUpdate();
        }
    }

    // ===== SUPPRIMER RESERVATION = DELETE FROM reservations WHERE id=? =====
    @Override
    public void supprimer(Reservation reservation) throws SQLException {
        if (reservation == null || reservation.getId() <= 0) {
            throw new SQLException("ID reservation invalide.");
        }
        String query = "DELETE FROM reservations WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reservation.getId());
            pst.executeUpdate();
        }
    }

    // ===== RECUPERER TOUT = SELECT * FROM reservations =====
    @Override
    public List<Reservation> recuperer() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservations ORDER BY date_creation DESC";

        try (PreparedStatement pst = cnx.prepareStatement(query);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                reservations.add(mapResultSet(rs));
            }
        }

        return reservations;
    }

    // ===== RECUPERER PAR ID =====

    public Reservation recupererParId(int id) throws SQLException {
        if (id <= 0) {
            throw new SQLException("ID reservation invalide.");
        }
        String query = "SELECT * FROM reservations WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    // njibou les reservations mta3 user specifique (demandeur wella proprietaire)
    public List<Reservation> recupererParUtilisateur(int userId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        if (userId <= 0) {
            return reservations;
        }
        String query = "SELECT * FROM reservations WHERE demandeur_id=? OR proprietaire_id=? ORDER BY date_creation DESC";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.setInt(2, userId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSet(rs));
                }
            }
        }
        return reservations;
    }

    /**
     * Alias pour recuperer() — conforme aux consignes du cours.
     */
    // Alias (meme methode, esm mokhtalef - bech nmatchiw les consignes mta3 el
    // cours)
    public List<Reservation> afficherTout() throws SQLException {
        return recuperer();
    }

    // commission = prix * 10%
    private double calculerCommission(double prixTotal) {
        return prixTotal * COMMISSION_TAUX;
    }

    /**
     * Auto-dérive le propriétaire depuis l'annonce si non défini.
     * Évite le crash "proprietaire obligatoire" quand l'appelant
     * ne le set pas explicitement.
     */
    // ken el proprietaire ma7attouch, njiboueh mel annonce (bech mayfailich)
    private void autoSetProprietaire(Reservation reservation) {
        if (reservation != null && reservation.getProprietaire() == null
                && reservation.getAnnonce() != null
                && reservation.getAnnonce().getProprietaire() != null) {
            reservation.setProprietaire(reservation.getAnnonce().getProprietaire());
        }
    }

    private void validateReservationForInsert(Reservation reservation) throws SQLException {
        validateReservationCommon(reservation);
    }

    private void validateReservationForUpdate(Reservation reservation) throws SQLException {
        validateReservationCommon(reservation);
        if (reservation.getId() <= 0) {
            throw new SQLException("ID reservation invalide.");
        }
    }

    // validation : nchoufou kol chay s7i7 9bal INSERT/UPDATE
    // (annonce mawjouda, demandeur mawjoud, dates s7a7, prix > 0)
    private void validateReservationCommon(Reservation reservation) throws SQLException {
        if (reservation == null) {
            throw new SQLException("Reservation obligatoire.");
        }
        if (reservation.getAnnonce() == null || reservation.getDemandeur() == null
                || reservation.getProprietaire() == null) {
            throw new SQLException("Annonce, demandeur et proprietaire sont obligatoires.");
        }
        if (reservation.getAnnonce().getId() <= 0 || reservation.getDemandeur().getId() <= 0
                || reservation.getProprietaire().getId() <= 0) {
            throw new SQLException("IDs annonce, demandeur ou proprietaire invalides.");
        }
        if (reservation.getDateDebut() == null || reservation.getDateFin() == null) {
            throw new SQLException("Les dates de debut et fin sont obligatoires.");
        }
        if (reservation.getDateDebut().isAfter(reservation.getDateFin())) {
            throw new SQLException("La date debut doit etre avant la date fin.");
        }
        if (reservation.getCaution() < 0) {
            throw new SQLException("Caution invalide.");
        }
    }

    // n7awlou el ResultSet (mel base) l objet Reservation (mapping)
    private Reservation mapResultSet(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("id"));
        reservation.setQuantite(rs.getInt("quantite"));
        reservation.setPrixTotal(rs.getDouble("prix_total"));
        reservation.setCaution(rs.getDouble("caution"));
        reservation.setStatut(StatutReservation.valueOf(rs.getString("statut")));
        reservation.setMessageDemande(rs.getString("message_demande"));
        reservation.setReponseProprietaire(rs.getString("reponse_proprietaire"));
        reservation.setContratUrl(rs.getString("contrat_url"));
        reservation.setContratSigne(rs.getBoolean("contrat_signe"));

        Date dateDebut = rs.getDate("date_debut");
        if (dateDebut != null) {
            reservation.setDateDebut(dateDebut.toLocalDate());
        }

        Date dateFin = rs.getDate("date_fin");
        if (dateFin != null) {
            reservation.setDateFin(dateFin.toLocalDate());
        }

        Timestamp dateDemande = rs.getTimestamp("date_demande");
        if (dateDemande != null) {
            reservation.setDateDemande(dateDemande.toLocalDateTime());
        }

        Timestamp dateReponse = rs.getTimestamp("date_reponse");
        if (dateReponse != null) {
            reservation.setDateReponse(dateReponse.toLocalDateTime());
        }

        Timestamp dateSignature = rs.getTimestamp("date_signature_contrat");
        if (dateSignature != null) {
            reservation.setDateSignatureContrat(dateSignature.toLocalDateTime());
        }

        int annonceId = rs.getInt("annonce_id");
        try {
            Annonce annonce = annonceService.recupererParId(annonceId);
            reservation.setAnnonce(annonce);
        } catch (SQLException e) {
            reservation.setAnnonce(null);
        }

        int demandeurId = rs.getInt("demandeur_id");
        try {
            User demandeur = userService.recupererParId(demandeurId);
            reservation.setDemandeur(demandeur);
        } catch (SQLException e) {
            reservation.setDemandeur(null);
        }

        int proprietaireId = rs.getInt("proprietaire_id");
        try {
            User proprietaire = userService.recupererParId(proprietaireId);
            reservation.setProprietaire(proprietaire);
        } catch (SQLException e) {
            reservation.setProprietaire(null);
        }

        return reservation;
    }

    // ===== Réservations envoyées par un demandeur =====
    public List<Reservation> recupererParDemandeur(int userId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        if (userId <= 0)
            return reservations;
        String query = "SELECT * FROM reservations WHERE demandeur_id=? ORDER BY date_creation DESC";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSet(rs));
                }
            }
        }
        return reservations;
    }

    // ===== Réservations reçues par un propriétaire =====
    public List<Reservation> recupererParProprietaire(int userId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        if (userId <= 0)
            return reservations;
        String query = "SELECT * FROM reservations WHERE proprietaire_id=? ORDER BY date_creation DESC";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSet(rs));
                }
            }
        }
        return reservations;
    }

    // ===== Accepter une réservation =====
    public void accepterReservation(int reservationId, String reponse) throws SQLException {
        String query = "UPDATE reservations SET statut=?, reponse_proprietaire=?, date_reponse=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, StatutReservation.ACCEPTEE.name());
            pst.setString(2, reponse);
            pst.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            pst.setInt(4, reservationId);
            pst.executeUpdate();
        }
    }

    // ===== Refuser une réservation =====
    public void refuserReservation(int reservationId, String reponse) throws SQLException {
        String query = "UPDATE reservations SET statut=?, reponse_proprietaire=?, date_reponse=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, StatutReservation.REFUSEE.name());
            pst.setString(2, reponse);
            pst.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            pst.setInt(4, reservationId);
            pst.executeUpdate();
        }
    }
}