package com.example.fromagiabackend.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "production_requirements")
@Data
public class ProductionRequirements {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;


    @Column(name = "quantity_required")
    private BigDecimal quantityRequired;

    @OneToOne(mappedBy = "product")
    private Product productRequired;

}
