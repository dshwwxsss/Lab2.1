package validation;

public class AuthValidator {

    public static void validateLogin(String login) throws ValidationException {
        if (login == null || login.trim().isEmpty()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (login.length() < 3) {
            throw new ValidationException("Логин должен содержать хотя бы 3 символа");
        }
        if (login.length() > 50) {
            throw new ValidationException("Логин не может быть длиннее 50 символов");
        }
        // Только буквы, цифры, подчёркивание
        if (!login.matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException("Логин может содержать только буквы, цифры и подчёркивание");
        }
    }

    public static void validatePassword(String password) throws ValidationException {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Пароль не может быть пустым");
        }
        if (password.length() < 4) {
            throw new ValidationException("Пароль должен содержать хотя бы 4 символа");
        }
    }
}