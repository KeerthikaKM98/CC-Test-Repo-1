package com.example.catalog.product;

import com.example.catalog.common.DuplicateResourceException;
import com.example.catalog.common.ResourceNotFoundException;
import com.example.catalog.product.dto.ProductCreateRequest;
import com.example.catalog.product.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultProductServiceTest {

    @Mock
    private ProductRepository repository;

    private DefaultProductService service;

    @BeforeEach
    void setUp() {
        service = new DefaultProductService(repository, new ProductMapper());
    }

    @Test
    void createsProductWithNormalizedSku() {
        ProductCreateRequest request = request("  kb-100  ");
        when(repository.existsBySkuIgnoreCase("KB-100")).thenReturn(false);
        when(repository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = service.create(request);

        assertThat(response.sku()).isEqualTo("KB-100");
        assertThat(response.name()).isEqualTo("Keyboard");
        verify(repository).save(any(Product.class));
    }

    @Test
    void rejectsDuplicateSku() {
        ProductCreateRequest request = request("KB-100");
        when(repository.existsBySkuIgnoreCase("KB-100")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("KB-100");

        verify(repository, never()).save(any(Product.class));
    }

    @Test
    void filtersByStatusAndMapsResults() {
        Product active = new Product(
                "KB-100",
                "Keyboard",
                null,
                new BigDecimal("89.99"),
                10,
                ProductStatus.ACTIVE
        );
        when(repository.findByStatusOrderByNameAsc(ProductStatus.ACTIVE))
                .thenReturn(List.of(active));

        List<ProductResponse> results = service.findAll(ProductStatus.ACTIVE, null);

        assertThat(results)
                .extracting(ProductResponse::sku)
                .containsExactly("KB-100");
    }

    @Test
    void reportsMissingProduct() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    private ProductCreateRequest request(String sku) {
        return new ProductCreateRequest(
                sku,
                "Keyboard",
                "A test product",
                new BigDecimal("89.99"),
                10,
                ProductStatus.ACTIVE
        );
    }
}

