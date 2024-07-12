package com.example.fromagiabackend.Repository;

import com.example.fromagiabackend.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product getProductByProductCode(String productCode);
}
