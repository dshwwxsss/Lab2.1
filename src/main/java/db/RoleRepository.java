package db;

import domain.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleRepository {

    public Optional<Role> findByName(String name) throws SQLException {
        String sql = "SELECT id, name FROM roles WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Role(rs.getInt("id"), rs.getString("name")));
            }
            return Optional.empty();
        }
    }

    public List<Role> findRolesByUser(String userLogin) throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.id, r.name FROM roles r " +
                "JOIN user_roles ur ON ur.role_id = r.id " +
                "WHERE ur.user_login = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userLogin);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roles.add(new Role(rs.getInt("id"), rs.getString("name")));
            }
        }
        return roles;
    }

    public void assignRoleToUser(String userLogin, String roleName) throws SQLException {
        String sql = "INSERT INTO user_roles (user_login, role_id) " +
                "SELECT ?, id FROM roles WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userLogin);
            stmt.setString(2, roleName);
            stmt.executeUpdate();
        }
    }
}