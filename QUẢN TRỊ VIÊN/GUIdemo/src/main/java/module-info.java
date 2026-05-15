module com.example.guidemo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.guidemo to javafx.fxml;
    exports com.example.guidemo;
}