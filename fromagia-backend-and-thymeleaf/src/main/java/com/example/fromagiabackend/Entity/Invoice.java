package com.example.fromagiabackend.Entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private Supplier supplier;

    @CreationTimestamp
    @Column(name = "emission_date")
    private LocalDateTime emissionDate;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceLine> invoiceLines = new ArrayList<>();
}