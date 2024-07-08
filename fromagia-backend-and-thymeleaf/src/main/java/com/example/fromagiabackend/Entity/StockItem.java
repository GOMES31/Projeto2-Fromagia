package com.example.fromagiabackend.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "stock_items")
@Data
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "stock_id", referencedColumnName = "id")
    private Stock stock;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(name = "quantity")
    private BigDecimal quantity;
}
