package com.orders.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {

    @NotNull(message = "L'id du produit ne peut pas etre vide")
    private Long productId;

    @NotNull(message = "La quantite ne peut pas etre null")
    @Min(value = 1, message = "La quantite doit etre superieure ou egale a 1")
    private Integer quantity;

}
