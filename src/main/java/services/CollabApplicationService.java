package services;

import entities.CollabApplication;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollabApplicationService {

    private final Connection connection;

    public CollabApplicationService() {
        try {
            this.connection = MyDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("❌ Erreur initialisation connexion : " + e.getMessage());
            throw new RuntimeException("Impossible de se connecter à la base de données", e);
        }
    }

    /**
     * Ajouter une nouvelle candidature
     */
    public long add(CollabApplication app) throws SQLException {
        String sql = "INSERT INTO collab_applications(" +
                "request_id, candidate_id, full_name, phone, email, " +
                "years_of_experience, motivation, expected_salary, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, app.getRequestId());
            ps.setLong(2, app.getCandidateId());
            ps.setString(3, app.getFullName());
            ps.setString(4, app.getPhone());
            ps.setString(5, app.getEmail());
            ps.setInt(6, app.getYearsOfExperience());
            ps.setString(7, app.getMotivation());
            ps.setDouble(8, app.getExpectedSalary());
            ps.setString(9, app.getStatus());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }

        return -1;
    }

    /**
     * Récupérer toutes les candidatures
     */
    public List<CollabApplication> findAll() throws SQLException {
        List<CollabApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM collab_applications ORDER BY applied_at DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                applications.add(mapResultSet(rs));
            }
        }

        return applications;
    }

    /**
     * Récupérer les candidatures d'un utilisateur
     */
    public List<CollabApplication> findByCandidateId(long candidateId) throws SQLException {
        List<CollabApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM collab_applications WHERE candidate_id = ? ORDER BY applied_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, candidateId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapResultSet(rs));
                }
            }
        }

        return applications;
    }

    /**
     * Récupérer les candidatures pour une demande spécifique
     */
    public List<CollabApplication> findByRequestId(long requestId) throws SQLException {
        List<CollabApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM collab_applications WHERE request_id = ? ORDER BY applied_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, requestId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapResultSet(rs));
                }
            }
        }

        return applications;
    }

    /**
     * Récupérer une candidature par ID
     */
    public CollabApplication findById(long id) throws SQLException {
        String sql = "SELECT * FROM collab_applications WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }

        return null;
    }

    /**
     * Mettre à jour le statut d'une candidature
     */
    public void updateStatus(long id, String status) throws SQLException {
        String sql = "UPDATE collab_applications SET status = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    /**
     * Supprimer une candidature
     */
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM collab_applications WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Vérifier si un candidat a déjà postulé à une demande
     */
    public boolean hasApplied(long candidateId, long requestId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM collab_applications WHERE candidate_id = ? AND request_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, candidateId);
            ps.setLong(2, requestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    /**
     * Mapper un ResultSet vers un objet CollabApplication
     */
    private CollabApplication mapResultSet(ResultSet rs) throws SQLException {
        CollabApplication app = new CollabApplication();
        app.setId(rs.getLong("id"));
        app.setRequestId(rs.getLong("request_id"));
        app.setCandidateId(rs.getLong("candidate_id"));
        app.setFullName(rs.getString("full_name"));
        app.setPhone(rs.getString("phone"));
        app.setEmail(rs.getString("email"));
        app.setYearsOfExperience(rs.getInt("years_of_experience"));
        app.setMotivation(rs.getString("motivation"));
        app.setExpectedSalary(rs.getDouble("expected_salary"));
        app.setStatus(rs.getString("status"));
        return app;
    }
    /**
     * Compte le nombre de candidatures pour une demande
     */
    public int countApplicationsByRequestId(Long requestId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM collab_applications WHERE request_id = ?";
        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, requestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    /**
     * Récupère toutes les candidatures pour une demande
     */
    public List<CollabApplication> getApplicationsByRequestId(Long requestId) throws SQLException {
        List<CollabApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM collab_applications WHERE request_id = ? ORDER BY applied_at DESC";

        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, requestId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CollabApplication app = new CollabApplication();
                app.setId(rs.getLong("id"));
                app.setRequestId(rs.getLong("request_id"));
                app.setCandidateId(rs.getLong("candidate_id"));
                app.setFullName(rs.getString("full_name"));
                app.setPhone(rs.getString("phone"));
                app.setEmail(rs.getString("email"));
                app.setYearsOfExperience(rs.getInt("years_of_experience"));
                app.setMotivation(rs.getString("motivation"));
                app.setExpectedSalary(rs.getDouble("expected_salary"));
                app.setStatus(rs.getString("status"));
                applications.add(app);
            }
        }
        return applications;
    }

    /**
     * Approuve une candidature
     */
    public void approveApplication(Long applicationId) throws SQLException {
        String sql = "UPDATE collab_applications SET status = 'APPROVED', updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, applicationId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Candidature approuvée : ID " + applicationId);
            }
        }
    }

    /**
     * Rejette une candidature
     */
    public void rejectApplication(Long applicationId) throws SQLException {
        String sql = "UPDATE collab_applications SET status = 'REJECTED', updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection c = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, applicationId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("❌ Candidature rejetée : ID " + applicationId);
            }
        }
    }
}
