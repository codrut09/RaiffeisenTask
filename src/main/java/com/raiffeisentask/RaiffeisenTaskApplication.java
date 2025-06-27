package com.raiffeisentask;

import com.raiffeisentask.model.Order;
import com.raiffeisentask.model.Product;
import com.raiffeisentask.repository.OrderRepository;
import com.raiffeisentask.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class RaiffeisenTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaiffeisenTaskApplication.class, args);
    }

    @Bean
    CommandLineRunner loadMockData(ProductRepository productRepository, OrderRepository orderRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                Product laptop = new Product(null, "Laptop", new BigDecimal("3500.00"), 10, null);
                Product phone = new Product(null, "Phone", new BigDecimal("2500.00"), 25, null);
                productRepository.saveAll(List.of(laptop, phone));

                Order order1 = Order.builder()
                        .quantity(2)
                        .orderNumber(UUID.randomUUID().toString())
                        .product(laptop)
                        .build();

                Order order2 = Order.builder()
                        .quantity(6)
                        .orderNumber(UUID.randomUUID().toString())
                        .product(laptop)
                        .build();

                Order order3 = Order.builder()
                        .quantity(1)
                        .orderNumber(UUID.randomUUID().toString())
                        .product(phone)
                        .build();

                Order order4 = Order.builder()
                        .quantity(11)
                        .orderNumber(UUID.randomUUID().toString())
                        .product(phone)
                        .build();

                orderRepository.saveAll(List.of(order1, order2, order3, order4));
            }
        };
    }
}