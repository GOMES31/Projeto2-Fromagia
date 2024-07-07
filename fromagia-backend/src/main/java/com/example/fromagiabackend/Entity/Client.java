package com.example.fromagiabackend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
public class Client {

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
    @Size(min = 9,max = 9, message = "Contacto tem que ter 9 digitos!")
    @Column(name = "contact_number")
    private String contactNumber;

    @NotEmpty(message = "Nome não pode estar vazio!")
    @Size(min = 9,max = 9, message = "NIF tem que ter 9 digitos!")
    @Column(name = "nif")
    private String nif;

    @OneToOne(mappedBy = "client")
    private User user;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();


    public Client(){

    }
    public Client(String name, String email, String contactNumber, String nif){
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.nif = nif;
    }
}
