package javafx.controller;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.AuthService;
import validation.ValidationException;

public class LoginDialog {

    private final AuthService authService;
    private boolean loginSuccess = false;

    public LoginDialog(AuthService authService) {
        this.authService = authService;
    }

    public boolean showAndWait() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Авторизация");
        dialog.setResizable(false);

        TextField loginField = new TextField();
        loginField.setPromptText("Логин");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
        errorLabel.setVisible(false);
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(250);

        Button loginButton = new Button("Вход");
        Button registerButton = new Button("Регистрация");
        Button cancelButton = new Button("Отмена");
        cancelButton.setOnAction(e -> dialog.close());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Логин:"), 0, 0);
        grid.add(loginField, 1, 0);
        grid.add(new Label("Пароль:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(errorLabel, 1, 2);

        VBox buttonBox = new VBox(10, loginButton, registerButton, cancelButton);
        buttonBox.setPadding(new Insets(0, 15, 15, 15));

        VBox root = new VBox(10, grid, buttonBox);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 320, 250);
        dialog.setScene(scene);

        loginButton.setDefaultButton(true);
        loginButton.setOnAction(e -> {
            errorLabel.setVisible(false);
            handleLogin(loginField.getText(), passwordField.getText(), errorLabel, dialog);
        });

        registerButton.setOnAction(e -> {
            errorLabel.setVisible(false);
            handleRegister(loginField.getText(), passwordField.getText(), errorLabel, dialog);
        });

        scene.setOnKeyPressed(ke -> {
            if (ke.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                dialog.close();
            }
        });

        dialog.showAndWait();
        return loginSuccess;
    }

    private void handleLogin(String login, String password, Label errorLabel, Stage dialog) {
        if (login.isEmpty() || password.isEmpty()) {
            showError(errorLabel, "Логин и пароль не могут быть пустыми");
            return;
        }
        try {
            authService.login(login, password);
            loginSuccess = true;
            dialog.close();
        } catch (ValidationException e) {
            showError(errorLabel, e.getMessage());
        } catch (Exception e) {  // ← ← ← Ловим всё остальное
            showError(errorLabel, "Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRegister(String login, String password, Label errorLabel, Stage dialog) {
        if (login.isEmpty() || password.isEmpty()) {
            showError(errorLabel, "Логин и пароль не могут быть пустыми");
            return;
        }

        TextInputDialog confirmDialog = new TextInputDialog();
        confirmDialog.initOwner(dialog);
        confirmDialog.setTitle("Подтверждение пароля");
        confirmDialog.setHeaderText("Повторите пароль");
        confirmDialog.setContentText("Пароль:");

        String confirmPassword = confirmDialog.showAndWait().orElse(null);
        if (confirmPassword == null) return;
        if (!confirmPassword.equals(password)) {
            showError(errorLabel, "Пароли не совпадают");
            return;
        }

        try {
            authService.register(login, password);
            DialogManager.showAlert("Успех", "Пользователь " + login + " зарегистрирован!");
            authService.login(login, password);
            loginSuccess = true;
            dialog.close();
        } catch (ValidationException e) {
            showError(errorLabel, e.getMessage());
        } catch (Exception e) {  // ← ← ← Ловим всё остальное
            showError(errorLabel, "Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}