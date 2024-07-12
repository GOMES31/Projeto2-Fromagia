package com.example.fromagiabackend.Service.Product;

import com.example.fromagiabackend.Entity.Product;
import com.example.fromagiabackend.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository _productRepository){
        productRepository = _productRepository;
    }


    @Override
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    @Override
    public Product getProductByCode(String productCode) {
        return productRepository.getProductByProductCode(productCode);
    }

    @Override
    public void save(Product product) {
        productRepository.save(product);
    }
}
