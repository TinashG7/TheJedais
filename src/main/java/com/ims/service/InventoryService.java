package com.ims.service;

import com.ims.dao.ItemDAO;
import com.ims.dao.StockOrderDAO;
import com.ims.model.Item;
import com.ims.model.StockOrder;
import java.time.LocalDateTime;
import java.util.List;

public class InventoryService { 
    
    // Connect to the database tables
    private ItemDAO itemDAO = new ItemDAO(); 
    private StockOrderDAO stockOrderDAO = new StockOrderDAO(); 

    /**
     * Restocks an item, increments its stock count, and logs the cost as an expense.
     */
    public void receiveStock(int itemId, int quantity, double expenseAmount) {
        // Step 1: Ask the database for the current item
        Item item = itemDAO.getItemById(itemId);
        
        if (item != null) {
            // Step 2: Calculate the new total and tell the database to update it
            int newQuantity = item.getQuantityInStock() + quantity;
            itemDAO.updateStockQuantity(itemId, newQuantity);

            // Step 3: Create a paper trail (StockOrder) for the owner's expense report
            StockOrder order = new StockOrder();
            order.setItemId(itemId);
            order.setQuantityReceived(quantity);
            order.setOrderDate(LocalDateTime.now());
            order.setExpenseAmount(expenseAmount);
            
            // Step 4: Save the expense record to the database
            stockOrderDAO.addStockOrder(order);
        }
    }

    /**
     * Fetches all items currently at or below their defined low-stock threshold.
     */
    public List<Item> getLowStockAlerts() {
        // The DAO already contains a SQL query that filters this perfectly. Just call it.
        return itemDAO.getLowStockItems(); 
    }

    /**
     * Validates whether an item is completely out of stock.
     */
    public boolean isOutOfStock(Item item) {
        // Prevent a crash if the item doesn't exist, then check if stock is 0 or less
        return item != null && item.getQuantityInStock() <= 0;
    }
}
