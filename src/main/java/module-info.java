module com.example.prpyectobd2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.prpyectobd2 to javafx.fxml;
    exports com.example.prpyectobd2;
}