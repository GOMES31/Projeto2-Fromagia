package com.example.fromagiabackend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotEmpty(message = "Nome do produto não pode estar vazio!")
    @Column(name = "product_name")
    private String productName;

    @NotNull(message = "Preço do produto não pode estar vazio!")
    @Min(value = 0, message = "O preço tem que ser superior a 0!")
    @Column(name = "price")
    private BigDecimal price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductionHistory> productionHistory = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "production_requirements", referencedColumnName = "id")
    private ProductionRequirements productionRequirements;

}
