package com.example.fromagiabackend.Service.Stock;

import com.example.fromagiabackend.Entity.*;

import java.util.List;

public interface StockService {

    void save(Stock stock);

    List<StockItem> getStockProducts(Integer id);

    Stock getCompanyStock(Company company);

    Stock getSupplierStock(Supplier supplier);

    Stock getClientStock(Client client);
}
