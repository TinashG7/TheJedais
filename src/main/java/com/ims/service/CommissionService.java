package com.ims.service;

import com.ims.dao.SaleDAO;
import com.ims.dao.UserDAO;
import com.ims.model.Sale;
import com.ims.model.User;
import java.time.LocalDate;
import java.util.List;

public class CommissionService {

    // Connect to the database tables
    private SaleDAO saleDAO = new SaleDAO();
    private UserDAO userDAO = new UserDAO();

    /**
     * Calculates the total commission owed to a shopkeeper over a specific period.
     * @param shopkeeperId The ID of the shopkeeper
     * @param start The start date of the period
     * @param end The end date of the period
     * @return The total commission amount
     */
    public double calculateCommission(int shopkeeperId, LocalDate start, LocalDate end) {
        // Step 1: Fetch the shopkeeper to get their specific commission rate
        User shopkeeper = userDAO.getUserById(shopkeeperId);
        
        // Safety check: If the user doesn't exist, nobody gets paid.
        if (shopkeeper == null) {
            return 0.0;
        }
        
        double commissionRate = shopkeeper.getCommissionRate(); // e.g., 0.05 for 5%

        // Step 2: Fetch all sales made by this specific shopkeeper within the date range
        List<Sale> sales = saleDAO.getSalesByShopkeeperAndDateRange(shopkeeperId, start, end);
        
        // Step 3: Sum up the total revenue from these sales
        double totalSalesRevenue = 0.0;
        if (sales != null) {
            for (Sale sale : sales) {
                totalSalesRevenue += sale.getTotalAmount();
            }
        }

        // Step 4: Calculate the final cut
        return totalSalesRevenue * commissionRate;
    }
}
