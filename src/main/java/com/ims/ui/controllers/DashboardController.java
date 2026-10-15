package com.ims.ui.controllers;

import java.io.IOException;
import java.util.List;

import com.ims.dao.ItemDAO;
import com.ims.model.Item;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class DashboardController {

    @FXML private BorderPane mainLayout;
    @FXML private HBox alertBanner;
    @FXML private Label alertLabel;

    private final ItemDAO itemDAO = new ItemDAO();

    @FXML
    public void initialize() {
        checkLowStockAlerts();
        showInventoryView();
    }

    private void checkLowStockAlerts() {
        try {
            List<Item> lowStockItems = itemDAO.getLowStockItems();
            if (lowStockItems != null && !lowStockItems.isEmpty()) {
                alertLabel.setText("Alert: " + lowStockItems.size() + " item(s) are below the low stock threshold!");
                alertBanner.setVisible(true);
            } else {
                alertBanner.setVisible(false);
            }
        } catch (Exception e) {
            System.err.println("Failed to check low stock alerts: " + e.getMessage());
        }
    }

    @FXML
    public void showInventoryView() {
        loadView("/com/ims/ui/fxml/inventory_list.fxml");
        checkLowStockAlerts();
    }

    @FXML
    public void showSalesView() {
        loadView("/com/ims/ui/fxml/sales.fxml");
        checkLowStockAlerts();
    }

    @FXML
    public void showOrdersView() {
        loadView("/com/ims/ui/fxml/stock_orders.fxml");
        checkLowStockAlerts();
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            mainLayout.setCenter(view);
        } catch (IOException e) {
            System.err.println("Failed to load view " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}