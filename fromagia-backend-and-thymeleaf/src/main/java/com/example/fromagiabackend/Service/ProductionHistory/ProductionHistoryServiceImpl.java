package com.example.fromagiabackend.Service.ProductionHistory;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.ProductionHistory;
import com.example.fromagiabackend.Repository.ProductionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductionHistoryServiceImpl implements ProductionHistoryService{

    private final ProductionHistoryRepository productionHistoryRepository;

    @Autowired
    public ProductionHistoryServiceImpl(ProductionHistoryRepository _productionHistoryRepository){
        productionHistoryRepository = _productionHistoryRepository;
    }

    public void save(ProductionHistory productionHistory){
        productionHistoryRepository.save(productionHistory);
    }

    @Override
    public List<ProductionHistory> findCompanyProductHistory(Company company) {
        return productionHistoryRepository.findByCompany(company);
    }
}
