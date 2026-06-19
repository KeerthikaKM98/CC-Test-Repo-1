package com.example.catalog.config;

import com.example.catalog.product.ProductRepository;
import com.example.catalog.product.ProductStatus;
import com.example.catalog.product.dto.ProductCreateRequest;
import com.example.catalog.product.ProductMapper;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class SampleDataConfiguration {

    @Bean
    @ConditionalOnProperty(name = "app.seed-data", havingValue = "true", matchIfMissing = true)
    ApplicationRunner seedProducts(ProductRepository repository, ProductMapper mapper) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            List<ProductCreateRequest> samples = List.of(
                    new ProductCreateRequest(
                            "KB-100",
                            "Mechanical Keyboard",
                            "Compact keyboard with tactile switches",
                            new BigDecimal("89.99"),
                            25,
                            ProductStatus.ACTIVE
                    ),
                    new ProductCreateRequest(
                            "MS-200",
                            "Wireless Mouse",
                            "Ergonomic mouse with USB receiver",
                            new BigDecimal("34.50"),
                            40,
                            ProductStatus.ACTIVE
                    ),
                    new ProductCreateRequest(
                            "HD-300",
                            "Studio Headphones",
                            "Closed-back headphones for focused listening",
                            new BigDecimal("129.00"),
                            0,
                            ProductStatus.DRAFT
                    )
            );

            samples.stream()
                    .map(mapper::toEntity)
                    .forEach(repository::save);
        };
    }
}

