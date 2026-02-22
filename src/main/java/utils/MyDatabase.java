package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {

    //private static final String URL = "jdbc:mysql://localhost:3306/db-gusers";
    private static final String URL = "jdbc:mysql://localhost:3306/agriflow5";
    //amen
    //private static final String URL = "jdbc:mysql:// localhost:3306/agriflow?useSSL=false&serverTimezone=UTC";


    private static final String USER = "root";
    private static final String PASSWORD = "";

    private Connection connection;
    private static MyDatabase instance;

    private MyDatabase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection established");

            // Ensure DB has required columns even when services/tests run without MainFX
            DbMigrations.ensureUserVerificationColumns(connection);
        } catch (SQLException e) {
            // mieux pour debug:
            e.printStackTrace();
        }
    }

    // Pattern Singleton bech manchallatouch barcha connexions (optimized)
    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}