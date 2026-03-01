package mains;

//import utils.MyConnection;
import utils.MyDatabase;

public class TestConnexion {
    public static void main(String[] args) {
        MyDatabase.getInstance().getConnection();
    }
}