package tn.esprit.agriflow.collab;

import tn.esprit.agriflow.config.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollabApplicationDAO {

    public long add(CollabApplication app) throws SQLException {
        String sql = "INSERT INTO collab_applications(request_id, candidate_id, message, status) VALUES (?,?,?,?)";
        
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setLong(1, app.getRequestId());
            ps.setLong(2, app.getCandidateId());
            ps.setString(3, app.getMessage());
            ps.setString(4, app.getStatus());
            
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return -1;
    }

    public List<CollabApplication> findByRequestId(long requestId) throws SQLException {
        List<CollabApplication> list = new ArrayList<>();
        String sql = "SELECT id, request_id, candidate_id, message, status FROM collab_applications WHERE request_id=?";
        
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setLong(1, requestId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CollabApplication a = new CollabApplication();
                    a.setId(rs.getLong("id"));
                    a.setRequestId(rs.getLong("request_id"));
                    a.setCandidateId(rs.getLong("candidate_id"));
                    a.setMessage(rs.getString("message"));
                    a.setStatus(rs.getString("status"));
                    list.add(a);
                }
            }
        }
        return list;
    }

    public void updateStatus(long id, String status) throws SQLException {
        String sql = "UPDATE collab_applications SET status=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM collab_applications WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}
