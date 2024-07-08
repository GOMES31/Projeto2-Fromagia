package com.example.fromagiabackend.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock")
@Data
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @OneToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private List<StockItem> stockItems = new ArrayList<>();


}
