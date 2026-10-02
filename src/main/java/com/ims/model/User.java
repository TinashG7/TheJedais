package com.ims.model;

public class User {

    private int userId;
    private String name;
    private String role;             // "shopkeeper" or "owner"
    private double commissionRate;   // e.g. 0.05 for 5%

    public User() {
    }

    public User(int userId, String name, String role, double commissionRate) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.commissionRate = commissionRate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(double commissionRate) {
        this.commissionRate = commissionRate;
    }
}
