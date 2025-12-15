package com.products.infrastructure.web.controller;

import com.products.application.dto.ProductRequestDTO;
import com.products.application.dto.ProductResponseDTO;
import com.products.application.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "API de gestion des produits")

public class ProductsController {

    private final ProductService productService;

    @Operation(summary = "Récupérer tous les produits",
            description = "Retourne la liste complète de tous les produits enregistrés")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductResponseDTO.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponseDTO>> getProducts() {
        log.info("GET /api/v1/products - Récupération de tous les produits");

        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "Récupérer un produit",
            description = "Retourne un produit spécifique basé sur son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit récupéré",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produit non récupéré",
                    content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "ID du produit", required = true)
            @PathVariable Long id) {

        log.info("GET /api/v1/products/{} - Récupération du produit", id);

        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Créer un produit",
            description = "Crée un produit avec les données fournies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produit créé avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Le produit existe déjà",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Parameter(description = "Produit à créer", required = true)
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {

        log.info("POST /api/v1/products - Création d'un produit: {}", productRequestDTO.getName());


        ProductResponseDTO createdProduct = productService.createProduct(productRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProduct.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(createdProduct);
    }

    @Operation(summary = "Mettre à jour un produit",
            description = "Met à jour les données d'un produit existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit mis à jour",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflit avec un produit existant",
                    content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "ID du produit", required = true)
            @PathVariable Long id,
            @Parameter(description = "Produit à mettre à jour", required = true)
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {

        log.info("PUT /api/v1/products/{} - Mise à jour du produit", id);

        return ResponseEntity.ok(productService.updateProduct(id, productRequestDTO));
    }


    @Operation(summary = "Supprimer un produit",
            description = "Supprime un produit existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produit supprimé",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé",
                    content = @Content)
    })
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID du produit", required = true)
            @PathVariable Long id) {

        log.info("DELETE /api/v1/products/{} - Suppression du produit", id);

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Rechercher des produits",
            description = "Recherche des produits nom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produits trouvés",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produits non trouvés",
                    content = @Content)
    })
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @Parameter(description = "Nom du produit", required = true)
            @RequestParam String name) {

        log.info("GET /api/v1/products/search?name={} - Recherche de produits", name);

        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    @Operation(summary = "Rechercher des produits",
            description = "Recherche des produits par categorie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit trouvé",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produit non touvé",
                    content = @Content)
    })
    @GetMapping(value = "/category/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(
            @Parameter(description = "Categorie du produit", required = true)
            @PathVariable String category) {

        log.info("GET /api/v1/products/category/{} - Récupération de produits par categorie", category);

        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @Operation(summary = "Rechercher des produits",
            description = "Recherche des produits disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produits trouvé",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produits non trouvés",
                    content = @Content)
    })
    @GetMapping(value = "/available", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponseDTO>> getAvailableProducts() {
        log.info("GET /api/v1/products/available - Récupération de produits disponibles");
        return ResponseEntity.ok(productService.getAvailableProducts());
    }

    @Operation(summary = "Mettre à jour le stock d'un produit",
            description = "Mise à jour du stock d'un produit existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock mis à jour",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflit avec un produit existant",
                    content = @Content)
    })
    @PatchMapping(value = "/{id}/stock", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponseDTO> updateProductStock(
            @Parameter(description = "ID du produit", required = true)
            @PathVariable Long id,
            @Parameter(description = "Quantité de produit", required = true)
            @RequestBody HashMap<String, Integer> quantityBody) {
        log.info("PATCH /api/v1/products/{}/stock - Mise à jour de stock du produit", id);
        return ResponseEntity.ok(productService.updateProductStock(id, quantityBody.get("quantity")));
    }
}
