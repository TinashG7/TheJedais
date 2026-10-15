package com.ims.ui.controllers;

import java.util.List;

import com.ims.dao.UserDAO;
import com.ims.model.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminController {

    @FXML private TextField userNameInput;
    @FXML private ComboBox<String> userRoleCombo;
    @FXML private PasswordField userPasswordInput;
    @FXML private TextField commissionInput;
    @FXML private Label statusLabel;

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> userIdCol;
    @FXML private TableColumn<User, String> nameCol;
    @FXML private TableColumn<User, String> roleCol;
    @FXML private TableColumn<User, String> passwordCol;
    @FXML private TableColumn<User, Double> commissionCol;

    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        commissionCol.setCellValueFactory(new PropertyValueFactory<>("commissionRate"));

        userRoleCombo.getItems().setAll("shopkeeper", "admin", "owner");

        loadUsers();
    }

    @FXML
    public void handleCreateUser() {
        try {
            String name = userNameInput.getText().trim();
            String role = userRoleCombo.getValue();
            String password = userPasswordInput.getText().trim();
            String commText = commissionInput.getText().trim();

            if (name.isEmpty() || role == null || password.isEmpty()) {
                showStatus("Please complete Name, Role, and Password.", true);
                return;
            }

            double commRate = commText.isEmpty() ? 0.05 : Double.parseDouble(commText);

            User newUser = new User(0, name, role, password, commRate);
            userDAO.addUser(newUser);

            showStatus("User " + name + " created successfully!", false);
            loadUsers();

            userNameInput.clear();
            userRoleCombo.setValue(null);
            userPasswordInput.clear();
            commissionInput.clear();

        } catch (NumberFormatException e) {
            showStatus("Commission rate must be numeric (e.g. 0.05)", true);
        }
    }

    private void loadUsers() {
        List<User> users = userDAO.getAllUsers();
        userList.setAll(users);
        usersTable.setItems(userList);
    }

    private void showStatus(String msg, boolean isError) {
        if (statusLabel != null) {
            statusLabel.setText(msg);
            statusLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        }
    }
}