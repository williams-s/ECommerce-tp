package com.products.domain.repository;

import com.products.domain.entity.Product;
import com.products.domain.enums.Category;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategory(Category category);

    List<Product> findByStockGreaterThan(Integer stock);

    Long countByStockLessThan(Integer stock);
}
