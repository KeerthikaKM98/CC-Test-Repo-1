package com.example.catalog.product;

import com.example.catalog.product.dto.ProductCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void clearDatabase() {
        repository.deleteAll();
    }

    @Test
    void createsAndReadsProduct() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest(
                "kb-100",
                "Mechanical Keyboard",
                "Tactile switches",
                new BigDecimal("89.99"),
                25,
                ProductStatus.ACTIVE
        );

        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/products/")))
                .andExpect(jsonPath("$.sku").value("KB-100"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID id = UUID.fromString(objectMapper.readTree(response).get("id").asText());

        mockMvc.perform(get("/api/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mechanical Keyboard"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void validatesCreateRequest() throws Exception {
        String invalidRequest = """
                {
                  "sku": "bad sku!",
                  "name": "",
                  "price": -1,
                  "stockQuantity": -5,
                  "status": null
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.validationErrors.sku").exists())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.price").exists());
    }

    @Test
    void returnsConflictForDuplicateSku() throws Exception {
        Product existing = new Product(
                "KB-100",
                "Existing Keyboard",
                null,
                new BigDecimal("70.00"),
                5,
                ProductStatus.ACTIVE
        );
        repository.saveAndFlush(existing);

        ProductCreateRequest request = new ProductCreateRequest(
                "kb-100",
                "Another Keyboard",
                null,
                new BigDecimal("90.00"),
                10,
                ProductStatus.ACTIVE
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("KB-100")));
    }

    @Test
    void filtersAndDeletesProducts() throws Exception {
        Product active = repository.save(new Product(
                "KB-100",
                "Keyboard",
                null,
                new BigDecimal("89.99"),
                10,
                ProductStatus.ACTIVE
        ));
        repository.save(new Product(
                "MS-200",
                "Mouse",
                null,
                new BigDecimal("29.99"),
                20,
                ProductStatus.DISCONTINUED
        ));

        mockMvc.perform(get("/api/products").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sku").value("KB-100"));

        mockMvc.perform(delete("/api/products/{id}", active.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/{id}", active.getId()))
                .andExpect(status().isNotFound());
    }
}

