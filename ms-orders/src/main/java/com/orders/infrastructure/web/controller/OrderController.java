package com.orders.infrastructure.web.controller;

import com.orders.application.dto.OrderRequestDTO;
import com.orders.application.dto.OrderResponseDTO;
import com.orders.application.service.OrderService;
import com.orders.infrastructure.security.UserDetails;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "API des commandes")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Récupérer toutes les commandes", description = "Retourne la liste complète de toutes les commandes enregistrées")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produit non récupéré", content = @Content)
    })
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponseDTO>> getOrders() {
        log.info("GET /api/v1/orders - Récupération de toutes les commandes");

        return ResponseEntity.ok(orderService.getOrders());
    }

    @Operation(summary = "Récupérer une commande", description = "Retourne une commande spécifique basé sur son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande récupérée",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Commande non récupérée", content = @Content)
    })
    @RequestMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable Long id) {

        log.info("GET /api/v1/orders/{} - Récupération de la commande", id);

        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @Operation(summary = "Créer une commande", description = "Créer une nouvelle commande")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Commande créee",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "409", description = "La commande existe deja", content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseDTO> createOrder(@Parameter(description = "Commande à créer", required = true) @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        orderRequestDTO.setUserId(UserDetails.getUserId());
        log.info("POST /api/v1/orders - Création de la commande pour le user: {}", orderRequestDTO.getUserId());
        return ResponseEntity.ok(orderService.createOrder(orderRequestDTO));
    }

    @Operation(summary = "Changer le statut", description = "Changer le statut d'une commande")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut mis à jour",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Commande non найдee", content = @Content)
    })
    @PutMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable Long id,
            @Parameter(description = "Statut de la commande", required = true)
            @RequestBody Map<String, String> body) {

        String status = body.get("status");
        log.info("PUT /api/v1/orders/{}/status - Mise à jour de statut de la commande", id);
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @Operation(summary = "Supprimer une commande", description = "Supprimer une commande")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande supprimée",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Commande non найдee", content = @Content)
    })
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteOrder(@Parameter(description = "ID de la commande", required = true) @PathVariable Long id) {
        log.info("DELETE /api/v1/orders/{} - Suppression de la commande", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Récupérer toutes les commandes d'un utilisateur", description = "Retourne toutes les commandes d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commandes récupérées",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Commandes non récupérées", content = @Content)
    })
    @RequestMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(@Parameter(description = "ID de l'utilisateur", required = true) @PathVariable Long userId) {
        log.info("GET /api/v1/orders/user/{} - Récupération de toutes les commandes de l'utilisateur", userId);
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @Operation(summary = "Filtrer les commandes", description = "Filtrer les commandes par status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commandes filtrées",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Commandes non filtrées", content = @Content)
    })
    @RequestMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(@Parameter(description = "Statut de la commande", required = true) @PathVariable String status) {
        log.info("GET /api/v1/orders/status/{} - Récupération de toutes les commandes de l'utilisateur", status);
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

}
