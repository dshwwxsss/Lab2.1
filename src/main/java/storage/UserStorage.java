package storage;

import domain.User;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class UserStorage {
    private static final String USER_FILE = "users.csv";
    private final Set<User> users = new HashSet<>();

    // Загрузить всех пользователей из файла
    public Set<User> loadAll() throws IOException {
        Set<User> loaded = new HashSet<>();
        Path path = Paths.get(USER_FILE);
        if (!Files.exists(path)) return loaded;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length == 2) {
                    loaded.add(new User(parts[0], parts[1]));
                }
            }
        }
        return loaded;
    }

    // Сохранить всех пользователей в файл
    public void saveAll() throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(USER_FILE)))) {
            for (User user : users) {
                writer.printf("%s,%s%n", escapeCsv(user.getLogin()), user.getPasswordHash());
            }
        }
    }

    // Найти пользователя по логину
    public Optional<User> findByLogin(String login) {
        return users.stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst();
    }

    // Добавить пользователя
    public void save(User user) {
        users.remove(user); // удаляем если есть (по equals)
        users.add(user);
    }

    // Заменить всех пользователей (при загрузке)
    public void replaceAll(Set<User> newUsers) {
        users.clear();
        users.addAll(newUsers);
    }

    // Получить всех пользователей
    public Set<User> getAll() {
        return new HashSet<>(users);
    }

    // Вспомогательные методы для CSV
    private String escapeCsv(String s) {
        if (s == null) return "";
        return s.contains(",") ? "\"" + s + "\"" : s;
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        return result.toArray(new String[0]);
    }
}