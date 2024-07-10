package com.example.fromagiabackend.Service.Stock;

import com.example.fromagiabackend.Entity.Stock;
import com.example.fromagiabackend.Entity.StockItem;

import java.util.List;

public interface StockService {

    void save(Stock stock);

    List<StockItem> getStockProducts(Integer id);
}
