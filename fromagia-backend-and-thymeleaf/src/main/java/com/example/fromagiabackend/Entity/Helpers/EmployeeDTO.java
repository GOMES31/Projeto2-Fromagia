package com.example.fromagiabackend.Entity.Helpers;

import com.example.fromagiabackend.Entity.Enums.CompanyPosition;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmployeeDTO {

    @NotEmpty(message = "Nome não pode estar vazio!")
    private String name;

    @NotEmpty(message = "Email não pode estar vazio!")
    @Email(message = "Email tem que ser válido!")
    private String email;

    @NotNull(message = "Salário não pode estar vazio!")
    @Min(value = 750, message = "O salário tem que ser superior a 750!")
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    private CompanyPosition companyPosition;

    @NotEmpty(message = "Nome de utilizador não pode estar vazio!")
    private String username;

    @NotEmpty(message = "Palavra-passe não pode estar vazia!")
    private String password;

}
