module com.agriflow.marketplace {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;
    requires org.json;
    requires kernel;
    requires layout;
    requires io;

    opens com.agriflow.marketplace to javafx.fxml;
    opens com.agriflow.marketplace.controllers to javafx.fxml;
    opens com.agriflow.marketplace.models to javafx.base;

    exports com.agriflow.marketplace;
}
