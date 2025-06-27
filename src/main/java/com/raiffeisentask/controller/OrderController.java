package com.raiffeisentask.controller;

import com.raiffeisentask.dto.OrderDto;
import com.raiffeisentask.model.Order;
import com.raiffeisentask.service.order.OrderService;
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

    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PostMapping
    public List<OrderDto> createOrders(@RequestBody List<OrderDto> orders) {
        return orderService.insertData(orders);
    }

    @PutMapping
    public List<OrderDto> updateOrders(@RequestBody List<OrderDto> orders) {
        return orderService.updateData(orders);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteData(id);
        return ResponseEntity.noContent().build();
    }

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

        Specification<Order> spec = (root, query, cb) -> cb.conjunction();

        if (orderNumber != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("orderNumber")), "%" + orderNumber.toLowerCase() + "%"));
        }

        if (productId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("product").get("id"), productId));
        }

        if (quantityMin != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("quantity"), quantityMin));
        }

        if (quantityMax != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("quantity"), quantityMax));
        }

        if (createdAfter != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
        }

        return orderService.getAllOrders(spec, pageable);
    }
}
