package com.orders.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderedProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
}
