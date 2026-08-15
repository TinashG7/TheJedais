package com.ims.dao;

import com.ims.model.Sale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    public void addSale(Sale sale) {
        String sql = "INSERT INTO sales (item_id, quantity_sold, sale_date, total_amount, shopkeeper_id) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

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

        } catch (SQLException e) {
            System.err.println("Failed to add sale: " + e.getMessage());
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
        String sql = "SELECT * FROM sales";
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
