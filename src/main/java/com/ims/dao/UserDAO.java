package com.ims.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ims.model.User;

public class UserDAO {

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to get user by id: " + e.getMessage());
            return null;
        }
    }

    public User getUserByName(String name) {
        String sql = "SELECT * FROM users WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to get user by name: " + e.getMessage());
            return null;
        }
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Failed to get all users: " + e.getMessage());
        }

        return users;
    }

    public void addUser(User user) {
        String sql = "INSERT INTO users (name, role, commission_rate) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getRole());
            stmt.setDouble(3, user.getCommissionRate());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to add user: " + e.getMessage());
        }
    }

    public void ensureDefaultUserExists() {
        String checkSql = "SELECT COUNT(*) FROM users";
        String insertSql = "INSERT INTO users (user_id, name, role, commission_rate) VALUES (1, 'Admin Shopkeeper', 'shopkeeper', 0.05)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.executeUpdate();
                    System.out.println("Default shopkeeper account created (ID: 1).");
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to seed default user: " + e.getMessage());
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setRole(rs.getString("role"));
        user.setCommissionRate(rs.getDouble("commission_rate"));
        return user;
    }
}