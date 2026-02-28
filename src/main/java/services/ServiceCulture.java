package services;

import entities.Culture;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCulture implements IService<Culture> {

    private final Connection connection;

    public ServiceCulture() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Culture c) throws SQLException {

        String req = "INSERT INTO cultures(" +
                "parcelle_id, proprietaire_id, nom, type_culture, superficie, etat, " +
                "date_recolte, recolte_estime, " +
                "id_acheteur, date_vente, date_publication, prix_vente" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(req)) {

            ps.setInt(1, c.getParcelleId());
            ps.setInt(2, c.getProprietaireId());
            ps.setString(3, c.getNom());
            ps.setString(4, c.getTypeCulture().name());
            ps.setDouble(5, c.getSuperficie());
            ps.setString(6, c.getEtat().name());

            // date_recolte nullable ?
            if (c.getDateRecolte() != null) ps.setDate(7, c.getDateRecolte());
            else ps.setNull(7, Types.DATE);

            // recolte_estime nullable
            if (c.getRecolteEstime() != null) ps.setDouble(8, c.getRecolteEstime());
            else ps.setNull(8, Types.DOUBLE);

            // ===== champs vente (tous nullable) =====
            if (c.getIdAcheteur() != null) ps.setInt(9, c.getIdAcheteur());
            else ps.setNull(9, Types.INTEGER);

            if (c.getDateVente() != null) ps.setDate(10, c.getDateVente());
            else ps.setNull(10, Types.DATE);

            if (c.getDatePublication() != null) ps.setDate(11, c.getDatePublication());
            else ps.setNull(11, Types.DATE);

            if (c.getPrixVente() != null) ps.setDouble(12, c.getPrixVente());
            else ps.setNull(12, Types.DOUBLE);

            ps.executeUpdate();
        }
    }

    @Override
    public void modifier(Culture c) throws SQLException {

        String req = "UPDATE cultures SET " +
                "parcelle_id=?, proprietaire_id=?, nom=?, type_culture=?, superficie=?, etat=?, " +
                "date_recolte=?, recolte_estime=?, " +
                "id_acheteur=?, date_vente=?, date_publication=?, prix_vente=? " +
                "WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {

            ps.setInt(1, c.getParcelleId());
            ps.setInt(2, c.getProprietaireId());
            ps.setString(3, c.getNom());
            ps.setString(4, c.getTypeCulture().name());
            ps.setDouble(5, c.getSuperficie());
            ps.setString(6, c.getEtat().name());

            if (c.getDateRecolte() != null) ps.setDate(7, c.getDateRecolte());
            else ps.setNull(7, Types.DATE);

            if (c.getRecolteEstime() != null) ps.setDouble(8, c.getRecolteEstime());
            else ps.setNull(8, Types.DOUBLE);

            if (c.getIdAcheteur() != null) ps.setInt(9, c.getIdAcheteur());
            else ps.setNull(9, Types.INTEGER);

            if (c.getDateVente() != null) ps.setDate(10, c.getDateVente());
            else ps.setNull(10, Types.DATE);

            if (c.getDatePublication() != null) ps.setDate(11, c.getDatePublication());
            else ps.setNull(11, Types.DATE);

            if (c.getPrixVente() != null) ps.setDouble(12, c.getPrixVente());
            else ps.setNull(12, Types.DOUBLE);

            ps.setInt(13, c.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(Culture c) throws SQLException {
        String req = "DELETE FROM cultures WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, c.getId());
            ps.executeUpdate();
        }
        System.out.println("culture supprimée");
    }

    @Override
    public List<Culture> recuperer() throws SQLException {
        List<Culture> cultures = new ArrayList<>();
        String req = "SELECT * FROM cultures";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {

                // Gestion nulls (DATE et champs vente)
                Date dateRecolte = rs.getDate("date_recolte");

                double re = rs.getDouble("recolte_estime");
                Double recolteEstime = rs.wasNull() ? null : re;

                int ia = rs.getInt("id_acheteur");
                Integer idAcheteur = rs.wasNull() ? null : ia;

                Date dateVente = rs.getDate("date_vente");
                Date datePublication = rs.getDate("date_publication");

                double pv = rs.getDouble("prix_vente");
                Double prixVente = rs.wasNull() ? null : pv;

                Culture culture = new Culture(
                        rs.getInt("id"),
                        rs.getInt("parcelle_id"),
                        rs.getInt("proprietaire_id"),
                        rs.getString("nom"),
                        Culture.TypeCulture.valueOf(rs.getString("type_culture")),
                        rs.getDouble("superficie"),
                        Culture.Etat.valueOf(rs.getString("etat")),
                        dateRecolte,
                        recolteEstime,
                        rs.getTimestamp("date_creation"),
                        idAcheteur,
                        dateVente,
                        datePublication,
                        prixVente
                );

                cultures.add(culture);
            }
        }

        return cultures;
    }

    // =========================
    // Méthodes pratiques "vente"
    // =========================

    /** Publie une culture en vente (etat=EN_VENTE) */
    public void publierEnVente(int cultureId, double prixVente, Date datePublication) throws SQLException {
        String req = "UPDATE cultures SET etat=?, prix_vente=?, date_publication=?, id_acheteur=NULL, date_vente=NULL " +
                "WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, Culture.Etat.EN_VENTE.name());
            ps.setDouble(2, prixVente);

            if (datePublication != null) ps.setDate(3, datePublication);
            else ps.setNull(3, Types.DATE);

            ps.setInt(4, cultureId);
            ps.executeUpdate();
        }
    }

    /** Marque une culture vendue (etat=VENDUE) */
    public void marquerVendue(int cultureId, int idAcheteur, Date dateVente) throws SQLException {
        String req = "UPDATE cultures SET etat=?, id_acheteur=?, date_vente=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, Culture.Etat.VENDUE.name());
            ps.setInt(2, idAcheteur);

            if (dateVente != null) ps.setDate(3, dateVente);
            else ps.setNull(3, Types.DATE);

            ps.setInt(4, cultureId);
            ps.executeUpdate();
        }
    }

    /** Retire une culture de la vente (reset champs vente). etatApresRetrait = EN_COURS ou RECOLTEE selon ton choix */
    public void retirerDeVente(int cultureId, Culture.Etat etatApresRetrait) throws SQLException {
        String req = "UPDATE cultures SET etat=?, prix_vente=NULL, date_publication=NULL, id_acheteur=NULL, date_vente=NULL " +
                "WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, etatApresRetrait.name());
            ps.setInt(2, cultureId);
            ps.executeUpdate();
        }
    }

    /** Retourne uniquement les cultures dont l'état est EN_VENTE */
    public List<Culture> recupererCulturesEnVente() throws SQLException {
        List<Culture> cultures = new ArrayList<>();
        String req = "SELECT * FROM cultures WHERE etat=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, Culture.Etat.EN_VENTE.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Date dateRecolte = rs.getDate("date_recolte");

                    double re = rs.getDouble("recolte_estime");
                    Double recolteEstime = rs.wasNull() ? null : re;

                    int ia = rs.getInt("id_acheteur");
                    Integer idAcheteur = rs.wasNull() ? null : ia;

                    Date dateVente = rs.getDate("date_vente");
                    Date datePublication = rs.getDate("date_publication");

                    double pv = rs.getDouble("prix_vente");
                    Double prixVente = rs.wasNull() ? null : pv;

                    Culture culture = new Culture(
                            rs.getInt("id"),
                            rs.getInt("parcelle_id"),
                            rs.getInt("proprietaire_id"),
                            rs.getString("nom"),
                            Culture.TypeCulture.valueOf(rs.getString("type_culture")),
                            rs.getDouble("superficie"),
                            Culture.Etat.valueOf(rs.getString("etat")),
                            dateRecolte,
                            recolteEstime,
                            rs.getTimestamp("date_creation"),
                            idAcheteur,
                            dateVente,
                            datePublication,
                            prixVente
                    );

                    cultures.add(culture);
                }
            }
        }

        return cultures;
    }
    public Culture recupererParId(int id) throws SQLException {
        String sql = "SELECT * FROM cultures WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Date dateRecolte = rs.getDate("date_recolte");

                double re = rs.getDouble("recolte_estime");
                Double recolteEstime = rs.wasNull() ? null : re;

                int ia = rs.getInt("id_acheteur");
                Integer idAcheteur = rs.wasNull() ? null : ia;

                Date dateVente = rs.getDate("date_vente");
                Date datePublication = rs.getDate("date_publication");

                double pv = rs.getDouble("prix_vente");
                Double prixVente = rs.wasNull() ? null : pv;

                return new Culture(
                        rs.getInt("id"),
                        rs.getInt("parcelle_id"),
                        rs.getInt("proprietaire_id"),
                        rs.getString("nom"),
                        Culture.TypeCulture.valueOf(rs.getString("type_culture")),
                        rs.getDouble("superficie"),
                        Culture.Etat.valueOf(rs.getString("etat")),
                        dateRecolte,
                        recolteEstime,
                        rs.getTimestamp("date_creation"),
                        idAcheteur,
                        dateVente,
                        datePublication,
                        prixVente
                );
            }
        }
    }

    public boolean acheterAtomic(int cultureId, int acheteurId, Date dateVente) throws SQLException {
        String sql =
                "UPDATE cultures " +
                        "SET etat=?, id_acheteur=?, date_vente=? " +
                        "WHERE id=? AND etat=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, Culture.Etat.VENDUE.name());
            ps.setInt(2, acheteurId);
            ps.setDate(3, dateVente);
            ps.setInt(4, cultureId);
            ps.setString(5, Culture.Etat.EN_VENTE.name());

            int updated = ps.executeUpdate();
            return updated == 1; // true => achat réussi ; false => déjà vendu / plus en vente
        }
    }
}