package com.products.application.dto;

import com.products.domain.enums.Category;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    @NotBlank(message = "Le nom du produit ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le nom du produit doit contenir entre 2 et 50 caractères")
    private String name;

    @NotBlank(message = "La description du produit ne peut pas être vide")
    @Size(min = 10, max = 500, message = "La description du produit doit contenir entre 10 et 500 caractères")
    private String description;

    @NotNull(message = "Le prix du produit ne peut pas être vide")
    @DecimalMin(value = "0", inclusive = false, message = "Le prix du produit doit etre supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Le prix du produit doit avoir 2 chiffres apres la virgule")
    private BigDecimal price;

    @NotNull(message = "La categorie du produit ne peut pas être vide")
    private Category category;

    @NotNull(message = "Le stock du produit ne peut pas être vide")
    @Min(value = 0, message = "Le stock du produit doit etre supérieur ou égal à 0")
    private Integer stock;

    private String imageUrl;
}
