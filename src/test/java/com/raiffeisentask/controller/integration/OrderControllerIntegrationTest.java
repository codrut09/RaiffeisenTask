package com.raiffeisentask.controller.integration;

import com.raiffeisentask.model.Order;
import com.raiffeisentask.model.Product;
import com.raiffeisentask.repository.OrderRepository;
import com.raiffeisentask.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class OrderControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private MockMvc mockMvc;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        orderRepository.deleteAll();
        productRepository.deleteAll();

        testProduct = productRepository.save(Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100.0))
                .build());

        orderRepository.save(Order.builder()
                .quantity(15)
                .orderNumber("test_order_number")
                .product(testProduct)
                .build());
    }

    @Test
    void testGetOrders() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].quantity").value(15))
                .andExpect(jsonPath("$.content[0].productId").value(testProduct.getId()));
    }

    @Test
    void testCreateOrders() throws Exception {
        String orderJson = "[{\"quantity\":15,\"productId\":" + testProduct.getId() + "}]";

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(15))
                .andExpect(jsonPath("$[0].productId").value(testProduct.getId()));
    }
}