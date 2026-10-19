package com.ims.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ims.model.Sale;

public class SaleDAO {

    public void addSale(Sale sale) {
        String insertSql = "INSERT INTO sales (item_id, quantity_sold, sale_date, total_amount, shopkeeper_id) " +
                           "VALUES (?, ?, ?, ?, ?)";
        String updateStockSql = "UPDATE items SET quantity_in_stock = quantity_in_stock - ? WHERE item_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction block

            // 1. Insert into sales table
            try (PreparedStatement stmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, sale.getItemId());
                stmt.setInt(2, sale.getQuantitySold());
                stmt.setString(3, sale.getSaleDate().toString());
                stmt.setDouble(4, sale.getTotalAmount());
                stmt.setInt(5, sale.getShopkeeperId());

                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        sale.setSaleId(generatedKeys.getInt(1));
                    }
                }
            }

            // 2. Deduct sold quantity from inventory
            try (PreparedStatement stmt = conn.prepareStatement(updateStockSql)) {
                stmt.setInt(1, sale.getQuantitySold());
                stmt.setInt(2, sale.getItemId());
                stmt.executeUpdate();
            }

            conn.commit(); // Commit both statements
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Failed to rollback sale transaction: " + ex.getMessage());
                }
            }
            System.err.println("Failed to add sale: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Failed to reset connection auto-commit: " + e.getMessage());
                }
            }
        }
    }

    public List<Sale> getSalesByDateRange(LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM sales WHERE sale_date >= ? AND sale_date <= ?";
        List<Sale> sales = new ArrayList<>();

        LocalDateTime startOfRange = start.atStartOfDay();
        LocalDateTime endOfRange = end.atTime(23, 59, 59);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, startOfRange.toString());
            stmt.setString(2, endOfRange.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sales.add(mapRowToSale(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to get sales by date range: " + e.getMessage());
        }

        return sales;
    }

    public List<Sale> getSalesByShopkeeper(int shopkeeperId) {
        String sql = "SELECT * FROM sales WHERE shopkeeper_id = ?";
        List<Sale> sales = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, shopkeeperId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sales.add(mapRowToSale(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to get sales by shopkeeper: " + e.getMessage());
        }

        return sales;
    }

    public List<Sale> getAllSales() {
        String sql = "SELECT * FROM sales ORDER BY sale_id DESC";
        List<Sale> sales = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                sales.add(mapRowToSale(rs));
            }

        } catch (SQLException e) {
            System.err.println("Failed to get all sales: " + e.getMessage());
        }

        return sales;
    }

    private Sale mapRowToSale(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setSaleId(rs.getInt("sale_id"));
        sale.setItemId(rs.getInt("item_id"));
        sale.setQuantitySold(rs.getInt("quantity_sold"));
        sale.setSaleDate(LocalDateTime.parse(rs.getString("sale_date")));
        sale.setTotalAmount(rs.getDouble("total_amount"));
        sale.setShopkeeperId(rs.getInt("shopkeeper_id"));
        return sale;
    }
}