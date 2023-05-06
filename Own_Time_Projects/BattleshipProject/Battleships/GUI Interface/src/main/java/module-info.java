module com.example.trying {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.trying to javafx.fxml;
    exports com.example.trying;
}