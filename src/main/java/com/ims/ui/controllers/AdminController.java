package com.ims.ui.controllers;

import com.ims.dao.AuditLogDAO;
import com.ims.dao.SaleDAO;
import com.ims.dao.UserDAO;
import com.ims.model.Sale;
import com.ims.model.User;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
import javafx.stage.FileChooser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

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

    @FXML private TableView<AuditLogDAO.AuditEntry> auditTable;
    @FXML private TableColumn<AuditLogDAO.AuditEntry, Integer> logIdCol;
    @FXML private TableColumn<AuditLogDAO.AuditEntry, String> logUserCol;
    @FXML private TableColumn<AuditLogDAO.AuditEntry, String> logActionCol;
    @FXML private TableColumn<AuditLogDAO.AuditEntry, String> logTimeCol;

    private final UserDAO userDAO = new UserDAO();
    private final AuditLogDAO auditDAO = new AuditLogDAO();
    private final SaleDAO saleDAO = new SaleDAO();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        commissionCol.setCellValueFactory(new PropertyValueFactory<>("commissionRate"));

        if (logIdCol != null) logIdCol.setCellValueFactory(new PropertyValueFactory<>("logId"));
        if (logUserCol != null) logUserCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        if (logActionCol != null) logActionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        if (logTimeCol != null) logTimeCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        userRoleCombo.getItems().setAll("shopkeeper", "admin", "owner");

        loadUsers();
        loadAuditLogs();
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

    @FXML
    public void handleExportPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Store Audit & Sales Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Document (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Store_Audit_Report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");

        File file = fileChooser.showSaveDialog(usersTable.getScene().getWindow());
        if (file != null) {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.setLeading(16f);
                    contentStream.newLineAtOffset(50, 750);

                    contentStream.showText("JOSHUA'S SHOP IMS - ADMIN AUDIT REPORT");
                    contentStream.newLine();
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.showText("Generated Date: " + LocalDateTime.now().format(FORMATTER));
                    contentStream.newLine();
                    contentStream.showText("Generated By: Jedais (Admin)");
                    contentStream.newLine();
                    contentStream.newLine();

                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.showText("SYSTEM USERS:");
                    contentStream.newLine();
                    contentStream.setFont(PDType1Font.HELVETICA, 10);

                    for (User u : userDAO.getAllUsers()) {
                        contentStream.showText("ID: " + u.getUserId() + " | User: " + u.getName() + " | Role: " + u.getRole() + " | Comm: " + u.getCommissionRate());
                        contentStream.newLine();
                    }

                    contentStream.newLine();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.showText("RECENT STORE TRANSACTIONS:");
                    contentStream.newLine();
                    contentStream.setFont(PDType1Font.HELVETICA, 10);

                    List<Sale> sales = saleDAO.getAllSales();
                    for (Sale s : sales) {
                        contentStream.showText("Sale ID: " + s.getSaleId() + " | Item: " + s.getItemId() + " | Qty: " + s.getQuantitySold() + " | Revenue: KSh " + s.getTotalAmount() + " | Date: " + s.getSaleDate());
                        contentStream.newLine();
                    }

                    contentStream.endText();
                }

                document.save(file);
                showStatus("PDF report generated successfully!", false);

            } catch (IOException e) {
                showStatus("Failed to export PDF: " + e.getMessage(), true);
                e.printStackTrace();
            }
        }
    }

    private void loadUsers() {
        List<User> users = userDAO.getAllUsers();
        userList.setAll(users);
        usersTable.setItems(userList);
    }

    private void loadAuditLogs() {
        if (auditTable != null) {
            auditTable.setItems(FXCollections.observableArrayList(auditDAO.getAllLogs()));
        }
    }

    private void showStatus(String msg, boolean isError) {
        if (statusLabel != null) {
            statusLabel.setText(msg);
            statusLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        }
    }
}