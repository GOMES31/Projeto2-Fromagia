package com.example.fromagiabackend.Service.Stock;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Stock;
import com.example.fromagiabackend.Entity.StockItem;
import com.example.fromagiabackend.Repository.StockRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    @Transactional
    public List<StockItem> getStockProducts(Integer id) {
        Stock stock = stockRepository.findById(id).orElse(null);

        if(Objects.nonNull(stock)){
            Hibernate.initialize(stock.getStockItems());
            return new ArrayList<>(stock.getStockItems());
        }

        return Collections.emptyList();
    }
}
