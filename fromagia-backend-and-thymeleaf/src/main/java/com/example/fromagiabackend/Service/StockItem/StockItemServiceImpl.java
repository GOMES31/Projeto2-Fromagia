package com.example.fromagiabackend.Service.StockItem;

import com.example.fromagiabackend.Entity.StockItem;
import com.example.fromagiabackend.Repository.StockItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StockItemServiceImpl implements StockItemService{

    private final StockItemRepository stockItemRepository;

    @Autowired
    public StockItemServiceImpl(StockItemRepository _stockItemRepository){
        stockItemRepository = _stockItemRepository;
    }

    @Override
    public void save(StockItem stockItem) {
        stockItemRepository.save(stockItem);
    }

    @Override
    @Transactional(readOnly = true)
    public StockItem findById(Integer id) {
        return stockItemRepository.findById(id).orElse(null);
    }
}
