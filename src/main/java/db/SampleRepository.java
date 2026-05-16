package db;

import domain.Sample;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SampleRepository {

    public List<Sample> findAll() throws SQLException {
        List<Sample> list = new ArrayList<>();
        String sql = "SELECT id, name, owner_username FROM samples";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Sample(rs.getLong("id"), rs.getString("name"), rs.getString("owner_username")));
            }
        }
        return list;
    }

    public Optional<Sample> findById(long id) throws SQLException {
        String sql = "SELECT id, name, owner_username FROM samples WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Sample(rs.getLong("id"), rs.getString("name"), rs.getString("owner_username")));
            }
            return Optional.empty();
        }
    }

    public Sample save(Sample sample) throws SQLException {
        String sql = "INSERT INTO samples (name, owner_username) VALUES (?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sample.getName());
            stmt.setString(2, sample.getOwnerUsername());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long generatedId = rs.getLong(1);
                return new Sample(generatedId, sample.getName(), sample.getOwnerUsername());
            }
            throw new SQLException("Failed to generate id");
        }
    }

    public void update(Sample sample) throws SQLException {
        String sql = "UPDATE samples SET name = ?, owner_username = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sample.getName());
            stmt.setString(2, sample.getOwnerUsername());
            stmt.setLong(3, sample.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteById(long id) throws SQLException {
        String sql = "DELETE FROM samples WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
}