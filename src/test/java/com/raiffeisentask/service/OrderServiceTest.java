package com.raiffeisentask.service;

import com.raiffeisentask.dto.OrderDto;
import com.raiffeisentask.exception.OrderNotFoundException;
import com.raiffeisentask.exception.ProductNotFoundException;
import com.raiffeisentask.model.Order;
import com.raiffeisentask.model.Product;
import com.raiffeisentask.repository.OrderRepository;
import com.raiffeisentask.repository.ProductRepository;
import com.raiffeisentask.service.order.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void testFindOrderById_Success() {
        //setup
        Long orderId = 5L;
        Order expectedOrder = Order.builder()
                .id(orderId)
                .quantity(125)
                .product(Product.builder().id(52L).build())
                .build();

        //mock
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(expectedOrder));

        //execute
        OrderDto result = orderService.findById(orderId);

        //verify
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(52L, result.getProductId());
        assertEquals(125, result.getQuantity());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testFindOrderById_NotFound() {
        //setup
        Long orderId = 1L;

        //mock
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        //execute&verify
        assertThrows(RuntimeException.class, () -> orderService.findById(orderId));
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testCreateOrder() {
        //setup
        Order expectedOrder1 = Order.builder().orderNumber("test_1_order_number").build();
        Order expectedOrder2 = Order.builder().orderNumber("test_2_order_number").build();
        OrderDto insertOrder1 = OrderDto.builder().build();
        OrderDto insertOrder2 = OrderDto.builder().build();
        List<OrderDto> insertedOrderList = List.of(insertOrder1, insertOrder2);
        List<Order> expectedOrderList = List.of(expectedOrder1, expectedOrder2);

        //mock
        when(orderRepository.saveAll(anyList())).thenReturn(expectedOrderList);
        when(productRepository.findById(any())).thenReturn(Optional.of(mock(Product.class)));

        //execute
        List<OrderDto> resultOrderList = orderService.insertData(insertedOrderList);

        //verify
        assertEquals(2, resultOrderList.size());
        assertEquals("test_1_order_number", resultOrderList.get(0).getOrderNumber());
        assertEquals("test_2_order_number", resultOrderList.get(1).getOrderNumber());
        verify(orderRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testDeleteOrder() {
        //setup
        Long orderId = 1L;

        //mock
        doNothing().when(orderRepository).deleteById(orderId);
        when(orderRepository.existsById(orderId)).thenReturn(Boolean.TRUE);

        //execute
        orderService.deleteData(orderId);

        //verify
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void testDeleteOrder_NotFound() {
        //setup
        Long orderId = 1L;

        //mock
        when(orderRepository.existsById(orderId)).thenReturn(Boolean.FALSE);

        //execute&verify
        OrderNotFoundException orderNotFoundException = assertThrows(OrderNotFoundException.class, () -> orderService.deleteData(orderId));
        assertEquals("Order 1 not found", orderNotFoundException.getMessage());
    }

    @Test
    void testUpdateOrder_Success() {
        //setup
        Order existingOrder = Order.builder()
                .id(1L)
                .quantity(10)
                .product(Product.builder().id(100L).build())
                .build();

        Order updatedOrder = Order.builder()
                .id(1L)
                .quantity(20)
                .product(Product.builder().id(101L).build())
                .build();

        OrderDto updateOrderDto = OrderDto.builder()
                .id(1L)
                .quantity(20)
                .productId(101L)
                .build();

        List<OrderDto> updateOrderDtoList = List.of(updateOrderDto);
        List<Order> updatedOrderList = List.of(updatedOrder);

        //mock
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(productRepository.findById(101L)).thenReturn(Optional.of(Product.builder().id(101L).build()));
        when(orderRepository.saveAll(anyList())).thenReturn(updatedOrderList);

        //execute
        List<OrderDto> resultOrderList = orderService.updateData(updateOrderDtoList);

        //verify
        assertEquals(1, resultOrderList.size());
        assertEquals(1L, resultOrderList.get(0).getId());
        assertEquals(20, resultOrderList.get(0).getQuantity());
        assertEquals(101L, resultOrderList.get(0).getProductId());
        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(101L);
        verify(orderRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testUpdateOrder_ProductNotFound() {
        //setup
        Order existingOrder = Order.builder()
                .id(1L)
                .quantity(10)
                .product(Product.builder().id(100L).build())
                .build();

        OrderDto updateOrderDto = OrderDto.builder()
                .id(1L)
                .quantity(20)
                .productId(101L)
                .build();

        List<OrderDto> updateOrderDtoList = List.of(updateOrderDto);

        //mock
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(productRepository.findById(101L)).thenReturn(Optional.empty()); // Simulate product not found

        //execute&verify
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> orderService.updateData(updateOrderDtoList));
        assertEquals("Product id: 101 not found for order id: 1", exception.getMessage());
        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(101L);
    }
}