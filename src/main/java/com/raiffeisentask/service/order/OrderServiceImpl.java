package com.raiffeisentask.service.order;

import com.raiffeisentask.dto.OrderDto;
import com.raiffeisentask.exception.OrderNotFoundException;
import com.raiffeisentask.exception.ProductNotFoundException;
import com.raiffeisentask.model.Order;
import com.raiffeisentask.model.Product;
import com.raiffeisentask.repository.OrderRepository;
import com.raiffeisentask.repository.ProductRepository;
import com.raiffeisentask.util.DtoMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public OrderDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order " + id + " not found"));
        return DtoMapperUtils.orderToDto(order);
    }

    @Override
    public List<OrderDto> insertData(List<OrderDto> orderList) {
        List<Order> orderEntities = orderList.stream()
                .map(dto -> {
                    Product product = productRepository.findById(dto.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found: " + dto.getProductId()));
                    return DtoMapperUtils.orderToEntity(dto, product);
                })
                .toList();

        log.info("Insert orders: {}", orderEntities.size());
        List<OrderDto> orderDtos = orderRepository.saveAll(orderEntities)
                .stream()
                .map(DtoMapperUtils::orderToDto)
                .toList();
        log.info("Orders inserted successfully: {}", orderDtos.size());
        return orderDtos;
    }

    @Override
    public void deleteData(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order " + id + " not found");
        }
        orderRepository.deleteById(id);
        log.info("Order {} deleted successfully", id);
    }

    @Override
    public List<OrderDto> updateData(List<OrderDto> orderList) {
        List<Order> updatedOrders = orderList.stream()
                .map(orderDto -> orderRepository.findById(orderDto.getId())
                        .map(existingOrder -> {
                            existingOrder.setQuantity(orderDto.getQuantity());
                            if (orderDto.getProductId() != null) {
                                Product product = productRepository.findById(orderDto.getProductId())
                                        .orElseThrow(() -> new ProductNotFoundException("Product id: " + orderDto.getProductId() + " not found for order id: " + orderDto.getId()));
                                existingOrder.setProduct(product);
                            }
                            return existingOrder;
                        })
                        .orElseGet(() -> {
                            log.info("Order id: {} not found", orderDto.getId());
                            return null;
                        }))
                .filter(Objects::nonNull)
                .toList();

        log.info("Updated orders: {}", updatedOrders.size());
        List<OrderDto> orderDtos = orderRepository.saveAll(updatedOrders)
                .stream()
                .map(DtoMapperUtils::orderToDto)
                .toList();
        log.info("Orders updated successfully: {}", orderDtos.size());
        return orderDtos;
    }

    @Override
    public Page<OrderDto> getAllOrders(Specification<Order> spec, Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(spec, pageable);
        return orders.map(DtoMapperUtils::orderToDto);
    }

}
