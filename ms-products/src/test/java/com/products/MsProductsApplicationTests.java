package com.products;

import com.products.application.dto.ProductRequestDTO;
import com.products.application.dto.ProductResponseDTO;
import com.products.application.mapper.ProductMapper;
import com.products.application.service.ProductService;
import com.products.domain.entity.Product;
import com.products.domain.enums.Category;
import com.products.domain.repository.ProductRepository;
import com.products.infrastructure.exception.ResourceNotFoundException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MsProductsApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(MsProductsApplicationTests.class);

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    private Counter counter;

    @Mock
    private Counter.Builder counterBuilder;

    private Product product;
    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productRequestDTO = ProductRequestDTO.builder()
                .name("Produit Test")
                .category(Category.ELECTRONICS)
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .build();

        product = Product.builder()
                .id(1L)
                .name("Produit Test")
                .category(Category.ELECTRONICS)
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .build();

        productResponseDTO = ProductResponseDTO.builder()
                .id(1L)
                .name("Produit Test")
                .category(Category.ELECTRONICS)
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .build();
    }


    @Test
    void createProduct_shouldSaveProduct() {
        log.info("Début test: createProduct_shouldSaveProduct");

        when(productMapper.toEntity(productRequestDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        // Mock du MeterRegistry pour renvoyer un Counter
        when(meterRegistry.counter(anyString(), anyString(), anyString())).thenReturn(counter);

        ProductResponseDTO response = productService.createProduct(productRequestDTO);

        assertNotNull(response);
        assertEquals("Produit Test", response.getName());
        verify(productRepository, times(1)).save(product);

        log.info("Fin test: createProduct_shouldSaveProduct");
    }




    @Test
    void getProductById_shouldReturnProduct_whenFound() {
        log.info("Début test: getProductById_shouldReturnProduct_whenFound");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO response = productService.getProductById(1L);

        assertEquals("Produit Test", response.getName());

        log.info("Fin test: getProductById_shouldReturnProduct_whenFound");
    }

    @Test
    void updateProduct_shouldUpdateProduct_whenFound() {
        log.info("Début test: updateProduct_shouldUpdateProduct_whenFound");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productMapper).updateEntityFromDto(productRequestDTO, product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO response = productService.updateProduct(1L, productRequestDTO);

        assertEquals("Produit Test", response.getName());

        log.info("Fin test: updateProduct_shouldUpdateProduct_whenFound");
    }

    @Test
    void updateProductStock_shouldUpdateStock() {
        log.info("Début test: updateProductStock_shouldUpdateStock");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO response = productService.updateProductStock(1L, 5);

        assertEquals(15, product.getStock());

        log.info("Fin test: updateProductStock_shouldUpdateStock");
    }

    @Test
    void deleteProduct_shouldDeleteProduct_whenFound() {
        log.info("Début test: deleteProduct_shouldDeleteProduct_whenFound");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).delete(product);

        log.info("Fin test: deleteProduct_shouldDeleteProduct_whenFound");
    }

    @Test
    void getProductsByCategory_shouldReturnProducts() {
        log.info("Début test: getProductsByCategory_shouldReturnProducts");

        when(productRepository.findByCategory(Category.ELECTRONICS)).thenReturn(List.of(product));
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        List<ProductResponseDTO> response = productService.getProductsByCategory("ELECTRONICS");

        assertEquals(1, response.size());

        log.info("Fin test: getProductsByCategory_shouldReturnProducts");
    }

    @Test
    void getProductById_shouldThrowException_whenNotFound() {
        log.info("Début test: getProductById_shouldThrowException_whenNotFound");

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));

        log.info("Fin test: getProductById_shouldThrowException_whenNotFound");
    }
}
