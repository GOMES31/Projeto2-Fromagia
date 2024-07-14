package com.example.fromagiabackend.Repository;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.ProductionHistory;
import com.example.fromagiabackend.Entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier,Integer> {

    List<Supplier> findByCompany(Company company);

    List<Supplier> findByCompanyIsNull();
}
