module agriflow.planirrigation {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.net.http;
    requires org.json;

    opens controllers to javafx.fxml;


    opens mains to javafx.fxml;


    exports mains;
}