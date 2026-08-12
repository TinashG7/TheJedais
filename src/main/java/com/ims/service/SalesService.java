package com.ims.service;

import com.ims.dao.ItemDAO; //Required to verify available stock before processing a transaction
import com.ims.dao.SaleDAO; //Required to record the completed sale in the database

//Required to manipulate inventory objects and generate sale records
import com.ims.model.Item;
import com.ims.model.Sale;
