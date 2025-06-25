package com.raiffeisentask.service.order;

import com.raiffeisentask.dto.OrderDto;
import com.raiffeisentask.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {

    OrderDto findById(Long id);
    List<OrderDto> insertData(List<OrderDto> orders);
//    Page<OrderDto> getData(Map<String, Object> filters, Pageable pageable);
//    List<Long> updateData(List<OrderDto> orders);
//    boolean deleteData(Long id);
}
