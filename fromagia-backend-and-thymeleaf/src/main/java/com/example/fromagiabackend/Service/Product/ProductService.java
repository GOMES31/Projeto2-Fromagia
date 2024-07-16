package com.example.fromagiabackend.Service.Product;

import com.example.fromagiabackend.Entity.Product;

import java.util.Optional;

public interface ProductService {


    Product getProductByProductName(String productName);

    void save(Product product);

    String generateProductCode(Integer id);

    Optional<Product> findById(Integer id);
}
