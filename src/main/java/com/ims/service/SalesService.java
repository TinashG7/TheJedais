package com.ims.service;

import com.ims.dao.ItemDAO;
import com.ims.dao.SaleDAO;
import com.ims.model.Item;
import com.ims.model.Sale;
import java.time.LocalDateTime;

public class SalesService {

    // Connect to the database tables
    private ItemDAO itemDAO = new ItemDAO();
    private SaleDAO saleDAO = new SaleDAO();

    /**
     * Validates available stock, reduces inventory, and generates a sales record.
     * @param itemId The ID of the item being sold
     * @param quantitySold The number of units purchased
     * @param shopkeeperId The ID of the user processing the transaction
     */
    public void recordSale(int itemId, int quantitySold, int shopkeeperId) {
        // Step 1: Fetch the item to verify it exists and check its price
        Item item = itemDAO.getItemById(itemId);
        
        if (item == null) {
            System.err.println("Transaction failed: Item does not exist.");
            return;
        }

        // Step 2: Prevent selling inventory you do not possess
        if (item.getQuantityInStock() < quantitySold) {
            System.err.println("Transaction failed: Insufficient stock for item " + item.getName());
            return;
        }

        // Step 3: Calculate the financial total (Price * Quantity)
        double totalAmount = item.getPrice() * quantitySold;

        // Step 4: Deduct the sold units from the physical inventory count
        int newQuantity = item.getQuantityInStock() - quantitySold;
        itemDAO.updateStockQuantity(itemId, newQuantity);

        // Step 5: Generate the digital receipt (Sale record)
        Sale sale = new Sale();
        sale.setItemId(itemId);
        sale.setQuantitySold(quantitySold);
        sale.setTotalAmount(totalAmount);
        sale.setShopkeeperId(shopkeeperId);
        sale.setSaleDate(LocalDateTime.now());

        // Step 6: Log the transaction permanently in the database
        saleDAO.addSale(sale);
    }
}
