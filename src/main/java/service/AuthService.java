package service;

import domain.User;
import db.UserStorage;
import validation.AuthValidator;
import validation.ValidationException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private UserStorage userStorage;
    private User currentUser;

    public AuthService() {
        this.userStorage = new UserStorage();
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка хеширования пароля", e);
        }
    }

    public void register(String login, String password) throws ValidationException {
        AuthValidator.validateLogin(login);
        AuthValidator.validatePassword(password);
        try {
            if (userStorage.findByLogin(login).isPresent()) {
                throw new ValidationException("Ошибка: логин '" + login + "' уже занят");
            }
            String passwordHash = hashPassword(password);
            User user = new User(login, passwordHash);
            userStorage.save(user);
        } catch (SQLException e) {
            throw new ValidationException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public void login(String login, String password) throws ValidationException {
        AuthValidator.validateLogin(login);
        AuthValidator.validatePassword(password);
        try {
            Optional<User> userOpt = userStorage.findByLogin(login);
            if (userOpt.isEmpty()) {
                throw new ValidationException("Ошибка: пользователь с логином '" + login + "' не найден");
            }
            User user = userOpt.get();
            String hashedInput = hashPassword(password);
            if (!user.getPasswordHash().equals(hashedInput)) {
                throw new ValidationException("Ошибка: неверный пароль");
            }
            this.currentUser = user;
            System.out.println("OK: Добро пожаловать, " + login + "!");
        } catch (SQLException e) {
            throw new ValidationException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public void logout() {
        this.currentUser = null;
        System.out.println("OK: Вы вышли из системы");
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getLogin() : null;
    }
}