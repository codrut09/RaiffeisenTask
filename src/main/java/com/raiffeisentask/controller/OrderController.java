package com.raiffeisentask.controller;

import com.raiffeisentask.dto.OrderDto;
import com.raiffeisentask.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok().body(orderService.findById(id));
    }

    @PostMapping
    public List<OrderDto> createOrders(@RequestBody List<OrderDto> orders) {
        return orderService.insertData(orders);
    }

//    @GetMapping
//    public Page<OrderDto> getOrders(
//            @RequestParam Map<String, String> params,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        Pageable pageable = PageRequest.of(page, size);
//        Map<String, Object> filters = new HashMap<>(params);
//        filters.remove("page");
//        filters.remove("size");
//        return orderService.getData(filters, pageable);
//    }

//    @PutMapping
//    public ResponseEntity<List<Long>> updateOrders(@RequestBody List<OrderDto> orders) {
//        List<Long> ids = orderService.updateData(orders);
//        return ResponseEntity.status(HttpStatus.OK).body(ids);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
//        if (orderService.deleteData(id)) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
}
