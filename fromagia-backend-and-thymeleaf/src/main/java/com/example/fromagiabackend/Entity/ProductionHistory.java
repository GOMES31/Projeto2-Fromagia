package com.example.fromagiabackend.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "production_history")
@Data
public class ProductionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "quantity_produced")
    private BigDecimal quantityProduced;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;
}
