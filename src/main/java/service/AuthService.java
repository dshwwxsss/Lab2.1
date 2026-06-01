package service;

import domain.User;
import db.UserStorage;
import db.RoleRepository;
import validation.AuthValidator;
import validation.ValidationException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;

public class AuthService {
    private UserStorage userStorage;
    private RoleRepository roleRepository;
    private User currentUser;
    private List<String> currentUserRoles;

    public AuthService() {
        this.userStorage = new UserStorage();
        this.roleRepository = new RoleRepository();
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
            roleRepository.assignRoleToUser(login, "USER");
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
            this.currentUserRoles = roleRepository.findRolesByUser(login).stream()
                    .map(r -> r.getName())
                    .toList();
            System.out.println("OK: Добро пожаловать, " + login + "! Роли: " + currentUserRoles);
        } catch (SQLException e) {
            throw new ValidationException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public void logout() {
        this.currentUser = null;
        this.currentUserRoles = null;
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
//новые поля
    public boolean hasRole(String roleName) {
        return currentUserRoles != null && currentUserRoles.contains(roleName);
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean canModify(String objectOwner) {
        if (currentUser == null) return false;
        if (isAdmin()) return true;
        return currentUser.getLogin().equals(objectOwner);
    }
}