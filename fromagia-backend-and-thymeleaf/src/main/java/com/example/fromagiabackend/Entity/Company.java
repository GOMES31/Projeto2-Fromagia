package com.example.fromagiabackend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "companies")
@Data
public class Company {

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

    @NotEmpty(message = "Contacto não pode estar vazio!")
    @Size(min = 9, message = "Contacto tem que ter no minimo 9 digitos!")
    @Column(name = "contact_number")
    private String contactNumber;

    @NotEmpty(message = "Nome não pode estar vazio!")
    @Size(min = 9, message = "NIF tem que ter no minimo 9 digitos!")
    @Column(name = "nif")
    private String nif;

    @OneToOne(mappedBy = "company")
    private User user;


    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Stock stock;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductionHistory> productionHistory = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Supplier> suppliers = new ArrayList<>();

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();



    public Company(){

    }
    public Company(String name, String email, String contactNumber, String nif){
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.nif = nif;
        this.stock = new Stock();
        this.stock.setCompany(this);
    }

    public void addProductionHistory(ProductionHistory productionHistory) {
        this.productionHistory.add(productionHistory);
        productionHistory.setCompany(this);
    }

    public void addOrder(Order order){
        this.orders.add(order);
    }

}
