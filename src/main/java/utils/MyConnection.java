package utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private  final  String URL="jdbc:mysql://localhost:3306/AgriFlow";
    private  final  String USER="root";
    private  final  String PASSWORD="";
    private Connection connection;
    private  static MyConnection instance;


    private MyConnection() {
        try {
            connection= DriverManager.getConnection(URL,USER,PASSWORD);
            System.out.println("Connection established");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static MyConnection getInstance() {
        if(instance==null)
            instance=new MyConnection();
        return instance;

    }

    public Connection getConnection() {
        return connection;
    }

}
