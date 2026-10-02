package com.ims.model;

import java.time.LocalDateTime;

public class StockOrder {

    private int orderId;
    private int itemId;
    private int quantityReceived;
    private LocalDateTime orderDate;
    private double expenseAmount;   // cost of this restock

    public StockOrder() {
    }

    public StockOrder(int orderId, int itemId, int quantityReceived,
                       LocalDateTime orderDate, double expenseAmount) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantityReceived = quantityReceived;
        this.orderDate = orderDate;
        this.expenseAmount = expenseAmount;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(int quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(double expenseAmount) {
        this.expenseAmount = expenseAmount;
    }
}
