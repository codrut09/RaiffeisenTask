package com.raiffeisentask.controller.integration;

import com.raiffeisentask.model.Order;
import com.raiffeisentask.model.Product;
import com.raiffeisentask.repository.OrderRepository;
import com.raiffeisentask.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
        //execute&verify
        mockMvc.perform(get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].quantity").value(15))
                .andExpect(jsonPath("$.content[0].productId").value(testProduct.getId()));
    }

    @Test
    void testCreateOrders() throws Exception {
        //setup
        String orderJson = "[{\"quantity\":15,\"productId\":" + testProduct.getId() + "}]";

        //execute&verify
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(15))
                .andExpect(jsonPath("$[0].productId").value(testProduct.getId()));
    }

    @Test
    void testGetOrderById() throws Exception {
        //setup
        Order savedOrder = orderRepository.findAll().get(0);

        //execute&verify
        mockMvc.perform(get("/api/orders/{id}", savedOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.quantity").value(15))
                .andExpect(jsonPath("$.orderNumber").value("test_order_number"))
                .andExpect(jsonPath("$.productId").value(testProduct.getId()));
    }

    @Test
    void testUpdateOrders() throws Exception {
        //setup
        Order savedOrder = orderRepository.findAll().get(0);
        String updateJson = "[{\"id\":" + savedOrder.getId() +
                ",\"quantity\":20,\"productId\":" + testProduct.getId() +
                ",\"orderNumber\":\"updated_order\"}]";

        //execute&verify
        mockMvc.perform(put("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedOrder.getId()))
                .andExpect(jsonPath("$[0].quantity").value(20))
                .andExpect(jsonPath("$[0].orderNumber").value("test_order_number"));
    }

    @Test
    void testDeleteOrder() throws Exception {
        //setup
        Order savedOrder = orderRepository.findAll().get(0);

        //execute&verify
        mockMvc.perform(delete("/api/orders/{id}", savedOrder.getId()))
                .andExpect(status().isNoContent());

        //execute&verify
        mockMvc.perform(get("/api/orders/{id}", savedOrder.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetOrdersWithFilters() throws Exception {
        //execute&verify
        mockMvc.perform(get("/api/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "quantity")
                        .param("direction", "desc")
                        .param("orderNumber", "test")
                        .param("productId", testProduct.getId().toString())
                        .param("quantityMin", "10")
                        .param("quantityMax", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].quantity").value(15))
                .andExpect(jsonPath("$.content[0].orderNumber").value("test_order_number"));
    }

    @Test
    void testGetOrdersWithEmptyResult() throws Exception {
        //execute&verify
        mockMvc.perform(get("/api/orders")
                        .param("quantityMin", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void testCreateOrders_throwValidationException() throws Exception {
        //setup
        String orderJson = "[{\"quantity\": 0,\"productId\":" + testProduct.getId() + "}]";

        //execute&verify
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson)).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.quantity").value("Quantity must be at least 1"));
    }
}