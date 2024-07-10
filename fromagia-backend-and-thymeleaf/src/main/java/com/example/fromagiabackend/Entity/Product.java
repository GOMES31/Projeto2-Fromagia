package com.example.fromagiabackend.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CollectionIdJdbcTypeCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "price")
    private BigDecimal price;

    @OneToOne(mappedBy = "productRequired")
    private ProductionRequirements productionRequirements;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductionHistory> productionHistory = new ArrayList<>();

}
