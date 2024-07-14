package com.example.fromagiabackend.Service.Stock;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Stock;
import com.example.fromagiabackend.Entity.StockItem;
import com.example.fromagiabackend.Entity.Supplier;

import java.util.List;

public interface StockService {

    void save(Stock stock);

    List<StockItem> getStockProducts(Integer id);

    Stock getCompanyStock(Company company);

    Stock getSupplierStock(Supplier supplier);
}
