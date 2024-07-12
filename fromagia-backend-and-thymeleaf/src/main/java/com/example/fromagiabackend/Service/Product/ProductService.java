package com.example.fromagiabackend.Service.Product;

import com.example.fromagiabackend.Entity.Product;

import java.util.Optional;

public interface ProductService {

    Optional<Product> getProductById(Integer id);

    Product getProductByCode(String productCode);

    void save(Product product);
}
