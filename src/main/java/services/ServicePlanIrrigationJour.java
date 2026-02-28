package services;

import utils.MyConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ServicePlanIrrigationJour {

    private final Connection connection;

    public ServicePlanIrrigationJour() {
        connection = MyConnection.getInstance().getConnection();
    }

    public void saveDay(int planId, String day, float eauMm, int tempsMin, float tempC) throws SQLException {
        String sql = """
            INSERT INTO plans_irrigation_jour (plan_id, jour, eau_mm, temps_min, temp_c)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                eau_mm = VALUES(eau_mm),
                temps_min = VALUES(temps_min),
                temp_c = VALUES(temp_c)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, planId);
            ps.setString(2, day); // MON..SUN
            ps.setFloat(3, eauMm);
            ps.setInt(4, tempsMin);
            ps.setFloat(5, tempC);
            ps.executeUpdate();
        }
    }

    public void saveDay(int planId, String day, float eauMm, int tempsMin, float tempC, LocalDate semaineDebut) throws SQLException {
        String sql = """
            INSERT INTO plans_irrigation_jour (plan_id, jour, eau_mm, temps_min, temp_c, semaine_debut)
            VALUES (?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                eau_mm = VALUES(eau_mm),
                temps_min = VALUES(temps_min),
                temp_c = VALUES(temp_c)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, planId);
            ps.setString(2, day);
            ps.setFloat(3, eauMm);
            ps.setInt(4, tempsMin);
            ps.setFloat(5, tempC);
            ps.setDate(6, Date.valueOf(semaineDebut));

            ps.executeUpdate();
        }
    }


    public Map<String, float[]> loadAll(int planId, LocalDate semaineDebut) throws SQLException {
        Map<String, float[]> map = new HashMap<>();
        // Lecture incluant humidite et pluie
        String sql = "SELECT jour, eau_mm, temps_min, temp_c, humidite, pluie FROM plans_irrigation_jour WHERE plan_id = ? AND semaine_debut = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, planId);
            ps.setDate(2, java.sql.Date.valueOf(semaineDebut));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("jour"), new float[]{
                            rs.getFloat("eau_mm"),
                            (float) rs.getInt("temps_min"),
                            rs.getFloat("temp_c"),
                            rs.getFloat("humidite"),
                            rs.getFloat("pluie")
                    });
                }
            }
        }
        return map;
    }
    public void saveDayOptimized(int planId, String jourNom, float eau, int duree, float temp, float humidite, float pluie, LocalDate dateDebutSemaine) throws SQLException {
        String sql = "INSERT INTO plans_irrigation_jour (plan_id, jour, eau_mm, temps_min, temp_c, humidite, pluie, semaine_debut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "eau_mm = VALUES(eau_mm), " +
                "temps_min = VALUES(temps_min), " +
                "temp_c = VALUES(temp_c), " +
                "humidite = VALUES(humidite), " +
                "pluie = VALUES(pluie)";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, planId);
            pst.setString(2, jourNom);
            pst.setFloat(3, eau);
            pst.setInt(4, duree);
            pst.setFloat(5, temp);
            pst.setFloat(6, humidite);
            pst.setFloat(7, pluie);
            pst.setDate(8, java.sql.Date.valueOf(dateDebutSemaine));

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'enregistrement IA : " + e.getMessage());
            throw e;
        }
    }
}