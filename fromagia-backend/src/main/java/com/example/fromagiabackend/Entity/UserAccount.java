package com.example.fromagiabackend.Entity;

import com.example.fromagiabackend.Entity.Enums.AccountType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private Supplier supplier;

    @PrePersist
    @PreUpdate
    private void checkAccountType() {
        Map<AccountType, Set<String>> associations = new EnumMap<>(AccountType.class);
        associations.put(AccountType.COMPANY, Set.of("client", "employee", "supplier"));
        associations.put(AccountType.CLIENT, Set.of("company", "employee", "supplier"));
        associations.put(AccountType.EMPLOYEE, Set.of("company", "client", "supplier"));
        associations.put(AccountType.SUPPLIER, Set.of("company", "client", "employee"));

        for (String association : associations.get(accountType)) {
            try {
                Object value = this.getClass().getDeclaredField(association).get(this);
                if (value != null) {
                    throw new IllegalStateException(String.format("User with account type %s cannot be associated with %s", accountType, association));
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
