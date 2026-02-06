package services;

import entities.CollabRequest;
import utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollabRequestService implements ICollabRequestService {

    @Override
    public long add(CollabRequest req) throws SQLException {
        String sql = "INSERT INTO collab_requests(requester_id, title, description, start_date, end_date, needed_people, status) " +
                     "VALUES (?,?,?,?,?,?,?)";
        
        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setLong(1, req.getRequesterId());
            ps.setString(2, req.getTitle());
            ps.setString(3, req.getDescription());
            ps.setDate(4, req.getStartDate() == null ? null : Date.valueOf(req.getStartDate()));
            ps.setDate(5, req.getEndDate() == null ? null : Date.valueOf(req.getEndDate()));
            ps.setInt(6, req.getNeededPeople());
            ps.setString(7, req.getStatus());
            
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return -1;
    }

    @Override
    public List<CollabRequest> findAll() throws SQLException {
        List<CollabRequest> list = new ArrayList<>();
        String sql = "SELECT id, requester_id, title, description, start_date, end_date, needed_people, status " +
                     "FROM collab_requests ORDER BY created_at DESC";
        
        try (Connection c = MyDatabase.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                CollabRequest r = new CollabRequest();
                r.setId(rs.getLong("id"));
                r.setRequesterId(rs.getLong("requester_id"));
                r.setTitle(rs.getString("title"));
                r.setDescription(rs.getString("description"));
                
                Date sd = rs.getDate("start_date");
                Date ed = rs.getDate("end_date");
                r.setStartDate(sd == null ? null : sd.toLocalDate());
                r.setEndDate(ed == null ? null : ed.toLocalDate());
                
                r.setNeededPeople(rs.getInt("needed_people"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        }
        return list;
    }

    @Override
    public CollabRequest findById(long id) throws SQLException {
        String sql = "SELECT id, requester_id, title, description, start_date, end_date, needed_people, status " +
                     "FROM collab_requests WHERE id=?";
        
        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CollabRequest r = new CollabRequest();
                    r.setId(rs.getLong("id"));
                    r.setRequesterId(rs.getLong("requester_id"));
                    r.setTitle(rs.getString("title"));
                    r.setDescription(rs.getString("description"));
                    
                    Date sd = rs.getDate("start_date");
                    Date ed = rs.getDate("end_date");
                    r.setStartDate(sd == null ? null : sd.toLocalDate());
                    r.setEndDate(ed == null ? null : ed.toLocalDate());
                    
                    r.setNeededPeople(rs.getInt("needed_people"));
                    r.setStatus(rs.getString("status"));
                    return r;
                }
            }
        }
        return null;
    }

    @Override
    public void update(CollabRequest entity) throws SQLException {
        String sql = "UPDATE collab_requests SET title=?, description=?, start_date=?, end_date=?, needed_people=?, status=? WHERE id=?";
        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, entity.getTitle());
            ps.setString(2, entity.getDescription());
            ps.setDate(3, entity.getStartDate() == null ? null : Date.valueOf(entity.getStartDate()));
            ps.setDate(4, entity.getEndDate() == null ? null : Date.valueOf(entity.getEndDate()));
            ps.setInt(5, entity.getNeededPeople());
            ps.setString(6, entity.getStatus());
            ps.setLong(7, entity.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateStatus(long id, String status) throws SQLException {
        String sql = "UPDATE collab_requests SET status=? WHERE id=?";
        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM collab_requests WHERE id=?";
        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<CollabRequest> findByStatus(String status) throws SQLException {
        List<CollabRequest> list = new ArrayList<>();
        String sql = "SELECT id, requester_id, title, description, start_date, end_date, needed_people, status " +
                     "FROM collab_requests WHERE status=? ORDER BY created_at DESC";
        
        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setString(1, status);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CollabRequest r = new CollabRequest();
                    r.setId(rs.getLong("id"));
                    r.setRequesterId(rs.getLong("requester_id"));
                    r.setTitle(rs.getString("title"));
                    r.setDescription(rs.getString("description"));
                    
                    Date sd = rs.getDate("start_date");
                    Date ed = rs.getDate("end_date");
                    r.setStartDate(sd == null ? null : sd.toLocalDate());
                    r.setEndDate(ed == null ? null : ed.toLocalDate());
                    
                    r.setNeededPeople(rs.getInt("needed_people"));
                    r.setStatus(rs.getString("status"));
                    list.add(r);
                }
            }
        }
        return list;
    }
}
