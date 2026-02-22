package services;

import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class VerificationAdminService {

    public void updateUserVerification(int userId, String status, String reason, Double score) throws Exception {
        Connection cnx = MyDatabase.getInstance().getConnection();
        String sql = "UPDATE utilisateurs SET verification_status=?, verification_reason=?, verification_score=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, reason);
            if (score == null) {
                ps.setNull(3, java.sql.Types.DOUBLE);
            } else {
                ps.setDouble(3, score);
            }
            ps.setInt(4, userId);
            ps.executeUpdate();
        }
    }
}
