package com.raiffeisentask.util;

import com.raiffeisentask.dto.OrderDto;
import com.raiffeisentask.dto.ProductDto;
import com.raiffeisentask.model.Order;
import com.raiffeisentask.model.Product;

import java.util.UUID;

public class DtoMapperUtils {

    public static OrderDto orderToDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .quantity(order.getQuantity())
                .productId(order.getProduct() != null ? order.getProduct().getId() : null)
                .build();
    }

    public static Order orderToEntity(OrderDto dto, Product product) {
        return Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .quantity(dto.getQuantity())
                .product(product)
                .build();
    }

    public static ProductDto productToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

    public static Product productToEntity(ProductDto dto) {
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();
    }
}
