package com.ims.service;

import com.ims.dao.ItemDAO; //Required to query stock counts and triggers
import com.ims.model.Item; //Required to pass around item entities
import java.util.List; //Required because getLowStockAlerts() returns a collection of items

public class InventoryService { //Handles business logic, stock adjustments, and low-stock checks
    private ItemDAO itemDAO = new ItemDAO(); // Data access object for reading and writing item records in SQLite

    public void receiveStock {
        
    }
}
