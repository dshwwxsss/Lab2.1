package javafx.controller; //создаёт модальное окно входа/регистрации

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.AuthService;
import validation.ValidationException;

public class LoginDialog {

    private AuthService authService;
    private Stage stage;
    private boolean loginSuccess = false;

    public LoginDialog(AuthService authService) {
        this.authService = authService;
    }

    public boolean showAndWait() {
        stage = new Stage();
        stage.setTitle("Авторизация");
        stage.setResizable(false); //изменение размера

        TextField loginField = new TextField(); //одностр текст поле
        loginField.setPromptText("Логин");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");

        Button loginButton = new Button("Вход");
        Button registerButton = new Button("Регистрация");
        Button cancelButton = new Button("Отмена");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Логин:"), 0, 0);
        grid.add(loginField, 1, 0);
        grid.add(new Label("Пароль:"), 0, 1);
        grid.add(passwordField, 1, 1);

        VBox buttonBox = new VBox(10, loginButton, registerButton, cancelButton);
        buttonBox.setPadding(new Insets(10)); // внешние отступы

        VBox root = new VBox(10, grid, buttonBox);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 300, 250);
        stage.setScene(scene); //прикрепляем сцену к окну

        loginButton.setOnAction(e -> handleLogin(loginField.getText(), passwordField.getText()));  //лямбда выражение, код, который нужно выполнить (тело), краткая запись функционального интерфейса EventHandler
        registerButton.setOnAction(e -> handleRegister(loginField.getText(), passwordField.getText()));
        cancelButton.setOnAction(e -> { //"отмена"
            loginSuccess = false;
            stage.close();
        });

        stage.showAndWait();
        return loginSuccess;
    }

    private void handleLogin(String login, String password) {
        try {
            authService.login(login, password); // хранится ссылка на сервис аутентификации
            loginSuccess = true;
            stage.close();
        } catch (ValidationException e) {
            showAlert("Ошибка входа", e.getMessage());
        }
    }

    private void handleRegister(String login, String password) {
        if (login.trim().isEmpty() || password.trim().isEmpty()) {
            showAlert("Ошибка", "Логин и пароль не могут быть пустыми");
            return;
        }

        TextInputDialog confirmDialog = new TextInputDialog(); //ok/cancel и поле
        confirmDialog.setTitle("Подтверждение");
        confirmDialog.setHeaderText("Повторите пароль"); //под заголовком
        confirmDialog.setContentText("Пароль:"); //над полем

        String confirmPassword = confirmDialog.showAndWait().orElse(null); //ждем, возвращаем контейнер, объявляем переменную для хранения введённого пароля
        if (confirmPassword == null || !confirmPassword.equals(password)) {
            showAlert("Ошибка", "Пароли не совпадают");
            return;
        }

        try {
            authService.register(login, password);
            showAlert("Успех", "Пользователь " + login + " зарегистрирован!");
            authService.login(login, password); //автоматически выполняем вход
            loginSuccess = true;
            stage.close();
        } catch (ValidationException e) {
            showAlert("Ошибка регистрации", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message); // класс JavaFX для стандартных диалогов (с иконками)
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
