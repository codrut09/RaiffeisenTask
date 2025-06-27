package com.raiffeisentask.service.product;

import com.raiffeisentask.dto.OrderDto;
import com.raiffeisentask.dto.ProductDto;
import com.raiffeisentask.dto.ProductWithOrdersDto;
import com.raiffeisentask.exception.ProductNotFoundException;
import com.raiffeisentask.model.Product;
import com.raiffeisentask.repository.ProductRepository;
import com.raiffeisentask.util.DtoMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductDto findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return DtoMapperUtils.productToDto(product);
    }

    @Override
    public List<ProductDto> insertData(List<ProductDto> products) {
        List<Product> productEntities = products.stream()
                .map(DtoMapperUtils::productToEntity)
                .toList();
        log.info("Inserted {} products", productEntities.size());
        List<ProductDto> productDtos = productRepository.saveAll(productEntities)
                .stream()
                .map(DtoMapperUtils::productToDto)
                .toList();
        log.info("Products inserted successfully: {}", productEntities.size());
        return productDtos;
    }

    @Override
    public List<ProductDto> updateData(List<ProductDto> products) {
        List<Product> updatedProducts = products.stream()
                .map(dto -> {
                    Product existingProduct = productRepository.findById(dto.getId())
                            .orElseThrow(() -> new ProductNotFoundException("Product id: " + dto.getId() + " not found"));
                    existingProduct.setName(dto.getName());
                    existingProduct.setPrice(dto.getPrice());
                    existingProduct.setStock(dto.getStock());
                    return existingProduct;
                })
                .toList();
        log.info("Updated products: {}", updatedProducts.size());
        List<ProductDto> productDtos = productRepository.saveAll(updatedProducts)
                .stream()
                .map(DtoMapperUtils::productToDto)
                .toList();
        log.info("Products updated successfully: {}", updatedProducts.size());
        return productDtos;
    }

    @Override
    public void deleteData(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product with id: " + id + " not found");
        }
        productRepository.deleteById(id);
        log.info("Product {} deleted successfully", id);
    }

    @Override
    public ProductWithOrdersDto getProductWithOrders(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        ProductDto productDto = DtoMapperUtils.productToDto(product);
        List<OrderDto> orderDtos = product.getOrders().stream()
                .map(DtoMapperUtils::orderToDto)
                .toList();
        ProductWithOrdersDto dto = new ProductWithOrdersDto();
        dto.setProduct(productDto);
        dto.setOrders(orderDtos);
        return ProductWithOrdersDto.builder()
                .product(productDto)
                .orders(orderDtos)
                .build();
    }

}
