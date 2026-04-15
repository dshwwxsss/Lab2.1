module ru.itmo.lab21 {
    // Подключаем модули JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Открываем пакеты для доступа через отражение (FXML, JavaFX)
    opens app to javafx.fxml;
    opens cli to javafx.fxml;
    opens cli.command to javafx.fxml;
    opens domain to javafx.fxml;
    opens service to javafx.fxml;
    opens storage to javafx.fxml;
    opens validation to javafx.fxml;
    opens javafx to javafx.fxml;
    opens javafx.controller to javafx.fxml;

    // Экспортируем пакеты (делаем их видимыми)
    exports app;
    exports cli;
    exports cli.command;
    exports domain;
    exports service;
    exports storage;
    exports validation;
    exports javafx;
    exports javafx.controller;
}