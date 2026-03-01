module agriflow{

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;
    requires javafx.graphics;

    requires java.sql;
    requires java.desktop;
    requires java.prefs;
    requires java.net.http;

    requires org.json;
    requires jdk.jsobject;
    requires stripe.java;

    requires kernel;
    requires layout;
    requires io;

    requires tess4j;   // ⭐ OCR
    requires jbcrypt;   // ⭐ PASSWORD
    requires com.google.gson;

    opens controllers to javafx.fxml;
    opens mains to javafx.fxml;
    opens services to javafx.fxml;
    opens services.ocr to javafx.fxml;
    // Allow JavaFX (base/reflective) and FXML loader to access entity properties reflectively
    opens entities to javafx.base, javafx.fxml;

    exports mains;
}