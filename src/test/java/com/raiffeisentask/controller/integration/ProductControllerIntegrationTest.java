package com.raiffeisentask.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raiffeisentask.dto.ProductDto;
import com.raiffeisentask.model.Product;
import com.raiffeisentask.repository.ProductRepository;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {

        productRepository.deleteAll();

        testProduct = Product.builder()
                .name("Test Product")
                .price(BigDecimal.TEN)
                .build();
        testProduct = productRepository.save(testProduct);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void findById_shouldReturnProduct() throws Exception {
        //execute&verify
        mockMvc.perform(get("/api/products/{id}", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testProduct.getId()))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(10));
    }

    @Test
    void findById_shouldReturn404WhenNotFound() throws Exception {
        //execute&verify
        mockMvc.perform(get("/api/products/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void insertData_shouldReturnCreatedProducts() throws Exception {
        //setup
        ProductDto newProduct = ProductDto.builder()
                .name("New Product")
                .price(BigDecimal.valueOf(20))
                .stock(100)
                .build();

        //execute&verify
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(newProduct))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("New Product"))
                .andExpect(jsonPath("$[0].price").value(20));
    }

    @Test
    void updateData_shouldReturnUpdatedProducts() throws Exception {
        //setup
        ProductDto updatedProduct = ProductDto.builder()
                .id(testProduct.getId())
                .name("Updated Product")
                .price(BigDecimal.valueOf(30))
                .stock(100)
                .build();

        //execute&verify
        mockMvc.perform(put("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(updatedProduct))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Updated Product"))
                .andExpect(jsonPath("$[0].price").value(30));
    }

    @Test
    void deleteData_shouldReturn204() throws Exception {
        //execute&verify
        mockMvc.perform(delete("/api/products/{id}", testProduct.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteData_shouldReturn404WhenNotFound() throws Exception {
        //execute&verify
        mockMvc.perform(delete("/api/products/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductWithOrders_shouldReturnProductWithOrders() throws Exception {
        //execute&verify
        mockMvc.perform(get("/api/products/{id}/orders", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.product.id").value(testProduct.getId()))
                .andExpect(jsonPath("$.product.name").value("Test Product"))
                .andExpect(jsonPath("$.orders").isArray());
    }

    @Test
    void getProductWithOrders_shouldReturn404WhenNotFound() throws Exception {
        //execute&verify
        mockMvc.perform(get("/api/products/{id}/orders", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowLazyInitializationException_whenAccessingOrdersOutsideTransaction() {
        //setup
        Product product = productRepository.findById(testProduct.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        //execute&verify
        Assertions.assertThrows(LazyInitializationException.class, () -> product.getOrders().size());
    }

    @Test
    void insertData_shouldReturnValidationException() throws Exception {
        //setup
        ProductDto newProduct = ProductDto.builder()
                .name("New Product")
                .price(BigDecimal.valueOf(20))
                .build();

        //execute&verify
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(newProduct))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stock").value("Stock is required"));
    }


}