package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    // Configuration de la base de données
    private static final String URL = "jdbc:mysql://localhost:3306/agriflow";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static MyDatabase instance;

    // Constructeur privé (Singleton)
    private MyDatabase() {
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Driver MySQL chargé avec succès");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Erreur: Driver MySQL non trouvé");
            e.printStackTrace();
        }
    }

    // Obtenir l'instance unique (Singleton)
    public static MyDatabase getInstance() {
        if (instance == null) {
            synchronized (MyDatabase.class) {
                if (instance == null) {
                    instance = new MyDatabase();
                }
            }
        }
        return instance;
    }

    /**
     * ✅ MÉTHODE CORRIGÉE : Créer une NOUVELLE connexion à chaque appel
     * Cela évite les problèmes de connexion fermée
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Nouvelle connexion MySQL établie");
            return connection;
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion MySQL: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Tester la connexion
     */
    public void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connexion MySQL (XAMPP) établie.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Échec de connexion MySQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fermer proprement une connexion
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("✅ Connexion MySQL fermée proprement");
                }
            } catch (SQLException e) {
                System.err.println("⚠️ Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }

    // Getters pour la configuration (utile pour debug)
    public static String getUrl() {
        return URL;
    }

    public static String getUser() {
        return USER;
    }
}
