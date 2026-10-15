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
    
    // Added
    @FXML private TableColumn<Item, Integer> itemIdColumn;
    // End of added
    @FXML private TableColumn<Item, String> nameColumn;
    @FXML private TableColumn<Item, String> categoryColumn;
    @FXML private TableColumn<Item, Double> priceColumn;
    @FXML private TableColumn<Item, Integer> quantityColumn;

    // Added
    // Form Inputs
    @FXML private TextField nameInput;
    @FXML private TextField categoryInput;
    @FXML private TextField priceInput;
    @FXML private TextField costPriceInput;
    @FXML private TextField quantityInput;
    @FXML private TextField thresholdInput;
    // End of added

    private final ObservableList<Item> masterItemList = FXCollections.observableArrayList();
    private FilteredList<Item> filteredData;

    // Counter to auto-increment itemId for newly created items
    private int nextItemId = 1;

    @FXML
    public void initialize() {
        // Added
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        // End of added
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));

        // Add initial sample items using the auto-incrementing addItem helper method
        addItem("Sugar 1kg", "Groceries", 150.0, 120.0, 50, 10);
        addItem("Flour 1kg", "Groceries", 200.0, 150.0, 60, 10);
        addItem("Notebook A4", "Stationery", 80.0, 50.0, 100, 20);

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

    // Added
    @FXML
    public void handleAddItem() {
        try {
            String name = nameInput.getText();
            String category = categoryInput.getText();
            double price = Double.parseDouble(priceInput.getText());
            double costPrice = Double.parseDouble(costPriceInput.getText());
            int quantity = Integer.parseInt(quantityInput.getText());
            int threshold = Integer.parseInt(thresholdInput.getText());

            if (name != null && !name.trim().isEmpty()) {
                addItem(name, category, price, costPrice, quantity, threshold);

                // Clear input fields after adding
                nameInput.clear();
                categoryInput.clear();
                priceInput.clear();
                costPriceInput.clear();
                quantityInput.clear();
                thresholdInput.clear();
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid numeric input for Price, Cost Price, Quantity, or Threshold.");
        }
    }
    // End of added

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

    /**
     * Helper method to add an item with an automatically assigned itemId.
     */
    public void addItem(String name, String category, double price, double costPrice, int quantity, int threshold) {
        Item newItem = new Item(nextItemId++, name, category, price, costPrice, quantity, threshold);
        masterItemList.add(newItem);
    }

    public int getNextItemId() {
        return nextItemId;
    }
}