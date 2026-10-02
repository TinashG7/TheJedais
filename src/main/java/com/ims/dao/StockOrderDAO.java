package com.ims.dao;

import com.ims.model.StockOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StockOrderDAO {

    public void addStockOrder(StockOrder order) {
        String sql = "INSERT INTO stock_orders (item_id, quantity_received, order_date, expense_amount) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

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

        } catch (SQLException e) {
            System.err.println("Failed to add stock order: " + e.getMessage());
        }
    }

    public List<StockOrder> getOrdersByDateRange(LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM stock_orders WHERE order_date >= ? AND order_date <= ?";
        List<StockOrder> orders = new ArrayList<>();

        LocalDateTime startOfRange = start.atStartOfDay();
        LocalDateTime endOfRange = end.atTime(23, 59, 59);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, startOfRange.toString());
            stmt.setString(2, endOfRange.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRowToStockOrder(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to get orders by date range: " + e.getMessage());
        }

        return orders;
    }

    public List<StockOrder> getAllOrders() {
        String sql = "SELECT * FROM stock_orders";
        List<StockOrder> orders = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(mapRowToStockOrder(rs));
            }

        } catch (SQLException e) {
            System.err.println("Failed to get all orders: " + e.getMessage());
        }

        return orders;
    }

    private StockOrder mapRowToStockOrder(ResultSet rs) throws SQLException {
        StockOrder order = new StockOrder();
        order.setOrderId(rs.getInt("order_id"));
        order.setItemId(rs.getInt("item_id"));
        order.setQuantityReceived(rs.getInt("quantity_received"));
        order.setOrderDate(LocalDateTime.parse(rs.getString("order_date")));
        order.setExpenseAmount(rs.getDouble("expense_amount"));
        return order;
    }
}
