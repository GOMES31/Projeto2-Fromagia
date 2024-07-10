package com.example.fromagiabackend.Repository;

import com.example.fromagiabackend.Entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock,Integer> {
}
