package com.ims.ui.controllers;

import com.ims.dao.ItemDAO;
import com.ims.dao.SaleDAO;
import com.ims.model.Item;
import com.ims.model.Sale;

import java.time.LocalDateTime;
import java.util.List;

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

public class SalesController {

    @FXML private ComboBox<Item> itemComboBox;
    @FXML private TextField quantityField;
    @FXML private TextField shopkeeperIdField;
    @FXML private Label unitPriceLabel;
    @FXML private Label totalAmountLabel;

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

    @FXML
    public void initialize() {
        saleIdColumn.setCellValueFactory(new PropertyValueFactory<>("saleId"));
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        shopkeeperColumn.setCellValueFactory(new PropertyValueFactory<>("shopkeeperId"));

        loadItemsIntoDropdown();
        loadSalesHistory();

        itemComboBox.setOnAction(e -> calculateTotal());
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
    }

    private void loadItemsIntoDropdown() {
        List<Item> items = itemDAO.getAllItems();
        itemComboBox.getItems().setAll(items);

        itemComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Item item) {
                return (item != null) ? item.getName() + " (Stock: " + item.getQuantityInStock() + ")" : "";
            }

            @Override
            public Item fromString(String string) { return null; }
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
        try {
            Item selectedItem = itemComboBox.getValue();
            if (selectedItem == null) {
                System.err.println("Select an item first.");
                return;
            }

            int qty = Integer.parseInt(quantityField.getText().trim());
            int shopkeeperId = Integer.parseInt(shopkeeperIdField.getText().trim());

            if (qty <= 0 || qty > selectedItem.getQuantityInStock()) {
                System.err.println("Invalid quantity or insufficient stock!");
                return;
            }

            double total = qty * selectedItem.getPrice();

            Sale sale = new Sale(0, selectedItem.getItemId(), qty, LocalDateTime.now(), total, shopkeeperId);

            saleDAO.addSale(sale);
            loadSalesHistory();
            loadItemsIntoDropdown();

            quantityField.clear();
            shopkeeperIdField.clear();
            totalAmountLabel.setText("Total: KSh 0.00");

        } catch (NumberFormatException e) {
            System.err.println("Invalid numbers for quantity or shopkeeper ID.");
        }
    }

    private void loadSalesHistory() {
        salesList.setAll(saleDAO.getAllSales());
        salesTable.setItems(salesList);
    }
}