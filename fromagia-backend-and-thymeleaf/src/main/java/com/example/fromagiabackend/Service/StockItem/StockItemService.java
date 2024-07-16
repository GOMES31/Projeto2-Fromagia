package com.example.fromagiabackend.Service.StockItem;

import com.example.fromagiabackend.Entity.Product;
import com.example.fromagiabackend.Entity.StockItem;

import java.util.Optional;

public interface StockItemService {

    void save(StockItem stockItem);

    StockItem findById(Integer id);

    StockItem findByProduct(Product product);
}
