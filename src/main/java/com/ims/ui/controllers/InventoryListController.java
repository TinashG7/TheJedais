package com.ims.ui.controllers;

import com.ims.model.Item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class InventoryListController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilterCombo;
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
        masterItemList.add(new Item(3, "Notebook A4", "Stationery", 80.0, 50.0, 100, 20));

        // Wrap the master list in a FilteredList
        filteredData = new FilteredList<>(masterItemList, p -> true);

        // Bind filtered data to the TableView
        itemsTable.setItems(filteredData);

        // Populate category dropdown
        if (categoryFilterCombo != null) {
            categoryFilterCombo.getItems().addAll("All Categories", "Groceries", "Stationery", "Electronics");
            categoryFilterCombo.setValue("All Categories");
            categoryFilterCombo.setOnAction(e -> applyFilters());
        }

        // Listen for live text changes in search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    @FXML
    public void handleSearch() {
        applyFilters();
    }

    @FXML
    public void handleFilter() {
        applyFilters();
    }

    private void applyFilters() {
        String keyword = searchField.getText();
        String selectedCategory = (categoryFilterCombo != null) ? categoryFilterCombo.getValue() : null;

        filteredData.setPredicate(item -> {
            // 1. Check Category Match
            boolean matchesCategory = true;
            if (selectedCategory != null && !"All Categories".equals(selectedCategory)) {
                matchesCategory = item.getCategory() != null && 
                                  item.getCategory().equalsIgnoreCase(selectedCategory);
            }

            // 2. Check Search Keyword Match
            boolean matchesSearch = true;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String lowerCaseFilter = keyword.toLowerCase().trim();
                matchesSearch = (item.getName() != null && item.getName().toLowerCase().contains(lowerCaseFilter)) ||
                                (item.getCategory() != null && item.getCategory().toLowerCase().contains(lowerCaseFilter));
            }

            return matchesCategory && matchesSearch;
        });
    }
}