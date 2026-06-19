package com.example.catalog.product.dto;

import com.example.catalog.product.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record ProductStatusRequest(@NotNull ProductStatus status) {
}

