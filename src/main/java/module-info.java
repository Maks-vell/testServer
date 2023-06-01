module com.psu.testserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires reflections;
    requires lombok;
    requires com.google.gson;


    opens com.psu.testserver to javafx.fxml;
    exports com.psu.testserver;
    exports com.psu.testserver.javafx.controller;
    opens com.psu.testserver.javafx.controller to javafx.fxml;
    exports com.psu.testserver.server;
    opens com.psu.testserver.server to javafx.fxml;
    exports com.psu.testserver.javafx;
    opens com.psu.testserver.javafx to javafx.fxml;
    exports com.psu.testserver.javafx.client;
    opens com.psu.testserver.javafx.client to javafx.fxml;
    opens com.psu.testserver.model to com.google.gson;
    exports com.psu.testserver.enums;
    opens com.psu.testserver.enums to com.google.gson;
}