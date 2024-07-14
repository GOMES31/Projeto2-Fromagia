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
    public Product getProductByProductName(String productName) {
        return productRepository.getProductByProductName(productName);
    }

    @Override
    public void save(Product product) {

        Product savedProduct = productRepository.save(product);

        String productCode = generateProductCode(savedProduct.getId());

        savedProduct.setProductCode(productCode);

        productRepository.save(savedProduct);
    }

    @Override
    public String generateProductCode(Integer id){
        return "PROD" + id;
    }
}
