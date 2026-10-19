package com.ims.ui.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ims.dao.ItemDAO;
import com.ims.dao.SaleDAO;
import com.ims.model.Item;
import com.ims.model.Sale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class SalesController {

    @FXML private ComboBox<Item> itemComboBox;
    @FXML private TextField quantityField;
    @FXML private TextField shopkeeperIdField;
    @FXML private Label unitPriceLabel;
    @FXML private Label totalAmountLabel;
    @FXML private Label statusMessageLabel;

    @FXML private TableView<Sale> salesTable;
    @FXML private TableColumn<Sale, Integer> saleIdColumn;
    @FXML private TableColumn<Sale, Integer> itemIdColumn;
    @FXML private TableColumn<Sale, Integer> quantityColumn;
    @FXML private TableColumn<Sale, Double> totalColumn;
    @FXML private TableColumn<Sale, LocalDateTime> dateColumn;
    @FXML private TableColumn<Sale, Integer> shopkeeperColumn;

    private final ItemDAO itemDAO = new ItemDAO();
    private final SaleDAO saleDAO = new SaleDAO();
    private final ObservableList<Sale> salesList = FXCollections.observableArrayList();
    
    private final ObservableList<Item> masterItemList = FXCollections.observableArrayList();
    private FilteredList<Item> filteredItemList;

    private static int currentLoggedInUserId = 1;

    // Formatter to remove 'T' and milliseconds
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        saleIdColumn.setCellValueFactory(new PropertyValueFactory<>("saleId"));
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        shopkeeperColumn.setCellValueFactory(new PropertyValueFactory<>("shopkeeperId"));

        // Format Date column to eliminate 'T' and milliseconds
        dateColumn.setCellFactory(column -> new TableCell<Sale, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DATE_FORMATTER));
                }
            }
        });

        shopkeeperIdField.setText(String.valueOf(currentLoggedInUserId));
        shopkeeperIdField.setEditable(false);

        setupSearchableComboBox();
        loadSalesHistory();

        itemComboBox.setOnAction(e -> calculateTotal());
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
    }

    public static void setCurrentLoggedInUserId(int userId) {
        currentLoggedInUserId = userId;
    }

    private void setupSearchableComboBox() {
        List<Item> items = itemDAO.getAllItems();
        masterItemList.setAll(items);
        filteredItemList = new FilteredList<>(masterItemList, p -> true);

        itemComboBox.setItems(filteredItemList);
        itemComboBox.setEditable(true);
        itemComboBox.setVisibleRowCount(10); // Show up to 10 items without needing immediately restricted scrolling

        itemComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Item item) {
                return (item != null) ? item.getName() + " (Stock: " + item.getQuantityInStock() + ")" : "";
            }

            @Override
            public Item fromString(String string) {
                return itemComboBox.getSelectionModel().getSelectedItem();
            }
        });

        itemComboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final Item selectedItem = itemComboBox.getSelectionModel().getSelectedItem();
            
            if (selectedItem != null && itemComboBox.getConverter().toString(selectedItem).equals(newValue)) {
                return;
            }

            filteredItemList.setPredicate(item -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String filterPattern = newValue.toLowerCase().trim();
                return item.getName().toLowerCase().contains(filterPattern);
            });

            if (!itemComboBox.isShowing() && !filteredItemList.isEmpty()) {
                itemComboBox.show();
            }
        });
    }

    private void calculateTotal() {
        Item selectedItem = itemComboBox.getValue();
        if (selectedItem == null) return;

        unitPriceLabel.setText("Unit Price: KSh " + selectedItem.getPrice());

        try {
            int qty = Integer.parseInt(quantityField.getText().trim());
            double total = qty * selectedItem.getPrice();
            totalAmountLabel.setText("Total: KSh " + total);
        } catch (NumberFormatException e) {
            totalAmountLabel.setText("Total: KSh 0.00");
        }
    }

    @FXML
    public void handleRecordSale() {
        showStatus("", false);
        try {
            Item selectedItem = itemComboBox.getValue();
            if (selectedItem == null) {
                showStatus("Please select a valid item from the dropdown.", true);
                return;
            }

            if (quantityField.getText().trim().isEmpty()) {
                showStatus("Please enter the quantity sold.", true);
                return;
            }

            int qty = Integer.parseInt(quantityField.getText().trim());
            int shopkeeperId = Integer.parseInt(shopkeeperIdField.getText().trim());

            if (qty <= 0) {
                showStatus("Quantity must be greater than 0.", true);
                return;
            }

            if (qty > selectedItem.getQuantityInStock()) {
                showStatus("Insufficient stock! Available: " + selectedItem.getQuantityInStock(), true);
                return;
            }

            double total = qty * selectedItem.getPrice();

            Sale sale = new Sale(0, selectedItem.getItemId(), qty, LocalDateTime.now(), total, shopkeeperId);

            saleDAO.addSale(sale);
            showStatus("Sale recorded successfully!", false);

            loadSalesHistory();
            
            List<Item> updatedItems = itemDAO.getAllItems();
            masterItemList.setAll(updatedItems);

            quantityField.clear();
            itemComboBox.getEditor().clear();
            totalAmountLabel.setText("Total: KSh 0.00");

        } catch (NumberFormatException e) {
            showStatus("Invalid quantity entered. Please enter numbers only.", true);
        }
    }

    private void showStatus(String message, boolean isError) {
        if (statusMessageLabel != null) {
            statusMessageLabel.setText(message);
            statusMessageLabel.setStyle(isError ? "-fx-text-fill: red; -fx-font-weight: bold;" : "-fx-text-fill: green; -fx-font-weight: bold;");
        }
    }

    private void loadSalesHistory() {
        salesList.setAll(saleDAO.getAllSales());
        salesTable.setItems(salesList);
    }
}