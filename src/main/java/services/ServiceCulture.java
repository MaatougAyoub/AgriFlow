package services;

import entities.Culture;
import utils.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCulture {
    private final Connection connection;

    public ServiceCulture() {
        connection = MyConnection.getInstance().getConnection();
    }

    public List<Culture> recupererCultures() throws SQLException {
        List<Culture> list = new ArrayList<>();

        // Jointure pour récupérer la superficie de la table parcelle
        String sql = "SELECT c.id, c.nom, c.parcelle_id, c.type_culture, p.superficie " +
                "FROM cultures c " +
                "JOIN parcelle p ON c.parcelle_id = p.id";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                // Conversion sécurisée du type_culture (String -> Enum)
                String typeStr = rs.getString("type_culture");
                Culture.TypeCulture typeEnum = Culture.TypeCulture.AUTRE;
                if (typeStr != null) {
                    try {
                        typeEnum = Culture.TypeCulture.valueOf(typeStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Type inconnu: " + typeStr);
                    }
                }

                Culture c = new Culture(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("parcelle_id"),
                        rs.getFloat("superficie"),
                        typeEnum
                );
                list.add(c);
            }
        }
        return list;
    }
}