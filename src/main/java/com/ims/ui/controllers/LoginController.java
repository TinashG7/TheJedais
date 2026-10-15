package com.ims.ui.controllers;

import java.io.IOException;

import com.ims.dao.UserDAO;
import com.ims.model.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField userIdField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        userDAO.ensureDefaultUserExists();
    }

    @FXML
    public void handleLogin() {
        showStatus("", false);
        try {
            String userIdText = userIdField.getText().trim();
            if (userIdText.isEmpty()) {
                showStatus("Please enter your User ID.", true);
                return;
            }

            int userId = Integer.parseInt(userIdText);
            User user = userDAO.getUserById(userId);

            if (user != null) {
                // Pass authenticated Shopkeeper User ID to SalesController
                SalesController.setCurrentLoggedInUserId(user.getUserId());

                // Redirect to Main Dashboard
                loadDashboard();
            } else {
                showStatus("User ID not found in database.", true);
            }

        } catch (NumberFormatException e) {
            showStatus("User ID must be a numeric ID (e.g. 1).", true);
        }
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ims/ui/fxml/dashboard.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) userIdField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Joshua's Shop IMS - Dashboard");
            stage.centerOnScreen();
        } catch (IOException e) {
            showStatus("Failed to launch dashboard: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void showStatus(String message, boolean isError) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        }
    }
}