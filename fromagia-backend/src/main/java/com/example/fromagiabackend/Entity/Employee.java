package com.example.fromagiabackend.Entity;

import com.example.fromagiabackend.Entity.Enums.CompanyPosition;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Table(name = "employees")
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "salary")
    private BigDecimal salary;

    @CreationTimestamp
    @Column(name = "admission_date")
    private LocalDateTime admissionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_position")
    private CompanyPosition companyPosition;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @OneToOne(mappedBy = "employee")
    private User user;


}
