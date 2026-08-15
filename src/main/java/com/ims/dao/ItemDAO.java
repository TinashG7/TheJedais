package com.ims.dao;

import com.ims.model.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public Item getItemById(int itemId) {
        String sql = "SELECT * FROM items WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToItem(rs);
                } else {
                    return null; // no item found with that id
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to get item by id: " + e.getMessage());
            return null;
        }
    }

    public List<Item> getAllItems() {
        String sql = "SELECT * FROM items";
        List<Item> items = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                items.add(mapRowToItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("Failed to get all items: " + e.getMessage());
        }

        return items;
    }

    public List<Item> searchItems(String keyword) {
        String sql = "SELECT * FROM items WHERE name LIKE ? OR category LIKE ?";
        List<Item> items = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRowToItem(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to search items: " + e.getMessage());
        }

        return items;
    }

    public List<Item> getLowStockItems() {
        String sql = "SELECT * FROM items WHERE quantity_in_stock <= low_stock_threshold";
        List<Item> items = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                items.add(mapRowToItem(rs));
            }

        } catch (SQLException e) {
            System.err.println("Failed to get low stock items: " + e.getMessage());
        }

        return items;
    }

    public void addItem(Item item) {
        String sql = "INSERT INTO items (name, category, price, cost_price, quantity_in_stock, low_stock_threshold) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, item.getName());
            stmt.setString(2, item.getCategory());
            stmt.setDouble(3, item.getPrice());
            stmt.setDouble(4, item.getCostPrice());
            stmt.setInt(5, item.getQuantityInStock());
            stmt.setInt(6, item.getLowStockThreshold());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setItemId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to add item: " + e.getMessage());
        }
    }

    public void updateItem(Item item) {
        String sql = "UPDATE items SET name = ?, category = ?, price = ?, cost_price = ?, " +
                     "quantity_in_stock = ?, low_stock_threshold = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setString(2, item.getCategory());
            stmt.setDouble(3, item.getPrice());
            stmt.setDouble(4, item.getCostPrice());
            stmt.setInt(5, item.getQuantityInStock());
            stmt.setInt(6, item.getLowStockThreshold());
            stmt.setInt(7, item.getItemId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to update item: " + e.getMessage());
        }
    }

    public void updateStockQuantity(int itemId, int newQuantity) {
        String sql = "UPDATE items SET quantity_in_stock = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setInt(2, itemId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to update stock quantity: " + e.getMessage());
        }
    }

    public void deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to delete item: " + e.getMessage());
        }
    }
    // Helper method: converts one row of a ResultSet into an Item object.
    // Every method that reads Items will reuse this instead of repeating the mapping logic.
    private Item mapRowToItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setItemId(rs.getInt("item_id"));
        item.setName(rs.getString("name"));
        item.setCategory(rs.getString("category"));
        item.setPrice(rs.getDouble("price"));
        item.setCostPrice(rs.getDouble("cost_price"));
        item.setQuantityInStock(rs.getInt("quantity_in_stock"));
        item.setLowStockThreshold(rs.getInt("low_stock_threshold"));
        return item;
    }
}
