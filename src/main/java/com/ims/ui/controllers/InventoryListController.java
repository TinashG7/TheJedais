package com.ims.ui.controllers;

import java.util.List;

import com.ims.dao.ItemDAO;
import com.ims.model.Item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class InventoryListController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilterCombo;
    @FXML private TableView<Item> itemsTable;
    
    @FXML private TableColumn<Item, Integer> itemIdColumn;
    @FXML private TableColumn<Item, String> nameColumn;
    @FXML private TableColumn<Item, String> categoryColumn;
    @FXML private TableColumn<Item, Double> priceColumn;
    @FXML private TableColumn<Item, Integer> quantityColumn;

    // Form Inputs
    @FXML private TextField nameInput;
    @FXML private ComboBox<String> categoryInputCombo;
    @FXML private TextField priceInput;
    @FXML private TextField costPriceInput;
    @FXML private TextField quantityInput;
    @FXML private TextField thresholdInput;

    private final ObservableList<Item> masterItemList = FXCollections.observableArrayList();
    private FilteredList<Item> filteredData;
    private final ItemDAO itemDAO = new ItemDAO();

    @FXML
    public void initialize() {
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));

        // Row highlighting: Red for stock == 0, Yellow for stock <= threshold
        itemsTable.setRowFactory(tv -> new TableRow<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.getQuantityInStock() == 0) {
                    setStyle("-fx-background-color: #ff9999; -fx-text-fill: black;"); // Red highlight
                } else if (item.getQuantityInStock() <= item.getLowStockThreshold()) {
                    setStyle("-fx-background-color: #fff2ac; -fx-text-fill: black;"); // Yellow highlight
                } else {
                    setStyle("");
                }
            }
        });

        loadItemsFromDatabase();

        filteredData = new FilteredList<>(masterItemList, p -> true);
        itemsTable.setItems(filteredData);

        if (categoryFilterCombo != null) {
            categoryFilterCombo.getItems().addAll("All Categories", "Groceries", "Stationery", "Electronics", "Beverages");
            categoryFilterCombo.setValue("All Categories");
            categoryFilterCombo.setOnAction(e -> applyFilters());
        }

        if (categoryInputCombo != null) {
            categoryInputCombo.getItems().addAll("Groceries", "Stationery", "Electronics", "Beverages");
        }

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    private void loadItemsFromDatabase() {
        try {
            List<Item> dbItems = itemDAO.getAllItems();
            masterItemList.setAll(dbItems);
        } catch (Exception e) {
            System.err.println("Error loading items from DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSearch() {
        applyFilters();
    }

    @FXML
    public void handleFilter() {
        applyFilters();
    }

    @FXML
    public void handleAddItem() {
        try {
            String name = nameInput.getText();
            String category = categoryInputCombo.getValue();
            
            if (name == null || name.trim().isEmpty() || category == null) {
                System.err.println("Please enter an item name and select a category.");
                return;
            }

            double price = Double.parseDouble(priceInput.getText());
            double costPrice = Double.parseDouble(costPriceInput.getText());
            int quantity = Integer.parseInt(quantityInput.getText());
            int threshold = Integer.parseInt(thresholdInput.getText());

            Item newItem = new Item(0, name, category, price, costPrice, quantity, threshold);

            itemDAO.addItem(newItem);
            loadItemsFromDatabase();

            nameInput.clear();
            categoryInputCombo.setValue(null);
            priceInput.clear();
            costPriceInput.clear();
            quantityInput.clear();
            thresholdInput.clear();

        } catch (NumberFormatException e) {
            System.err.println("Invalid numeric input for Price, Cost Price, Quantity, or Threshold.");
        }
    }

    private void applyFilters() {
        String keyword = searchField.getText();
        String selectedCategory = (categoryFilterCombo != null) ? categoryFilterCombo.getValue() : null;

        filteredData.setPredicate(item -> {
            boolean matchesCategory = true;
            if (selectedCategory != null && !"All Categories".equals(selectedCategory)) {
                matchesCategory = item.getCategory() != null && 
                                  item.getCategory().equalsIgnoreCase(selectedCategory);
            }

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