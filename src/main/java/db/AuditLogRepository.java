package db;

import domain.AuditLog;
import java.sql.*;
import java.time.Instant;

public class AuditLogRepository {

    public void log(String userLogin, String action, String entityType, Long entityId, String details) throws SQLException {
        String sql = "INSERT INTO audit_log (user_login, action, entity_type, entity_id, details, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userLogin);
            stmt.setString(2, action);
            stmt.setString(3, entityType);
            if (entityId == null) stmt.setNull(4, Types.BIGINT);
            else stmt.setLong(4, entityId);
            stmt.setString(5, details);
            stmt.setTimestamp(6, Timestamp.from(Instant.now()));
            stmt.executeUpdate();
        }
    }
}