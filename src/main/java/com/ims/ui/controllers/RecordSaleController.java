package com.ims.ui.controllers;

import com.ims.model.Item;
import com.ims.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

import java.util.List;    

public class RecordSaleController {

    @FXML private ComboBox<Item> itemComboBox;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Label totalPriceLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    @FXML
    public void initialize() {
        quantitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1)
        );
        quantitySpinner.setEditable(true);

        itemComboBox.valueProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                int maxStock = Math.max(newItem.getQuantityInStock(), 1);
                quantitySpinner.setValueFactory(
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxStock, 1)
                );
            }
            updateTotal();
        });

        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTotal());

        confirmButton.setOnAction(e -> handleConfirm());
        cancelButton.setOnAction(e -> closeWindow());
    }

    public void setAvailableItems(List<Item> items) {
        itemComboBox.getItems().setAll(items);
    }

    private void updateTotal() {
        Item selected = itemComboBox.getValue();
        if (selected == null) {
            totalPriceLabel.setText("Total: $0.00");
            return;
        }
        int qty = quantitySpinner.getValue();
        double total = selected.getPrice() * qty;
        totalPriceLabel.setText(String.format("Total: $%.2f", total));
    }

    private void handleConfirm() {
        Item selected = itemComboBox.getValue();
        if (selected == null) {
            AlertHelper.showError("Please select an item to sell.");
            return;
        }

        int qty = quantitySpinner.getValue();

        if (qty <= 0) {
            AlertHelper.showError("Quantity must be at least 1.");
            return;
        }

        if (qty > selected.getQuantityInStock()) {
            AlertHelper.showError(
                    "Cannot sell " + qty + " units. Only " + selected.getQuantityInStock() + " in stock."
            );
            return;
        }

        selected.setQuantityInStock(selected.getQuantityInStock() - qty);

        if (selected.getQuantityInStock() <= selected.getLowStockThreshold()) {
            AlertHelper.showLowStockAlert();
        }

        AlertHelper.showConfirmation("Sale recorded: " + qty + " x " + selected.getName());
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}