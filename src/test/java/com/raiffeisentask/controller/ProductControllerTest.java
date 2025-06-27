package com.raiffeisentask.controller;

import com.raiffeisentask.dto.ProductDto;
import com.raiffeisentask.dto.ProductWithOrdersDto;
import com.raiffeisentask.service.product.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void getProductById_shouldReturnProduct() {
        //setup
        ProductDto productDto = ProductDto.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.TEN)
                .build();

        //mock
        when(productService.findById(1L)).thenReturn(productDto);

        //execute
        ResponseEntity<ProductDto> response = productController.getProductById(1L);

        //validate
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getName()).isEqualTo("Test Product");
        verify(productService).findById(1L);
    }

    @Test
    void createProducts_shouldReturnCreatedProducts() {
        //setup
        ProductDto productDto = ProductDto.builder()
                .name("Test Product")
                .price(BigDecimal.TEN)
                .build();
        List<ProductDto> products = List.of(productDto);

        //mock
        when(productService.insertData(products)).thenReturn(products);

        //execute
        List<ProductDto> response = productController.createProducts(products);

        //validate
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("Test Product");
        verify(productService).insertData(products);
    }

    @Test
    void updateOrders_shouldReturnUpdatedProducts() {
        //setup
        ProductDto productDto = ProductDto.builder()
                .id(1L)
                .name("Updated Product")
                .price(BigDecimal.TEN)
                .build();
        List<ProductDto> products = List.of(productDto);

        //mock
        when(productService.updateData(products)).thenReturn(products);

        //execute
        List<ProductDto> response = productController.updateOrders(products);

        //validate
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("Updated Product");
        verify(productService).updateData(products);
    }

    @Test
    void deleteOrder_shouldReturnNoContent() {
        //mock
        doNothing().when(productService).deleteData(1L);

        //execute
        ResponseEntity<Void> response = productController.deleteOrder(1L);

        //validate
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(productService).deleteData(1L);
    }

    @Test
    void getProductWithOrders_shouldReturnProductWithOrders() {
        //setup
        ProductDto productDto = ProductDto.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.TEN)
                .build();
        ProductWithOrdersDto dto = ProductWithOrdersDto.builder()
                .product(productDto)
                .orders(List.of())
                .build();

        //mock
        when(productService.getProductWithOrders(1L)).thenReturn(dto);

        //execute
        ResponseEntity<ProductWithOrdersDto> response = productController.getProductWithOrders(1L);

        //validate
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProduct().getId()).isEqualTo(1L);
        assertThat(response.getBody().getOrders()).isEmpty();
        verify(productService).getProductWithOrders(1L);
    }
}