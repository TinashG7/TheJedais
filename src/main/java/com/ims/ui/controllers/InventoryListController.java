package com.ims.ui.controllers;

import com.ims.model.Item;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class InventoryListController {
    @FXML private TextField searchField;
    @FXML private TableView<Item> itemsTable;
    @FXML private TableColumn<Item, String> nameColumn;
    @FXML private TableColumn<Item, String> categoryColumn;
    @FXML private TableColumn<Item, Double> priceColumn;
    @FXML private TableColumn<Item, Integer> quantityColumn;    

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));

        // Parameters: itemId, name, category, price (selling), costPrice, quantityInStock, lowStockThreshold
        itemsTable.getItems().add(new Item(1, "Sugar 1kg", "Groceries", 150.0, 120.0, 50, 10));
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText();
        System.out.println("Searching for: " + keyword);
        // later: call InventoryService.searchItems(keyword)
    }
}
