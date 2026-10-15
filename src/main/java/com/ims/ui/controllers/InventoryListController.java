package com.ims.ui.controllers;

import com.ims.model.Item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

    private final ObservableList<Item> masterItemList = FXCollections.observableArrayList();
    private FilteredList<Item> filteredData;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));

        // Add sample items into the master list
        masterItemList.add(new Item(1, "Sugar 1kg", "Groceries", 150.0, 120.0, 50, 10));
        masterItemList.add(new Item(2, "Flour 1kg", "Groceries", 200.0, 150.0, 60, 10));

        // Wrap the master list in a FilteredList (initial predicate shows all data)
        filteredData = new FilteredList<>(masterItemList, p -> true);

        // Bind filtered data to the TableView
        itemsTable.setItems(filteredData);

        // Listen for live text changes in the search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applySearchFilter(newValue);
        });
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText();
        System.out.println("Searching for: " + keyword);
        applySearchFilter(keyword);
    }

    private void applySearchFilter(String keyword) {
        filteredData.setPredicate(item -> {
            // If search bar is empty or null, display all items
            if (keyword == null || keyword.trim().isEmpty()) {
                return true;
            }

            String lowerCaseFilter = keyword.toLowerCase().trim();

            // Match against Item name or category
            if (item.getName() != null && item.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (item.getCategory() != null && item.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }

            return false; // Hide row if it doesn't match
        });
    }
}