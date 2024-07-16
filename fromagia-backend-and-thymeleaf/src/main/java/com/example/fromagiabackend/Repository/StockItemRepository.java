package com.example.fromagiabackend.Repository;

import com.example.fromagiabackend.Entity.Product;
import com.example.fromagiabackend.Entity.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockItemRepository extends JpaRepository<StockItem,Integer> {

    StockItem findStockItemByProduct(Product product);
}
