package com.products.application.service;

import com.products.application.dto.ProductRequestDTO;
import com.products.application.dto.ProductResponseDTO;
import com.products.application.mapper.ProductMapper;
import com.products.domain.entity.Product;
import com.products.domain.enums.Category;
import com.products.domain.repository.ProductRepository;
import com.products.infrastructure.exception.ResourceNotFoundException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MeterRegistry meterRegistry;

    public List<ProductResponseDTO> getAllProducts() {
        log.debug("Récupération de tous les produits");

        List<Product> products = productRepository.findAll();

        log.info("Nombre de produits récupérés: {}", products.size());

        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id) {
        log.debug("Récupération du produit avec l'id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", id));

        log.info("Produit recuperé: {}", product.getName());

        return productMapper.toDTO(product);
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        log.debug("Création du produit: {}", productRequestDTO.getName());

        Product product = productMapper.toEntity(productRequestDTO);
        Product savedProduct = productRepository.save(product);

        Counter counter = meterRegistry.counter("products.created.total", "category", product.getCategory().toString());
        counter.increment();

        log.info("Produit créé: {}", savedProduct.getName());

        return productMapper.toDTO(savedProduct);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        log.debug("Mise à jour du produit avec l'id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", id));

        productMapper.updateEntityFromDto(productRequestDTO, product);

        Product updatedProduct = productRepository.save(product);

        log.info("Produit mis à jour: {}", updatedProduct.getName());

        return productMapper.toDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Suppression du produit avec l'id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", id));

        productRepository.delete(product);

        log.info("Produit supprimé: {}", product.getName());
    }

    public List<ProductResponseDTO> getProductsByCategory(String category) {
        log.debug("Récupération des produits par catégorie: {}", category);

        Category categoryEnum = Category.valueOf(category.toUpperCase());
        List<Product> products = productRepository.findByCategory(categoryEnum);

        log.info("Nombre de produits recuperés: {}", products.size());

        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> searchProductsByName(String name) {
        log.debug("Recherche de produits par nom: {}", name);

        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);

        log.info("Nombre de produits trouvés: {}", products.size());

        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> getAvailableProducts() {
        log.debug("Récupération des produits disponibles");

        List<Product> products = productRepository.findByStockGreaterThan(0);

        log.info("Nombre de produits disponibles: {}", products.size());

        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDTO updateProductStock(Long id, int quantity) {
        log.debug("Mise à jour du stock du produit avec l'id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", id));

        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            newStock = 0;
        }
        product.setStock(newStock);

        Product updatedProduct = productRepository.save(product);

        log.info("Produit mis à jour: {} - Nouveau stock: {}", updatedProduct.getName(), updatedProduct.getStock());

        return productMapper.toDTO(updatedProduct);
    }
}
