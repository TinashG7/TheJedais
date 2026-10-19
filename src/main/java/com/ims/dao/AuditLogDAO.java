package com.ims.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void logAction(int userId, String username, String action) {
        String sql = "INSERT INTO audit_logs (user_id, username, action, timestamp) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, action);
            stmt.setString(4, LocalDateTime.now().format(FORMATTER));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to log audit action: " + e.getMessage());
        }
    }

    public List<AuditEntry> getAllLogs() {
        String sql = "SELECT * FROM audit_logs ORDER BY log_id DESC";
        List<AuditEntry> logs = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                logs.add(new AuditEntry(
                    rs.getInt("log_id"),
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("action"),
                    rs.getString("timestamp")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch audit logs: " + e.getMessage());
        }
        return logs;
    }

    public static class AuditEntry {
        private final int logId;
        private final int userId;
        private final String username;
        private final String action;
        private final String timestamp;

        public AuditEntry(int logId, int userId, String username, String action, String timestamp) {
            this.logId = logId;
            this.userId = userId;
            this.username = username;
            this.action = action;
            this.timestamp = timestamp;
        }

        public int getLogId() { return logId; }
        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getAction() { return action; }
        public String getTimestamp() { return timestamp; }
    }
}