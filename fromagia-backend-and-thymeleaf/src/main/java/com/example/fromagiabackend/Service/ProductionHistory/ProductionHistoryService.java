package com.example.fromagiabackend.Service.ProductionHistory;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.ProductionHistory;

import java.util.List;

public interface ProductionHistoryService {

    void save(ProductionHistory productionHistory);

    List<ProductionHistory> findCompanyProductHistory(Company company);
}
