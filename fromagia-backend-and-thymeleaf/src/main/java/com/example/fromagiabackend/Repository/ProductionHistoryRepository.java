package com.example.fromagiabackend.Repository;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.ProductionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionHistoryRepository extends JpaRepository<ProductionHistory,Integer> {

    List<ProductionHistory> findByCompany(Company company);
}
