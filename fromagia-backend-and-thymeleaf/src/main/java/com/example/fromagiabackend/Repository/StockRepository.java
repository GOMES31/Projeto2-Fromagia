package com.example.fromagiabackend.Repository;

import com.example.fromagiabackend.Entity.Client;
import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Stock;
import com.example.fromagiabackend.Entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock,Integer> {
    Stock findStockByCompany(Company company);

    Stock findStockBySupplier(Supplier supplier);

    Stock findStockByClient(Client client);
}
