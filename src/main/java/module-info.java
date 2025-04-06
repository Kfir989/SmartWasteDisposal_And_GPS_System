module com.example.proj {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.web;
    requires java.net.http;
    requires org.json;


    opens com.example.proj to javafx.fxml;
    exports com.example.proj;
}