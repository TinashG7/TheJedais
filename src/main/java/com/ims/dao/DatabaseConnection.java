package com.ims.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:ims.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createItems = """
            CREATE TABLE IF NOT EXISTS items (
                item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                category TEXT,
                price REAL NOT NULL,
                cost_price REAL NOT NULL,
                quantity_in_stock INTEGER NOT NULL DEFAULT 0,
                low_stock_threshold INTEGER NOT NULL DEFAULT 5
            );
            """;

        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                role TEXT NOT NULL,
                commission_rate REAL DEFAULT 0
            );
            """;

        String createSales = """
            CREATE TABLE IF NOT EXISTS sales (
                sale_id INTEGER PRIMARY KEY AUTOINCREMENT,
                item_id INTEGER NOT NULL,
                quantity_sold INTEGER NOT NULL,
                sale_date TEXT NOT NULL,
                total_amount REAL NOT NULL,
                shopkeeper_id INTEGER NOT NULL,
                FOREIGN KEY (item_id) REFERENCES items(item_id),
                FOREIGN KEY (shopkeeper_id) REFERENCES users(user_id)
            );
            """;

        String createStockOrders = """
            CREATE TABLE IF NOT EXISTS stock_orders (
                order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                item_id INTEGER NOT NULL,
                quantity_received INTEGER NOT NULL,
                order_date TEXT NOT NULL,
                expense_amount REAL NOT NULL,
                FOREIGN KEY (item_id) REFERENCES items(item_id)
            );
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createItems);
            stmt.execute(createUsers);
            stmt.execute(createSales);
            stmt.execute(createStockOrders);

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }
}
