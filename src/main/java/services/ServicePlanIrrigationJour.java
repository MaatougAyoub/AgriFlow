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

    public Map<String, float[]> loadAll(int planId) throws SQLException {
        // day -> [eau, timeMin, temp]
        Map<String, float[]> map = new HashMap<>();

        String sql = "SELECT jour, eau_mm, temps_min, temp_c FROM plans_irrigation_jour WHERE plan_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, planId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String day = rs.getString("jour");
                    float eau = rs.getFloat("eau_mm");
                    int timeMin = rs.getInt("temps_min");
                    float temp = rs.getFloat("temp_c");
                    map.put(day, new float[]{eau, timeMin, temp});
                }
            }
        }
        return map;
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

        String sql = "SELECT jour, eau_mm, temps_min, temp_c FROM plans_irrigation_jour WHERE plan_id = ? AND semaine_debut = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, planId);
            ps.setDate(2, Date.valueOf(semaineDebut)); // ✅ On filtre par semaine

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String day = rs.getString("jour");
                    float eau = rs.getFloat("eau_mm");
                    int timeMin = rs.getInt("temps_min");
                    float temp = rs.getFloat("temp_c");
                    map.put(day, new float[]{eau, timeMin, temp});
                }
            }
        }
        return map;
    }
}