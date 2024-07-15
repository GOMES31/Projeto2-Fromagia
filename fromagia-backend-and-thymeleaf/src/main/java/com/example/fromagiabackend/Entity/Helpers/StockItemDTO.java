package com.example.fromagiabackend.Entity.Helpers;

import com.example.fromagiabackend.Entity.Enums.ProductType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class StockItemDTO {

    @NotEmpty(message = "Nome do produto não pode estar vazio!")
    private String productName;

    @NotNull(message = "Preço do produto não pode estar vazio!")
    @Min(value = 0, message = "O preço tem que ser superior a 0!")
    private BigDecimal price;

    @NotNull(message = "Quantidade do produto não pode estar vazia!")
    @Min(value = 0, message = "A quantidade tem que ser superior a 0!")
    private BigDecimal quantity;

    private Boolean forSale;

    private ProductType productType;

}
