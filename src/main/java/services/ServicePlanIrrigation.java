package services;

import entities.PlanIrrigation;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServicePlanIrrigation implements IService<PlanIrrigation> {

    private final Connection connection;

    public ServicePlanIrrigation() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(PlanIrrigation plan) throws SQLException {
        String sql = "INSERT INTO plans_irrigation (id_culture, nom_culture, date_demande, statut, "
                + "volume_eau_propose, temp_irrigation, temp, donnees_meteo_json) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, plan.getIdCulture());
            ps.setString(2, plan.getNomCulture());

            LocalDateTime dd = (plan.getDateDemande() != null) ? plan.getDateDemande() : LocalDateTime.now();
            ps.setTimestamp(3, Timestamp.valueOf(dd));
            ps.setString(4, plan.getStatut());
            ps.setFloat(5, plan.getVolumeEauPropose());

            if (plan.getTempIrrigation() != null) {
                ps.setTime(6, Time.valueOf(plan.getTempIrrigation()));
            } else {
                ps.setNull(6, Types.TIME);
            }

            if (plan.getTemp() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(plan.getTemp()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }

            ps.setString(8, plan.getDonneesMeteojson());
            ps.executeUpdate();
            System.out.println("✅ Plan d'irrigation ajouté !");
        }
    }

    @Override
    public void modifier(PlanIrrigation plan) throws SQLException {
        String sql = "UPDATE plans_irrigation SET nom_culture = ?, statut = ?, volume_eau_propose = ?, "
                + "temp_irrigation = ?, temp = ?, donnees_meteo_json = ? WHERE plan_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, plan.getNomCulture());
            ps.setString(2, plan.getStatut());
            ps.setFloat(3, plan.getVolumeEauPropose());

            if (plan.getTempIrrigation() != null) {
                ps.setTime(4, Time.valueOf(plan.getTempIrrigation()));
            } else {
                ps.setNull(4, Types.TIME);
            }

            if (plan.getTemp() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(plan.getTemp()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            ps.setString(6, plan.getDonneesMeteojson());
            ps.setInt(7, plan.getPlanId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(PlanIrrigation plan) throws SQLException {
        String sql = "DELETE FROM plans_irrigation WHERE plan_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, plan.getPlanId());
            ps.executeUpdate();
        }
    }

    /**
     * Récupère TOUS les plans (pour l'expert)
     */
    @Override
    public List<PlanIrrigation> recuperer() throws SQLException {
        List<PlanIrrigation> plans = new ArrayList<>();
        String sql = "SELECT plan_id, id_culture, nom_culture, date_demande, statut, "
                + "volume_eau_propose, temp_irrigation, temp, donnees_meteo_json "
                + "FROM plans_irrigation";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                plans.add(mapResultSetToPlan(rs));
            }
        }
        return plans;
    }

    /**
     * ✅ Récupère les plans d'un utilisateur connecté
     *
     * Chaîne : utilisateur → parcelle.agriculteur_id → cultures.parcelle_id → plans_irrigation.id_culture
     */
    public List<PlanIrrigation> recupererParUtilisateur(int userId) throws SQLException {
        List<PlanIrrigation> plans = new ArrayList<>();

        String sql = "SELECT p.plan_id, p.id_culture, p.nom_culture, p.date_demande, p.statut, "
                + "p.volume_eau_propose, p.temp_irrigation, p.temp, p.donnees_meteo_json "
                + "FROM plans_irrigation p "
                + "INNER JOIN cultures c ON p.id_culture = c.id "
                + "INNER JOIN parcelle pa ON c.parcelle_id = pa.id "
                + "WHERE pa.agriculteur_id = ? "
                + "ORDER BY p.date_demande DESC";

        System.out.println("🔍 Recherche plans pour utilisateur ID = " + userId);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                plans.add(mapResultSetToPlan(rs));
                System.out.println("✅ Plan : id=" + rs.getInt("plan_id") +
                        " | culture=" + rs.getString("nom_culture") +
                        " | statut=" + rs.getString("statut"));
            }

            System.out.println("📊 Total plans : " + plans.size());
        }
        return plans;
    }

    public int getLastPlanIdByCulture(int idCulture) throws SQLException {
        String sql = "SELECT plan_id FROM plans_irrigation WHERE id_culture = ? ORDER BY plan_id DESC LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idCulture);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("plan_id");
            }
        }
        return 0;
    }

    public int createDraftPlanAndReturnId(int idCulture, float volumeEauPropose) throws SQLException {
        String sql = "INSERT INTO plans_irrigation (id_culture, date_demande, statut, volume_eau_propose, "
                + "temp_irrigation, temp, donnees_meteo_json) "
                + "VALUES (?, NOW(), ?, ?, '00:00:00', NOW(), NULL)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idCulture);
            ps.setString(2, "brouillon");
            ps.setFloat(3, volumeEauPropose);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Impossible de créer un plan brouillon.");
    }

    private PlanIrrigation mapResultSetToPlan(ResultSet rs) throws SQLException {
        int id = rs.getInt("plan_id");
        int idCulture = rs.getInt("id_culture");
        String nomCulture = rs.getString("nom_culture");

        Timestamp ddTs = rs.getTimestamp("date_demande");
        LocalDateTime dateDemande = ddTs != null ? ddTs.toLocalDateTime() : null;

        String statut = rs.getString("statut");
        float volume = rs.getFloat("volume_eau_propose");

        Time t = rs.getTime("temp_irrigation");
        LocalTime tempIrrigation = (t != null) ? t.toLocalTime() : null;

        Timestamp tempTs = rs.getTimestamp("temp");
        LocalDateTime temp = (tempTs != null) ? tempTs.toLocalDateTime() : null;

        String meteo = rs.getString("donnees_meteo_json");

        PlanIrrigation plan = new PlanIrrigation(id, nomCulture, dateDemande, statut, volume, meteo);
        plan.setIdCulture(idCulture);
        plan.setTempIrrigation(tempIrrigation);
        plan.setTemp(temp);

        return plan;
    }
}