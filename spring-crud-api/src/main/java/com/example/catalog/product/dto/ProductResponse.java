package com.example.catalog.product.dto;

import com.example.catalog.product.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        ProductStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}

