package com.raiffeisentask.service.order;

import com.raiffeisentask.dto.OrderDto;
import com.raiffeisentask.exception.OrderNotFoundException;
import com.raiffeisentask.model.Order;
import com.raiffeisentask.model.Product;
import com.raiffeisentask.repository.OrderRepository;
import com.raiffeisentask.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public OrderDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        return toDto(order);
    }

    @Override
    public List<OrderDto> insertData(List<OrderDto> orderList) {
        List<Order> orderEntities = orderList.stream()
                .map(this::toEntity)
                .toList();
        return orderRepository.saveAll(orderEntities)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getCreatedAt())
                .quantity(order.getQuantity())
                .productId(order.getProduct() != null ? order.getProduct().getId() : null)
                .build();
    }

    private Order toEntity(OrderDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .quantity(dto.getQuantity())
                .product(product)
                .build();
    }

//    @Override
//    public Page<OrderDto> getData(Map<String, Object> filters, Pageable pageable) {
//        return orderRepository.findAll(pageable).map(order -> OrderDto.builder()
//                .id(order.getId())
//                .quantity(order.getQuantity())
//                .orderDate(order.getOrderDate())
//                .productId(order.getProduct() != null ? order.getProduct().getId() : null)
//                .build())
//    }
//    @Override
//    public List<Long> updateData(List<OrderDto> orderList) {
//         List<Order> orderEntities = orderList.stream()
//                .map(orderDto -> new Order(
//                        orderDto.getId(),
//                        orderDto.getQuantity(),
//                        orderDto.getOrderDate(),
//                        null // Assuming product will be set later
//                )).toList();
//        return orderRepository.saveAll(orderEntities).stream().map(Order::getId).collect(Collectors.toList());
//    }
//
//    @Override
//    public boolean deleteData(Long id) {
//        if (orderRepository.existsById(id)) {
//            orderRepository.deleteById(id);
//            return true;
//        }
//        return false;
//    }
}
