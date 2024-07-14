package com.example.fromagiabackend.Entity.Helpers;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateStockDTO {

    @NotNull(message = "Quantidade do produto não pode estar vazia!")
    @Min(value = 0, message = "A quantidade tem que ser superior a 0!")
    private BigDecimal quantity;

    private Integer stockItemId;
}
