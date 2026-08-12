package com.ims.service;

//Required to calculate gross revenue, subtract restock costs, and query item metadata
import com.ims.dao.SaleDAO;
import com.ims.dao.StockOrderDAO;
import com.ims.dao.ItemDAO;

import com.ims.model.Item; //Required for item-level reportin outputs
import java.time.LocalDate; //Required to run time-bounded reports (weekly/monthly)

//Required to return ranked data like fastest-moving items or item-to-volume maps
import java.util.List;
import java.util.Map;
