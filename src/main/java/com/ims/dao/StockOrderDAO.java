package com.ims.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ims.model.StockOrder;

public class StockOrderDAO {

    public void addStockOrder(StockOrder order) {
        String insertSql = "INSERT INTO stock_orders (item_id, quantity_received, order_date, expense_amount) VALUES (?, ?, ?, ?)";
        String updateStockSql = "UPDATE items SET quantity_in_stock = quantity_in_stock + ? WHERE item_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Insert order record
            try (PreparedStatement stmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, order.getItemId());
                stmt.setInt(2, order.getQuantityReceived());
                stmt.setString(3, order.getOrderDate().toString());
                stmt.setDouble(4, order.getExpenseAmount());
                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setOrderId(generatedKeys.getInt(1));
                    }
                }
            }

            // 2. Increase stock quantity in inventory
            try (PreparedStatement stmt = conn.prepareStatement(updateStockSql)) {
                stmt.setInt(1, order.getQuantityReceived());
                stmt.setInt(2, order.getItemId());
                stmt.executeUpdate();
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.err.println("Failed to add stock order: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Failed to reset auto-commit: " + e.getMessage());
                }
            }
        }
    }

    public List<StockOrder> getAllStockOrders() {
        String sql = "SELECT * FROM stock_orders ORDER BY order_id DESC";
        List<StockOrder> orders = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                StockOrder order = new StockOrder(
                    rs.getInt("order_id"),
                    rs.getInt("item_id"),
                    rs.getInt("quantity_received"),
                    LocalDateTime.parse(rs.getString("order_date")),
                    rs.getDouble("expense_amount")
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Failed to get stock orders: " + e.getMessage());
        }

        return orders;
    }
}