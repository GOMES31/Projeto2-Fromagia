package com.example.fromagiabackend.Service.Stock;

import com.example.fromagiabackend.Entity.*;
import com.example.fromagiabackend.Repository.StockRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService {


    private final StockRepository stockRepository;

    @Autowired
    public StockServiceImpl(StockRepository _stockRepository){
        stockRepository = _stockRepository;
    }

    @Override
    public void save(Stock stock) {
        stockRepository.save(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockItem> getStockProducts(Integer id) {
        Stock stock = stockRepository.findById(id).orElse(null);

        if(Objects.nonNull(stock)){
            Hibernate.initialize(stock.getStockItems());
            return new ArrayList<>(stock.getStockItems());
        }

        return Collections.emptyList();
    }

    @Override
    public Stock getCompanyStock(Company company){
        return stockRepository.findStockByCompany(company);
    }

    @Override
    public Stock getSupplierStock(Supplier supplier){
        return stockRepository.findStockBySupplier(supplier);
    }

    @Override
    public Stock getClientStock(Client client) {
        return stockRepository.findStockByClient(client);
    }

    public List<StockItem> getSupplierStockItemsForSale(Supplier supplier) {
        List<StockItem> stockItems = stockRepository.findStockBySupplier(supplier).getStockItems();
        return stockItems.stream()
                .filter(StockItem::getForSale)
                .collect(Collectors.toList());
    }

}
