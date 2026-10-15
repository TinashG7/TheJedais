package com.ims.ui.controllers;

import java.io.IOException;
import java.util.List;

import com.ims.dao.AuditLogDAO;
import com.ims.dao.ItemDAO;
import com.ims.model.Item;
import com.ims.model.User;
import com.ims.util.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private BorderPane mainLayout;
    @FXML private HBox alertBanner;
    @FXML private Label alertLabel;

    @FXML private Button inventoryBtn;
    @FXML private Button salesBtn;
    @FXML private Button ordersBtn;
    @FXML private Button reportsBtn;
    @FXML private Button adminPanelBtn;

    private final ItemDAO itemDAO = new ItemDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @FXML
    public void initialize() {
        checkLowStockAlerts();

        if (SessionManager.isAdmin()) {
            if (inventoryBtn != null) { inventoryBtn.setVisible(false); inventoryBtn.setManaged(false); }
            if (salesBtn != null) { salesBtn.setVisible(false); salesBtn.setManaged(false); }
            if (ordersBtn != null) { ordersBtn.setVisible(false); ordersBtn.setManaged(false); }
            if (reportsBtn != null) { reportsBtn.setVisible(false); reportsBtn.setManaged(false); }

            showAdminView();
        } else if (SessionManager.isShopkeeper()) {
            if (adminPanelBtn != null) { adminPanelBtn.setVisible(false); adminPanelBtn.setManaged(false); }

            showReportsView();
        } else {
            showReportsView();
        }
    }

    private void checkLowStockAlerts() {
        try {
            List<Item> lowStockItems = itemDAO.getLowStockItems();
            if (lowStockItems != null && !lowStockItems.isEmpty()) {
                alertLabel.setText("Alert: " + lowStockItems.size() + " item(s) are below low stock threshold!");
                alertBanner.setVisible(true);
            } else {
                alertBanner.setVisible(false);
            }
        } catch (Exception e) {
            System.err.println("Failed to check low stock alerts: " + e.getMessage());
        }
    }

    @FXML public void showReportsView() { loadView("/com/ims/ui/fxml/reports.fxml"); checkLowStockAlerts(); }
    @FXML public void showInventoryView() { loadView("/com/ims/ui/fxml/inventory_list.fxml"); checkLowStockAlerts(); }
    @FXML public void showSalesView() { loadView("/com/ims/ui/fxml/sales.fxml"); checkLowStockAlerts(); }
    @FXML public void showOrdersView() { loadView("/com/ims/ui/fxml/stock_orders.fxml"); checkLowStockAlerts(); }
    @FXML public void showAdminView() { loadView("/com/ims/ui/fxml/admin_dashboard.fxml"); }

    @FXML 
    public void handleSwitchUser() { 
        logCurrentLogout();
        redirectToLogin(); 
    }

    @FXML
    public void handleLogout() {
        logCurrentLogout();
        SessionManager.setCurrentUser(null);
        SalesController.setCurrentLoggedInUserId(0);
        redirectToLogin();
    }

    private void logCurrentLogout() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            auditLogDAO.logAction(user.getUserId(), user.getName(), "LOGGED_OUT");
        }
    }

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ims/ui/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Joshua's Shop IMS - Login");
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Failed to load login view: " + e.getMessage());
            e.printStackTrace();
        }
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