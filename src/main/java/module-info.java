module ru.itmo.lab21 {
    requires javafx.controls;
    requires javafx.fxml;

    opens javafx.controller to javafx.fxml; //разрешает JavaFX заглядывать внутрь твоего контроллера

    exports javafx;
}