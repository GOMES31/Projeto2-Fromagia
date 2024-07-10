package com.example.fromagiabackend.Entity;

import com.example.fromagiabackend.Entity.Enums.OrderState;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_state")
    private OrderState orderState = OrderState.PENDING;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Invoice invoice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    public void calculateTotalAmount() {
        this.totalAmount = orderItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(item.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
