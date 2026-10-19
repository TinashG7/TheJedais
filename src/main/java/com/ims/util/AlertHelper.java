package com.ims.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public final class AlertHelper {

    private AlertHelper() {
        // utility class, no instances
    }

    public static void showLowStockAlert() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Low Stock Warning");
        alert.setHeaderText(null);
        alert.setContentText("This item's stock has fallen to or below its threshold.");
        alert.showAndWait();
    }

    public static void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showConfirmation(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}