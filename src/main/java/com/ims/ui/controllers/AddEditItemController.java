package com.ims.ui.controllers;

import com.ims.model.Item;
import com.ims.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddEditItemController {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField priceField;
    @FXML private TextField costPriceField;
    @FXML private TextField stockThresholdField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Item itemToEdit;
    private boolean saved = false;

    @FXML
    public void initialize() {
        categoryBox.getItems().addAll("Electronics", "Groceries", "Clothing", "Other");
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> closeWindow());
    }

    public void setItemToEdit(Item item) {
        this.itemToEdit = item;
        if (item != null) {
            nameField.setText(item.getName());
            categoryBox.setValue(item.getCategory());
            priceField.setText(String.valueOf(item.getPrice()));
            costPriceField.setText(String.valueOf(item.getCostPrice()));
            stockThresholdField.setText(String.valueOf(item.getLowStockThreshold()));
        }
    }

    private void handleSave() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String category = categoryBox.getValue();

        if (name.isEmpty()) {
            AlertHelper.showError("Item name cannot be empty.");
            return;
        }
        if (category == null || category.isEmpty()) {
            AlertHelper.showError("Please select a category.");
            return;
        }

        double price;
        double costPrice;
        int stockThreshold;

        try {
            price = Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            AlertHelper.showError("Price must be a valid number.");
            return;
        }
        try {
            costPrice = Double.parseDouble(costPriceField.getText().trim());
        } catch (NumberFormatException e) {
            AlertHelper.showError("Cost price must be a valid number.");
            return;
        }
        try {
            stockThreshold = Integer.parseInt(stockThresholdField.getText().trim());
        } catch (NumberFormatException e) {
            AlertHelper.showError("Stock threshold must be a whole number.");
            return;
        }

        if (price < 0) {
            AlertHelper.showError("Price cannot be negative.");
            return;
        }
        if (costPrice < 0) {
            AlertHelper.showError("Cost price cannot be negative.");
            return;
        }
        if (stockThreshold < 0) {
            AlertHelper.showError("Stock threshold cannot be negative.");
            return;
        }

        if (itemToEdit == null) {
            itemToEdit = new Item();
        }
        itemToEdit.setName(name);
        itemToEdit.setCategory(category);
        itemToEdit.setPrice(price);
        itemToEdit.setCostPrice(costPrice);
        itemToEdit.setLowStockThreshold(stockThreshold);

        saved = true;
        AlertHelper.showConfirmation("Item saved successfully.");
        closeWindow();
    }

    public boolean isSaved() {
        return saved;
    }

    public Item getItem() {
        return itemToEdit;
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}