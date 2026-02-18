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

        String sql = "SELECT id_culture, `nom-culture`, id_parcelle, quantite_eau FROM culture";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Culture(
                        rs.getInt("id_culture"),
                        rs.getString("nom-culture"),
                        rs.getInt("id_parcelle"),
                        rs.getFloat("quantite_eau")
                ));
            }
        }
        return list;
    }
}