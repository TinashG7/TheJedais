package com.ims.ui.controllers;

import com.ims.dao.SaleDAO;
import com.ims.dao.UserDAO;
import com.ims.model.Sale;
import com.ims.model.User;
import com.ims.util.SessionManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
import javafx.stage.FileChooser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class ReportsController {

    @FXML private Label reportTitleLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private Label todaySalesLabel;
    @FXML private Label weekSalesLabel;
    @FXML private Label monthSalesLabel;
    @FXML private Label commissionLabel;

    @FXML private Button exportPdfBtn;

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

        LocalDate now = LocalDate.now();
        startDatePicker.setValue(now.withDayOfMonth(1));
        endDatePicker.setValue(now);

        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            reportTitleLabel.setText("Performance Dashboard - " + currentUser.getName() + " (" + currentUser.getRole() + ")");
            
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

    @FXML
    public void handleExportPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Sales Report PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Document (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Sales_Report_" + LocalDate.now() + ".pdf");

        File file = fileChooser.showSaveDialog(exportPdfBtn.getScene().getWindow());
        if (file != null) {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.setLeading(16f);
                    contentStream.newLineAtOffset(50, 750);

                    contentStream.showText("JOSHUA'S SHOP IMS - PERFORMANCE REPORT");
                    contentStream.newLine();
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.showText("Generated Date: " + LocalDateTime.now().format(FORMATTER));
                    contentStream.newLine();
                    contentStream.showText("Generated By: " + (SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getName() : "System"));
                    contentStream.newLine();
                    contentStream.newLine();

                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.showText("METRICS SUMMARY:");
                    contentStream.newLine();
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.showText("Today Sales: " + todaySalesLabel.getText());
                    contentStream.newLine();
                    contentStream.showText("Week Sales: " + weekSalesLabel.getText());
                    contentStream.newLine();
                    contentStream.showText("Month Sales: " + monthSalesLabel.getText());
                    contentStream.newLine();
                    contentStream.showText("Estimated Commission: " + commissionLabel.getText());
                    contentStream.newLine();
                    contentStream.newLine();

                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.showText("TRANSACTIONS AUDIT:");
                    contentStream.newLine();
                    contentStream.setFont(PDType1Font.HELVETICA, 10);

                    for (Sale sale : reportSalesTable.getItems()) {
                        String dateStr = (sale.getSaleDate() != null) ? sale.getSaleDate().format(FORMATTER) : "N/A";
                        contentStream.showText("Sale ID: " + sale.getSaleId() + " | Item: " + sale.getItemId() + 
                                               " | Qty: " + sale.getQuantitySold() + " | Total: KSh " + sale.getTotalAmount() + 
                                               " | Date: " + dateStr);
                        contentStream.newLine();
                    }

                    contentStream.endText();
                }

                document.save(file);

            } catch (IOException e) {
                System.err.println("Failed to export PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadReportData() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        List<Sale> allSales = saleDAO.getAllSales();
        User currentUser = SessionManager.getCurrentUser();

        List<Sale> relevantSales = allSales.stream()
            .filter(sale -> {
                if (SessionManager.isShopkeeper() && currentUser != null) {
                    return sale.getShopkeeperId() == currentUser.getUserId();
                }
                return true;
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