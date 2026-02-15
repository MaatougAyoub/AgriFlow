package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {

    private  final  String URL="jdbc:mysql://localhost:3306/agriflow";
    private  final  String USER="root";
    private  final  String PASSWORD="";
    private Connection connection;
    private  static MyDatabase instance;


    private MyDatabase() {
        try {
            connection= DriverManager.getConnection(URL,USER,PASSWORD);
            System.out.println("Connection established");
        } catch (SQLException e) {
            throw new RuntimeException("DB connection failed: " + e.getMessage(), e);
        }
    }
    public static MyDatabase getInstance() {
        if(instance==null)
            instance=new MyDatabase();
        return instance;

    }

    public Connection getConnection() {
        return connection;
    }
}
