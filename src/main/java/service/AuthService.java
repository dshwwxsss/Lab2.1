package service;

import domain.User;
import storage.UserStorage;
import validation.AuthValidator;
import validation.ValidationException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class AuthService {
    private UserStorage userStorage;
    private User currentUser;

    public AuthService() {
        this.userStorage = new UserStorage();
        loadUsersFromFile();
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(password.getBytes());//превращаем пароль в хеш
            StringBuilder sb = new StringBuilder();//создаём строитель строк
            for (byte b : hashBytes) {//цикл по каждому байту
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка хеширования пароля", e);
        }
    }

    // Регистрация нового пользователя
    public void register(String login, String password) throws ValidationException {
        AuthValidator.validateLogin(login);
        AuthValidator.validatePassword(password);

        if (userStorage.findByLogin(login).isPresent()) {
            throw new ValidationException("Ошибка: логин '" + login + "' уже занят");
        }

        String passwordHash = hashPassword(password);
        User user = new User(login, passwordHash);
        userStorage.save(user);
        saveUsersToFile();
    }

    // Вход в систему
    public void login(String login, String password) throws ValidationException {
        AuthValidator.validateLogin(login);
        AuthValidator.validatePassword(password);

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
    }

    // Выход из системы
    public void logout() {
        this.currentUser = null;
        System.out.println("OK: Вы вышли из системы");
    }

    // Проверка, авторизован ли пользователь
    public boolean isAuthenticated() {
        return currentUser != null;
    }

    // Получить текущего пользователя
    public User getCurrentUser() {
        return currentUser;
    }

    // Получить имя текущего пользователя (или null)
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getLogin() : null;
    }

    // Загрузка пользователей из файла
    private void loadUsersFromFile() {
        try {
            var users = userStorage.loadAll();
            userStorage.replaceAll(users);
        } catch (Exception e) {
            System.out.println("Не удалось загрузить пользователей: " + e.getMessage());
        }
    }

    // Сохранение пользователей в файл
    private void saveUsersToFile() {
        try {
            userStorage.saveAll();
        } catch (Exception e) {
            System.out.println("Не удалось сохранить пользователей: " + e.getMessage());
        }
    }
}