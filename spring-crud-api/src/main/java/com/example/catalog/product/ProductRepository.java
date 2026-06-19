package com.example.catalog.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCase(String sku);

    List<Product> findAllByOrderByNameAsc();

    List<Product> findByStatusOrderByNameAsc(ProductStatus status);

    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByNameAsc(
            String name,
            String description
    );

    List<Product> findByStatusAndNameContainingIgnoreCaseOrStatusAndDescriptionContainingIgnoreCaseOrderByNameAsc(
            ProductStatus nameStatus,
            String name,
            ProductStatus descriptionStatus,
            String description
    );
}

