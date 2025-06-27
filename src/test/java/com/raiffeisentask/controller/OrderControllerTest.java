package com.raiffeisentask.controller;

import com.raiffeisentask.dto.OrderDto;
import com.raiffeisentask.exception.OrderNotFoundException;
import com.raiffeisentask.service.order.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderServiceImpl orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        orderDto = OrderDto.builder()
                .id(1L)
                .quantity(10)
                .productId(100L)
                .build();
    }

    @Test
    void testGetOrderById() {
        //mock
        when(orderService.findById(any())).thenReturn(orderDto);

        //execute
        OrderDto response = orderController.getOrderById(1L);

        //verify
        assertEquals(orderDto.getId(), response.getId());
        verify(orderService, times(1)).findById(any());
    }

    @Test
    void testUpdateOrders() {
        //setup
        List<OrderDto> orderDtoList = List.of(orderDto);

        //mock
        when(orderService.updateData(orderDtoList)).thenReturn(orderDtoList);

        //execute
        List<OrderDto> orders = orderController.updateOrders(orderDtoList);

        //verify
        assertEquals(orders.get(0), orderDto);
        verify(orderService, times(1)).updateData(orderDtoList);
    }

    @Test
    void testHandleOrderNotFoundException() {
        //mock
        when(orderService.findById(any())).thenThrow(new OrderNotFoundException("Order not found"));

        //execute
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderController.getOrderById(1L));

        //verify
        assertEquals("Order not found", exception.getMessage());
        verify(orderService, times(1)).findById(any());
    }

    @Test
    void testCreateOrders() {
        //setup
        List<OrderDto> orderDtoList = List.of(orderDto);

        //mock
        when(orderService.insertData(orderDtoList)).thenReturn(orderDtoList);

        //execute
        List<OrderDto> response = orderController.createOrders(orderDtoList);

        //verify
        assertEquals(orderDtoList, response);
        verify(orderService, times(1)).insertData(orderDtoList);
    }

    @Test
    void testDeleteOrder() {
        //execute
        orderController.deleteOrder(1L);

        //verify
        verify(orderService, times(1)).deleteData(1L);
    }

    @Test
    void testGetOrders() {
        //setup
        Page<OrderDto> mockPage = Page.empty();

        //mock
        when(orderService.getAllOrders(any(), any())).thenReturn(mockPage);

        //execute
        Page<OrderDto> response = orderController.getOrders(0, 10, "id", "asc", null, null, null, null, null);

        //verify
        assertEquals(mockPage, response);
        verify(orderService, times(1)).getAllOrders(any(), any());
    }
}