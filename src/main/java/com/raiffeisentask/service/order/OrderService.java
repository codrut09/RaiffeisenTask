package com.raiffeisentask.service.order;

import com.raiffeisentask.dto.OrderDto;
import com.raiffeisentask.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface OrderService {

    OrderDto findById(Long id);

    List<OrderDto> insertData(List<OrderDto> orders);

    List<OrderDto> updateData(List<OrderDto> orders);

    void deleteData(Long id);

    Page<OrderDto> getAllOrders(Specification<Order> spec, Pageable pageable);
}
