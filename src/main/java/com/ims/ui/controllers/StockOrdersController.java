package com.ims.ui.controllers;

import com.ims.dao.ItemDAO;
import com.ims.dao.StockOrderDAO;
import com.ims.model.Item;
import com.ims.model.StockOrder;

import java.time.LocalDateTime;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class StockOrdersController {

    @FXML private ComboBox<Item> itemComboBox;
    @FXML private TextField quantityField;
    @FXML private TextField expenseField;

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

    @FXML
    public void handleRecordOrder() {
        try {
            Item selectedItem = itemComboBox.getValue();
            if (selectedItem == null) {
                System.err.println("Select an item to restock.");
                return;
            }

            int qty = Integer.parseInt(quantityField.getText().trim());
            double expense = Double.parseDouble(expenseField.getText().trim());

            if (qty <= 0 || expense < 0) {
                System.err.println("Quantity must be greater than zero and expense cannot be negative.");
                return;
            }

            StockOrder order = new StockOrder(0, selectedItem.getItemId(), qty, LocalDateTime.now(), expense);

            stockOrderDAO.addStockOrder(order);
            loadOrdersHistory();
            loadItemsIntoDropdown(); // Refresh stock counts in dropdown

            quantityField.clear();
            expenseField.clear();

        } catch (NumberFormatException e) {
            System.err.println("Invalid numeric input for quantity or expense.");
        }
    }

    private void loadOrdersHistory() {
        ordersList.setAll(stockOrderDAO.getAllStockOrders());
        ordersTable.setItems(ordersList);
    }
}