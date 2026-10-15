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
        String sql = "SELECT * FROM users WHERE name LIKE ?";

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
        String sql = "INSERT INTO users (name, role, password, commission_rate) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getRole());
            stmt.setString(3, user.getPassword());
            stmt.setDouble(4, user.getCommissionRate());

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

    public void ensureDefaultUsersExist() {
    // SQL statement updates existing user records if they already exist in the database
        String sql = "INSERT INTO users (user_id, name, role, password, commission_rate) VALUES (?, ?, ?, ?, ?) " +
                     "ON CONFLICT(user_id) DO UPDATE SET name=excluded.name, role=excluded.role, " +
                     "password=excluded.password, commission_rate=excluded.commission_rate";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 1. User ID 1 - Pamela (Shopkeeper)
            stmt.setInt(1, 1);
            stmt.setString(2, "Pamela");
            stmt.setString(3, "shopkeeper");
            stmt.setString(4, "pamela123");
            stmt.setDouble(5, 0.05);
            stmt.addBatch();

            // 2. User ID 2 - Jedais (Admin)
            stmt.setInt(1, 2);
            stmt.setString(2, "Jedais");
            stmt.setString(3, "admin");
            stmt.setString(4, "admin123");
            stmt.setDouble(5, 0.00);
            stmt.addBatch();

            // 3. User ID 3 - Joshua (Owner)
            stmt.setInt(1, 3);
            stmt.setString(2, "Joshua");
            stmt.setString(3, "owner");
            stmt.setString(4, "joshua123");
            stmt.setDouble(5, 0.10);
            stmt.addBatch();

            stmt.executeBatch();
            System.out.println("Default user accounts updated successfully.");

        } catch (SQLException e) {
            System.err.println("Failed to update default user accounts: " + e.getMessage());
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setRole(rs.getString("role"));
        
        // Handle database compatibility if password column was added later
        try {
            user.setPassword(rs.getString("password"));
        } catch (SQLException e) {
            user.setPassword("1234");
        }
        
        user.setCommissionRate(rs.getDouble("commission_rate"));
        return user;
    }
}