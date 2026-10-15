package com.ims.ui.controllers;

import java.io.IOException;

import com.ims.dao.AuditLogDAO;
import com.ims.dao.UserDAO;
import com.ims.model.User;
import com.ims.util.SessionManager;

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
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @FXML
    public void initialize() {
        userDAO.ensureDefaultUsersExist();
    }

    @FXML
    public void handleLogin() {
        showStatus("", false);

        String userInput = userIdField.getText().trim();
        String passInput = passwordField.getText().trim();

        if (userInput.isEmpty()) {
            showStatus("Please enter your Username or User ID.", true);
            return;
        }

        if (passInput.isEmpty()) {
            showStatus("Please enter your password.", true);
            return;
        }

        User user = null;

        try {
            int userId = Integer.parseInt(userInput);
            user = userDAO.getUserById(userId);
        } catch (NumberFormatException e) {
            user = userDAO.getUserByName(userInput);
        }

        if (user == null) {
            showStatus("Account not found for: " + userInput, true);
            return;
        }

        if (user.getPassword() != null && user.getPassword().equals(passInput)) {
            SessionManager.setCurrentUser(user);
            SalesController.setCurrentLoggedInUserId(user.getUserId());
            auditLogDAO.logAction(user.getUserId(), user.getName(), "LOGGED_IN");
            loadDashboard(user);
        } else {
            showStatus("Invalid password. Please try again.", true);
        }
    }

    private void loadDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ims/ui/fxml/dashboard.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) userIdField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Joshua's Shop IMS - Logged in as " + user.getName() + " (" + user.getRole() + ")");
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