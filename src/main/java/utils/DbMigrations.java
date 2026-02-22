package utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public final class DbMigrations {

    private DbMigrations() {
    }

    public static void ensureUserVerificationColumns(Connection cnx) {
        try {
            if (cnx == null) return;

            addColumnIfMissing(cnx, "utilisateurs", "verification_status",
                    "ALTER TABLE utilisateurs ADD COLUMN verification_status VARCHAR(20) NOT NULL DEFAULT 'APPROVED'");
            addColumnIfMissing(cnx, "utilisateurs", "verification_reason",
                    "ALTER TABLE utilisateurs ADD COLUMN verification_reason VARCHAR(500) NULL");
            addColumnIfMissing(cnx, "utilisateurs", "verification_score",
                    "ALTER TABLE utilisateurs ADD COLUMN verification_score DOUBLE NULL");
            addColumnIfMissing(cnx, "utilisateurs", "nom_ar",
                    "ALTER TABLE utilisateurs ADD COLUMN nom_ar VARCHAR(255) NULL");
            addColumnIfMissing(cnx, "utilisateurs", "prenom_ar",
                    "ALTER TABLE utilisateurs ADD COLUMN prenom_ar VARCHAR(255) NULL");

        } catch (Exception e) {
            // migration failure should not crash the UI, but should be visible in console
            e.printStackTrace();
        }
    }

    private static void addColumnIfMissing(Connection cnx, String table, String column, String alterSql) throws Exception {
        DatabaseMetaData meta = cnx.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, table, column)) {
            if (rs.next()) {
                return;
            }
        }
        try (Statement st = cnx.createStatement()) {
            st.execute(alterSql);
        }
    }
}
