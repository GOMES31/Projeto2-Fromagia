package com.example.fromagiabackend.Entity.Helpers;

import com.example.fromagiabackend.Entity.OrderItem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderDTO {


    private Integer stockItemId;

    private Integer supplierId;

    private Integer companyId;

    @NotNull(message = "Quantidade do produto não pode estar vazia!")
    @Min(value = 1, message = "A quantidade tem que ser superior a 0!")
    private BigDecimal quantity;

}
