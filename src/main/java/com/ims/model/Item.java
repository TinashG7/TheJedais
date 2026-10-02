package com.ims.model;

public class Item {

    private int itemId;
    private String name;
    private String category;
    private double price;        // selling price
    private double costPrice;    // what the shop paid — needed for profit calc
    private int quantityInStock;
    private int lowStockThreshold;

    // No-args constructor — useful when building an Item step by step (e.g. from a form)
    public Item() {
    }

    // Full constructor — useful when reading a complete row back from the database
    public Item(int itemId, String name, String category, double price,
                double costPrice, int quantityInStock, int lowStockThreshold) {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.costPrice = costPrice;
        this.quantityInStock = quantityInStock;
        this.lowStockThreshold = lowStockThreshold;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }
}
