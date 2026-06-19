package com.example.catalog.product;

import com.example.catalog.product.dto.ProductCreateRequest;
import com.example.catalog.product.dto.ProductResponse;
import com.example.catalog.product.dto.ProductStatusRequest;
import com.example.catalog.product.dto.ProductUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductResponse create(ProductCreateRequest request);

    List<ProductResponse> findAll(ProductStatus status, String query);

    ProductResponse findById(UUID id);

    ProductResponse update(UUID id, ProductUpdateRequest request);

    ProductResponse changeStatus(UUID id, ProductStatusRequest request);

    void delete(UUID id);
}

