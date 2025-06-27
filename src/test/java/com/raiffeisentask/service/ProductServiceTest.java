package com.raiffeisentask.service;

import com.raiffeisentask.dto.ProductDto;
import com.raiffeisentask.dto.ProductWithOrdersDto;
import com.raiffeisentask.exception.ProductNotFoundException;
import com.raiffeisentask.model.Product;
import com.raiffeisentask.repository.ProductRepository;
import com.raiffeisentask.service.product.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void findById_shouldReturnProduct() {
        //setup
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.TEN)
                .build();

        //mock
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        //execute
        ProductDto result = productService.findById(1L);

        //verify
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        //mock
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        //execute&verify
        assertThatThrownBy(() -> productService.findById(1L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void insertData_shouldReturnInsertedProducts() {
        //setup
        ProductDto inputDto = ProductDto.builder()
                .name("Test Product")
                .price(BigDecimal.TEN)
                .build();
        Product savedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.TEN)
                .build();

        //mock
        when(productRepository.saveAll(any())).thenReturn(List.of(savedProduct));

        //execute
        List<ProductDto> result = productService.insertData(List.of(inputDto));

        //verify
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
        verify(productRepository).saveAll(any());
    }

    @Test
    void updateData_shouldReturnUpdatedProducts() {
        //setup
        ProductDto inputDto = ProductDto.builder()
                .id(1L)
                .name("Updated Product")
                .price(BigDecimal.TEN)
                .build();
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Product")
                .price(BigDecimal.TEN)
                .build();

        //mock
        when(productRepository.findById(1L)).thenReturn(Optional.of(updatedProduct));
        when(productRepository.saveAll(any())).thenReturn(List.of(updatedProduct));

        //execute
        List<ProductDto> result = productService.updateData(List.of(inputDto));

        //verify
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Updated Product");
        verify(productRepository).saveAll(any());
    }

    @Test
    void deleteData_shouldDeleteProduct() {
        //mock
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        //execute
        productService.deleteData(1L);

        //verify
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteData_shouldThrowWhenNotFound() {
        //mock
        when(productRepository.existsById(1L)).thenReturn(false);

        //execute&verify
        assertThatThrownBy(() -> productService.deleteData(1L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void getProductWithOrders_shouldReturnProductWithOrders() {
        //setup
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.TEN)
                .orders(List.of())
                .build();

        //mock
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        //execute
        ProductWithOrdersDto result = productService.getProductWithOrders(1L);

        //verify
        assertThat(result).isNotNull();
        assertThat(result.getProduct().getId()).isEqualTo(1L);
        assertThat(result.getProduct().getName()).isEqualTo("Test Product");
        assertThat(result.getOrders()).isEmpty();
    }

    @Test
    void getProductWithOrders_shouldThrowWhenNotFound() {
        //mock
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        //execute&verify
        assertThatThrownBy(() -> productService.getProductWithOrders(1L))
                .isInstanceOf(ProductNotFoundException.class);
    }
}