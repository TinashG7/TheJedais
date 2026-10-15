package com.ims.ui.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.ims.dao.SaleDAO;
import com.ims.dao.UserDAO;
import com.ims.model.Sale;
import com.ims.model.User;
import com.ims.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class ReportsController {

    @FXML private Label reportTitleLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private Label todaySalesLabel;
    @FXML private Label weekSalesLabel;
    @FXML private Label monthSalesLabel;
    @FXML private Label commissionLabel;

    @FXML private VBox adminUserManagementBox;
    @FXML private TextField newUserNameInput;
    @FXML private ComboBox<String> newUserRoleCombo;
    @FXML private PasswordField newUserPasswordInput;
    @FXML private TextField newUserCommissionInput;
    @FXML private Label userCreationStatusLabel;

    @FXML private TableView<Sale> reportSalesTable;
    @FXML private TableColumn<Sale, Integer> saleIdCol;
    @FXML private TableColumn<Sale, Integer> itemIdCol;
    @FXML private TableColumn<Sale, Integer> qtyCol;
    @FXML private TableColumn<Sale, Double> totalCol;
    @FXML private TableColumn<Sale, LocalDateTime> dateCol;
    @FXML private TableColumn<Sale, Integer> shopkeeperCol;

    private final SaleDAO saleDAO = new SaleDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<Sale> filteredSalesList = FXCollections.observableArrayList();

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        saleIdCol.setCellValueFactory(new PropertyValueFactory<>("saleId"));
        itemIdCol.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        shopkeeperCol.setCellValueFactory(new PropertyValueFactory<>("shopkeeperId"));

        dateCol.setCellFactory(column -> new TableCell<Sale, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.format(FORMATTER));
            }
        });

        // Set default date range to current month
        LocalDate now = LocalDate.now();
        startDatePicker.setValue(now.withDayOfMonth(1));
        endDatePicker.setValue(now);

        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            reportTitleLabel.setText("Performance Dashboard - " + currentUser.getName() + " (" + currentUser.getRole() + ")");
            
            // Show user creation controls for Admin and Owner
            if (SessionManager.isAdmin() || SessionManager.isOwner()) {
                adminUserManagementBox.setVisible(true);
                adminUserManagementBox.setManaged(true);
                newUserRoleCombo.getItems().setAll("shopkeeper", "admin", "owner");
            }
        }

        loadReportData();
    }

    @FXML
    public void handleFilterReports() {
        loadReportData();
    }

    private void loadReportData() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        List<Sale> allSales = saleDAO.getAllSales();
        User currentUser = SessionManager.getCurrentUser();

        // Filter sales by logged-in shopkeeper if role is shopkeeper
        List<Sale> relevantSales = allSales.stream()
            .filter(sale -> {
                if (SessionManager.isShopkeeper()) {
                    return sale.getShopkeeperId() == currentUser.getUserId();
                }
                return true; // Admin/Owner views all store sales
            })
            .filter(sale -> {
                if (sale.getSaleDate() == null) return false;
                LocalDate saleDate = sale.getSaleDate().toLocalDate();
                return (saleDate.isEqual(start) || saleDate.isAfter(start)) &&
                       (saleDate.isEqual(end) || saleDate.isBefore(end.plusDays(1)));
            })
            .collect(Collectors.toList());

        filteredSalesList.setAll(relevantSales);
        reportSalesTable.setItems(filteredSalesList);

        calculateMetrics(relevantSales);
    }

    private void calculateMetrics(List<Sale> sales) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(7);
        LocalDate monthStart = today.withDayOfMonth(1);

        double todayTotal = 0;
        double weekTotal = 0;
        double monthTotal = 0;

        for (Sale sale : sales) {
            if (sale.getSaleDate() == null) continue;
            LocalDate saleDate = sale.getSaleDate().toLocalDate();

            if (saleDate.isEqual(today)) {
                todayTotal += sale.getTotalAmount();
            }
            if (!saleDate.isBefore(weekStart)) {
                weekTotal += sale.getTotalAmount();
            }
            if (!saleDate.isBefore(monthStart)) {
                monthTotal += sale.getTotalAmount();
            }
        }

        todaySalesLabel.setText(String.format("KSh %.2f", todayTotal));
        weekSalesLabel.setText(String.format("KSh %.2f", weekTotal));
        monthSalesLabel.setText(String.format("KSh %.2f", monthTotal));

        User currentUser = SessionManager.getCurrentUser();
        double rate = (currentUser != null) ? currentUser.getCommissionRate() : 0.05;
        commissionLabel.setText(String.format("KSh %.2f", monthTotal * rate));
    }

    @FXML
    public void handleCreateUser() {
        try {
            String name = newUserNameInput.getText().trim();
            String role = newUserRoleCombo.getValue();
            String password = newUserPasswordInput.getText().trim();
            double commRate = Double.parseDouble(newUserCommissionInput.getText().trim());

            if (name.isEmpty() || role == null || password.isEmpty()) {
                userCreationStatusLabel.setText("Please complete all user fields.");
                userCreationStatusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            User newUser = new User(0, name, role, password, commRate);
            userDAO.addUser(newUser);

            userCreationStatusLabel.setText("User account created successfully! (ID: " + newUser.getUserId() + ")");
            userCreationStatusLabel.setStyle("-fx-text-fill: green;");

            newUserNameInput.clear();
            newUserRoleCombo.setValue(null);
            newUserPasswordInput.clear();
            newUserCommissionInput.clear();

        } catch (NumberFormatException e) {
            userCreationStatusLabel.setText("Invalid commission rate. Enter numbers e.g. 0.05");
            userCreationStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}