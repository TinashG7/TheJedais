package com.ims.service;

import com.ims.dao.ItemDAO;
import com.ims.dao.SaleDAO;
import com.ims.dao.StockOrderDAO;
import com.ims.model.Item;
import com.ims.model.Sale;
import com.ims.model.StockOrder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {

    // Connect to the database tables
    private SaleDAO saleDAO = new SaleDAO();
    private StockOrderDAO stockOrderDAO = new StockOrderDAO();
    private ItemDAO itemDAO = new ItemDAO();

    /**
     * Calculates net profit by subtracting cost of goods and expenses from revenue.
     */
    public double calculateProfit(LocalDate start, LocalDate end) {
        // Assume DN created these standard date-range query methods in the DAOs
        List<Sale> sales = saleDAO.getSalesByDateRange(start, end);
        List<StockOrder> restocks = stockOrderDAO.getOrdersByDateRange(start, end);

        double totalRevenue = 0.0;
        double costOfGoodsSold = 0.0;
        double totalExpenses = 0.0;

        // Tally up the money coming in and the cost of the physical items leaving
        if (sales != null) {
            for (Sale sale : sales) {
                totalRevenue += sale.getTotalAmount();
                
                Item item = itemDAO.getItemById(sale.getItemId());
                if (item != null) {
                    costOfGoodsSold += (item.getCostPrice() * sale.getQuantitySold());
                }
            }
        }

        // Tally up the money spent on restocking
        if (restocks != null) {
            for (StockOrder order : restocks) {
                totalExpenses += order.getExpenseAmount();
            }
        }

        // Net Profit = Revenue - COGS - Operating Expenses
        return totalRevenue - costOfGoodsSold - totalExpenses;
    }

    /**
     * Maps each item to its total quantity sold in a given period.
     */
    public Map<Item, Integer> getSalesVolumeByItem(LocalDate start, LocalDate end) {
        Map<Item, Integer> volumeMap = new HashMap<>();
        List<Sale> sales = saleDAO.getSalesByDateRange(start, end);

        if (sales != null) {
            for (Sale sale : sales) {
                Item item = itemDAO.getItemById(sale.getItemId());
                if (item != null) {
                    // If the item is already in the map, add to its count. Otherwise, start at 0.
                    volumeMap.put(item, volumeMap.getOrDefault(item, 0) + sale.getQuantitySold());
                }
            }
        }
        return volumeMap;
    }

    /**
     * Identifies the items with the highest sales volume in a given period.
     */
    public List<Item> getFastestMovingGoods(LocalDate start, LocalDate end, int topN) {
        Map<Item, Integer> volumeMap = getSalesVolumeByItem(start, end);

        // Sort the map in descending order based on volume and slice off the top N items
        return volumeMap.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
