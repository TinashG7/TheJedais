package com.ims.ui.controllers;

import java.time.LocalDateTime;
import java.util.List;

import com.ims.dao.ItemDAO;
import com.ims.dao.StockOrderDAO;
import com.ims.model.Item;
import com.ims.model.StockOrder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class StockOrdersController {

    @FXML private ComboBox<Item> itemComboBox;
    @FXML private TextField unitCostField;
    @FXML private TextField quantityField;
    @FXML private TextField expenseField;
    @FXML private Label sellingPriceLabel;
    @FXML private Label statusMessageLabel;

    @FXML private TableView<StockOrder> ordersTable;
    @FXML private TableColumn<StockOrder, Integer> orderIdColumn;
    @FXML private TableColumn<StockOrder, Integer> itemIdColumn;
    @FXML private TableColumn<StockOrder, Integer> quantityColumn;
    @FXML private TableColumn<StockOrder, Double> expenseColumn;
    @FXML private TableColumn<StockOrder, LocalDateTime> dateColumn;

    private final ItemDAO itemDAO = new ItemDAO();
    private final StockOrderDAO stockOrderDAO = new StockOrderDAO();
    private final ObservableList<StockOrder> ordersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityReceived"));
        expenseColumn.setCellValueFactory(new PropertyValueFactory<>("expenseAmount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        loadItemsIntoDropdown();
        loadOrdersHistory();

        // Task 4 & 5: When an item is selected, populate unit cost & calculate expense
        itemComboBox.setOnAction(e -> onItemSelected());

        // Recalculate expense automatically when quantity or cost price is edited
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotalExpense());
        unitCostField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotalExpense());
    }

    private void loadItemsIntoDropdown() {
        List<Item> items = itemDAO.getAllItems();
        itemComboBox.getItems().setAll(items);

        itemComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Item item) {
                return (item != null) ? item.getName() + " (Current Stock: " + item.getQuantityInStock() + ")" : "";
            }

            @Override
            public Item fromString(String string) { return null; }
        });
    }

    private void onItemSelected() {
        Item selectedItem = itemComboBox.getValue();
        if (selectedItem != null) {
            // Task 5: Show existing cost price and allow editing
            unitCostField.setText(String.valueOf(selectedItem.getCostPrice()));
            sellingPriceLabel.setText("Current Selling Price: KSh " + selectedItem.getPrice());
            calculateTotalExpense();
        }
    }

    private void calculateTotalExpense() {
        try {
            int qty = Integer.parseInt(quantityField.getText().trim());
            double unitCost = Double.parseDouble(unitCostField.getText().trim());
            // Task 4: Auto-calculate expense
            double totalExpense = qty * unitCost;
            expenseField.setText(String.format("%.2f", totalExpense));
        } catch (NumberFormatException e) {
            expenseField.setText("");
        }
    }

    @FXML
    public void handleRecordOrder() {
        showStatus("", false);
        try {
            Item selectedItem = itemComboBox.getValue();
            if (selectedItem == null) {
                showStatus("Please select an item to restock.", true);
                return;
            }

            int qty = Integer.parseInt(quantityField.getText().trim());
            double unitCost = Double.parseDouble(unitCostField.getText().trim());
            double expense = Double.parseDouble(expenseField.getText().trim());

            if (qty <= 0) {
                showStatus("Quantity must be greater than zero.", true);
                return;
            }
            if (unitCost < 0 || expense < 0) {
                showStatus("Cost price and expense cannot be negative.", true);
                return;
            }

            // Task 5: Update the item's cost price in the database if modified
            if (selectedItem.getCostPrice() != unitCost) {
                selectedItem.setCostPrice(unitCost);
                itemDAO.updateItem(selectedItem);
            }

            StockOrder order = new StockOrder(0, selectedItem.getItemId(), qty, LocalDateTime.now(), expense);

            stockOrderDAO.addStockOrder(order);
            showStatus("Stock order recorded and inventory updated successfully!", false);

            loadOrdersHistory();
            loadItemsIntoDropdown(); // Refresh dropdown stock levels

            quantityField.clear();
            unitCostField.clear();
            expenseField.clear();
            sellingPriceLabel.setText("Current Selling Price: KSh 0.00");

        } catch (NumberFormatException e) {
            showStatus("Please enter valid numeric values for quantity and cost price.", true);
        }
    }

    private void showStatus(String message, boolean isError) {
        if (statusMessageLabel != null) {
            statusMessageLabel.setText(message);
            statusMessageLabel.setStyle(isError ? "-fx-text-fill: red; -fx-font-weight: bold;" : "-fx-text-fill: green; -fx-font-weight: bold;");
        }
    }

    private void loadOrdersHistory() {
        ordersList.setAll(stockOrderDAO.getAllStockOrders());
        ordersTable.setItems(ordersList);
    }
}