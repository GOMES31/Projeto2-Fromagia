package com.example.fromagiabackend.Entity;

import com.example.fromagiabackend.Entity.Enums.CompanyPosition;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotEmpty(message = "Nome não pode estar vazio!")
    @Column(name = "name")
    private String name;

    @NotEmpty(message = "Email não pode estar vazio!")
    @Email(message = "Email tem que ser válido!")
    @Column(name = "email")
    private String email;

    @NotNull(message = "Salário não pode estar vazio!")
    @Min(value = 750, message = "O salário tem que ser superior a 750!")
    @Column(name = "salary")
    private BigDecimal salary;

    @CreationTimestamp
    @Column(name = "admission_date")
    private LocalDateTime admissionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_position")
    private CompanyPosition companyPosition;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @OneToOne(mappedBy = "employee")
    private User user;

    public Employee(){

    }

    public Employee(String name,String email,BigDecimal salary,CompanyPosition companyPosition){
        this.name = name;
        this.email = email;
        this.salary = salary;
        this.companyPosition = companyPosition;
    }


}
