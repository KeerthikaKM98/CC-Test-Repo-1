package com.example.catalog.product.dto;

import com.example.catalog.product.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "must contain only letters, numbers, and hyphens")
        @Size(max = 40)
        String sku,

        @NotBlank
        @Size(max = 120)
        String name,

        @Size(max = 1000)
        String description,

        @NotNull
        @DecimalMin(value = "0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @Min(0)
        int stockQuantity,

        @NotNull
        ProductStatus status
) {
}

