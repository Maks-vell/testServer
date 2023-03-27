module com.psu.testserver {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.psu.testserver to javafx.fxml;
    exports com.psu.testserver;
}