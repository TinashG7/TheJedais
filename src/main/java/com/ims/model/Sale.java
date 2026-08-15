package com.ims.model;

import java.time.LocalDateTime;

public class Sale {

    private int saleId;
    private int itemId;
    private int quantitySold;
    private LocalDateTime saleDate;
    private double totalAmount;   // quantitySold * item.price
    private int shopkeeperId;

    public Sale() {
    }

    public Sale(int saleId, int itemId, int quantitySold, LocalDateTime saleDate,
                double totalAmount, int shopkeeperId) {
        this.saleId = saleId;
        this.itemId = itemId;
        this.quantitySold = quantitySold;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
        this.shopkeeperId = shopkeeperId;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getShopkeeperId() {
        return shopkeeperId;
    }

    public void setShopkeeperId(int shopkeeperId) {
        this.shopkeeperId = shopkeeperId;
    }
}
