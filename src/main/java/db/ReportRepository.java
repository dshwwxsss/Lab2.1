package db;

import domain.Report;
import domain.ReportStatus;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReportRepository {

    public List<Report> findAll() throws SQLException {
        List<Report> list = new ArrayList<>();
        String sql = "SELECT id, name, sample_id, experiment_id, status, owner_username, signed_by, created_at, updated_at FROM reports";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Optional<Report> findById(long id) throws SQLException {
        String sql = "SELECT id, name, sample_id, experiment_id, status, owner_username, signed_by, created_at, updated_at FROM reports WHERE id = ?";
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

    public Report save(Report report) throws SQLException {
        String sql = "INSERT INTO reports (name, sample_id, experiment_id, status, owner_username, signed_by, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, report.getName());
            stmt.setLong(2, report.getSampleId());
            stmt.setLong(3, report.getExperimentId());
            stmt.setString(4, report.getStatus().name());
            stmt.setString(5, report.getOwnerUsername());
            stmt.setString(6, report.getSignedBy());
            stmt.setTimestamp(7, Timestamp.from(report.getCreatedAt()));
            stmt.setTimestamp(8, Timestamp.from(report.getUpdatedAt()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong(1);
                return new Report(id, report.getName(), report.getSampleId(), report.getExperimentId(),
                        report.getOwnerUsername(), report.getStatus(), report.getSignedBy(),
                        report.getCreatedAt(), report.getUpdatedAt());
            }
            throw new SQLException("Failed to generate id");
        }
    }

    public void update(Report report) throws SQLException {
        String sql = "UPDATE reports SET name=?, sample_id=?, experiment_id=?, status=?, owner_username=?, signed_by=?, updated_at=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, report.getName());
            stmt.setLong(2, report.getSampleId());
            stmt.setLong(3, report.getExperimentId());
            stmt.setString(4, report.getStatus().name());
            stmt.setString(5, report.getOwnerUsername());
            stmt.setString(6, report.getSignedBy());
            stmt.setTimestamp(7, Timestamp.from(report.getUpdatedAt()));
            stmt.setLong(8, report.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteById(long id) throws SQLException {
        String sql = "DELETE FROM reports WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Report map(ResultSet rs) throws SQLException {
        return new Report(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getLong("sample_id"),
                rs.getLong("experiment_id"),
                rs.getString("owner_username"),
                ReportStatus.valueOf(rs.getString("status")),
                rs.getString("signed_by"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}