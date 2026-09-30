module com.example.prpyectobd2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    opens com.example.prpyectobd2.controller to javafx.fxml;

    opens com.example.prpyectobd2.model to javafx.fxml, javafx.base;

    opens com.example.prpyectobd2 to javafx.fxml;
    exports com.example.prpyectobd2;
    exports com.example.prpyectobd2.controller to javafx.fxml;
}