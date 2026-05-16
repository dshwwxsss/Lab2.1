module ru.itmo.lab21 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens javafx.controller to javafx.fxml;

    exports javafx;
}