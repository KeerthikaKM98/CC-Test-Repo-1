package com.example.catalog.product;

import com.example.catalog.product.dto.ProductCreateRequest;
import com.example.catalog.product.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductCreateRequest request) {
        return new Product(
                normalizeSku(request.sku()),
                request.name().trim(),
                normalizeDescription(request.description()),
                request.price(),
                request.stockQuantity(),
                request.status()
        );
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public String normalizeSku(String sku) {
        return sku.trim().toUpperCase();
    }

    public String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }
}

