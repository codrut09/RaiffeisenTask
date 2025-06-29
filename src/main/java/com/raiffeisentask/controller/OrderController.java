package com.raiffeisentask.controller;

import com.raiffeisentask.dto.OrderDto;
import com.raiffeisentask.model.Order;
import com.raiffeisentask.service.order.OrderService;
import com.raiffeisentask.util.OrderSpecificationBuilder;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Get an order by ID",
            description = "Fetches an order from the database using its unique identifier."
    )
    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @Operation(
            summary = "Add one or more new orders",
            description = "Add new orders to the database"
    )
    @PostMapping
    public List<OrderDto> createOrders(@RequestBody @Valid List<OrderDto> orders) {
        return orderService.insertData(orders);
    }

    @Operation(
            summary = "Update one or more existing orders",
            description = "Update existing orders in the database"
    )
    @PutMapping
    public List<OrderDto> updateOrders(@RequestBody @Valid List<OrderDto> orders) {
        return orderService.updateData(orders);
    }

    @Operation(
            summary = "Delete an order by ID",
            description = "Deletes an order from the database using its unique identifier."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteData(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get all orders",
            description = "Fetches all orders from the database with optional filtering and pagination."
    )
    @GetMapping
    public Page<OrderDto> getOrders(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "id") String sortBy,
                                    @RequestParam(defaultValue = "asc") String direction,
                                    @RequestParam(required = false) String orderNumber,
                                    @RequestParam(required = false) Long productId,
                                    @RequestParam(required = false) Integer quantityMin,
                                    @RequestParam(required = false) Integer quantityMax,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAfter) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Order> spec = OrderSpecificationBuilder.build(
                orderNumber, productId, quantityMin, quantityMax, createdAfter
        );

        return orderService.getAllOrders(spec, pageable);
    }
}
