package com.example.fromagiabackend.Entity;

import com.example.fromagiabackend.Entity.Enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
@Entity
@Table(name = "users")
@Data
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotEmpty(message = "Nome de utilizador não pode estar vazio!")
    @Size(min = 6, message = "Tamanho do username que ter no mínimo 6 caracteres!")
    @Column(name = "username")
    private String username;

    @NotEmpty(message = "Palavra-passe não pode estar vazia!")
    @Size(min = 8, message = "Palavra-passe tem que ter no mínimo 8 caracteres!")
    @Column(name = "password")
    private String password;

    @CreationTimestamp
    @Column(name = "created_date",updatable = false,nullable = false)
    private LocalDateTime createdDate;



    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private Supplier supplier;
}
