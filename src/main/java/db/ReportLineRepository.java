package db;

import domain.MeasurementParam;
import domain.ReportLine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReportLineRepository {

    public List<ReportLine> findAll() throws SQLException {
        List<ReportLine> list = new ArrayList<>();
        String sql = "SELECT id, report_id, param, value, unit, owner_username, created_at, updated_at FROM report_lines";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<ReportLine> findByReportId(long reportId) throws SQLException {
        List<ReportLine> list = new ArrayList<>();
        String sql = "SELECT id, report_id, param, value, unit, owner_username, created_at, updated_at FROM report_lines WHERE report_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, reportId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Optional<ReportLine> findById(long id) throws SQLException {
        String sql = "SELECT id, report_id, param, value, unit, owner_username, created_at, updated_at FROM report_lines WHERE id = ?"; //плейсхолдер
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(map(rs));
            }
            return Optional.empty();
        }
    }

    public ReportLine save(ReportLine line) throws SQLException {
        String sql = "INSERT INTO report_lines (report_id, param, value, unit, owner_username, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, line.getReportId());
            stmt.setString(2, line.getParam().name());
            stmt.setDouble(3, line.getValue());
            stmt.setString(4, line.getUnit());
            stmt.setString(5, line.getOwnerUsername());
            stmt.setTimestamp(6, Timestamp.from(line.getCreatedAt()));
            stmt.setTimestamp(7, Timestamp.from(line.getUpdatedAt()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong(1);
                return new ReportLine(id, line.getReportId(), line.getParam(), line.getValue(), line.getUnit(),
                        line.getOwnerUsername(), line.getCreatedAt(), line.getUpdatedAt());
            }
            throw new SQLException("Failed to generate id");
        }
    }

    public void update(ReportLine line) throws SQLException {
        String sql = "UPDATE report_lines SET report_id=?, param=?, value=?, unit=?, owner_username=?, updated_at=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, line.getReportId());
            stmt.setString(2, line.getParam().name());
            stmt.setDouble(3, line.getValue());
            stmt.setString(4, line.getUnit());
            stmt.setString(5, line.getOwnerUsername());
            stmt.setTimestamp(6, Timestamp.from(line.getUpdatedAt()));
            stmt.setLong(7, line.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteById(long id) throws SQLException {
        String sql = "DELETE FROM report_lines WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private ReportLine map(ResultSet rs) throws SQLException {
        return new ReportLine(
                rs.getLong("id"),
                rs.getLong("report_id"),
                MeasurementParam.valueOf(rs.getString("param")),
                rs.getDouble("value"),
                rs.getString("unit"),
                rs.getString("owner_username"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}
