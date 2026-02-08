package services;

import entities.CollabApplication;
import utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollabApplicationService implements ICollabApplicationService {

    @Override
    public long add(CollabApplication app) throws SQLException {
        String sql = "INSERT INTO collab_applications(request_id, candidate_id, message, status) VALUES (?,?,?,?)";
        
        try (Connection c = MyDatabase.getInstance().getConnection();
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

    @Override
    public List<CollabApplication> findAll() throws SQLException {
        List<CollabApplication> list = new ArrayList<>();
        String sql = "SELECT id, request_id, candidate_id, message, status FROM collab_applications";
        
        try (Connection c = MyDatabase.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
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
        return list;
    }

    @Override
    public CollabApplication findById(long id) throws SQLException {
        String sql = "SELECT id, request_id, candidate_id, message, status FROM collab_applications WHERE id=?";
        
        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CollabApplication a = new CollabApplication();
                    a.setId(rs.getLong("id"));
                    a.setRequestId(rs.getLong("request_id"));
                    a.setCandidateId(rs.getLong("candidate_id"));
                    a.setMessage(rs.getString("message"));
                    a.setStatus(rs.getString("status"));
                    return a;
                }
            }
        }
        return null;
    }

    @Override
    public void update(CollabApplication entity) throws SQLException {
        String sql = "UPDATE collab_applications SET request_id=?, candidate_id=?, message=?, status=? WHERE id=?";
        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, entity.getRequestId());
            ps.setLong(2, entity.getCandidateId());
            ps.setString(3, entity.getMessage());
            ps.setString(4, entity.getStatus());
            ps.setLong(5, entity.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM collab_applications WHERE id=?";
        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<CollabApplication> findByRequestId(long requestId) throws SQLException {
        List<CollabApplication> list = new ArrayList<>();
        String sql = "SELECT id, request_id, candidate_id, message, status FROM collab_applications WHERE request_id=?";
        
        try (Connection c = MyDatabase.getInstance().getConnection();
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

    @Override
    public void updateStatus(long id, String status) throws SQLException {
        String sql = "UPDATE collab_applications SET status=? WHERE id=?";
        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }
}
