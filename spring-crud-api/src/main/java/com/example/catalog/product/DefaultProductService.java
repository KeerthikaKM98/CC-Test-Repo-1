package com.example.catalog.product;

import com.example.catalog.common.DuplicateResourceException;
import com.example.catalog.common.ResourceNotFoundException;
import com.example.catalog.product.dto.ProductCreateRequest;
import com.example.catalog.product.dto.ProductResponse;
import com.example.catalog.product.dto.ProductStatusRequest;
import com.example.catalog.product.dto.ProductUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DefaultProductService implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public DefaultProductService(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ProductResponse create(ProductCreateRequest request) {
        String normalizedSku = mapper.normalizeSku(request.sku());
        if (repository.existsBySkuIgnoreCase(normalizedSku)) {
            throw new DuplicateResourceException("A product with SKU '" + normalizedSku + "' already exists");
        }

        Product product = mapper.toEntity(request);
        return mapper.toResponse(repository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll(ProductStatus status, String query) {
        String normalizedQuery = query == null ? null : query.trim();
        boolean hasQuery = normalizedQuery != null && !normalizedQuery.isBlank();

        List<Product> products;
        if (status != null && hasQuery) {
            products = repository
                    .findByStatusAndNameContainingIgnoreCaseOrStatusAndDescriptionContainingIgnoreCaseOrderByNameAsc(
                            status,
                            normalizedQuery,
                            status,
                            normalizedQuery
                    );
        } else if (status != null) {
            products = repository.findByStatusOrderByNameAsc(status);
        } else if (hasQuery) {
            products = repository
                    .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByNameAsc(
                            normalizedQuery,
                            normalizedQuery
                    );
        } else {
            products = repository.findAllByOrderByNameAsc();
        }

        return products.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(UUID id) {
        return mapper.toResponse(requireProduct(id));
    }

    @Override
    public ProductResponse update(UUID id, ProductUpdateRequest request) {
        Product product = requireProduct(id);
        String normalizedSku = mapper.normalizeSku(request.sku());

        repository.findBySkuIgnoreCase(normalizedSku)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "A product with SKU '" + normalizedSku + "' already exists"
                    );
                });

        product.updateDetails(
                normalizedSku,
                request.name().trim(),
                mapper.normalizeDescription(request.description()),
                request.price(),
                request.stockQuantity(),
                request.status()
        );

        return mapper.toResponse(repository.save(product));
    }

    @Override
    public ProductResponse changeStatus(UUID id, ProductStatusRequest request) {
        Product product = requireProduct(id);
        product.changeStatus(request.status());
        return mapper.toResponse(repository.save(product));
    }

    @Override
    public void delete(UUID id) {
        Product product = requireProduct(id);
        repository.delete(product);
    }

    private Product requireProduct(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
}

