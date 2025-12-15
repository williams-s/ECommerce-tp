package com.orders.application.dto;

import com.orders.domain.enums.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    @NotNull(message = "L'id de l'utilisateur ne peut pas être vide")
    private Long userId;

    @NotNull(message = "Le statut de la commande ne peut pas être vide")
    private OrderStatus status;

    @NotBlank(message = "L'adresse de livraison ne peut pas être vide")
    private String shippingAddress;

    @NotEmpty(message = "La liste des articles de la commande ne peut pasêtre vide")
    @Valid
    private List<OrderItemRequestDTO> orderItems;

}
