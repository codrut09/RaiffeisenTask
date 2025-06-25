package com.raiffeisentask.service.product;

import com.raiffeisentask.dto.ProductDto;
import com.raiffeisentask.exception.ProductNotFoundException;
import com.raiffeisentask.model.Product;
import com.raiffeisentask.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductDto findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return toDto(product);
    }

    @Override
    public List<ProductDto> insertData(List<ProductDto> products) {
        List<Product> productEntities = products.stream()
                .map(this::toEntity)
                .toList();
        return productRepository.saveAll(productEntities)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

    private Product toEntity(ProductDto dto) {
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();
    }

//    @Override
//    public Page<Product> getData(Map<String, Object> filters, Pageable pageable) {
//        return productRepository.findAll(pageable);
//    }
//
//    @Override
//    public List<Product> updateData(List<Product> products) {
//        return productRepository.saveAll(products);
//    }

//    @Override
//    public boolean deleteData(Long id) {
//        if (productRepository.existsById(id)) {
//            productRepository.deleteById(id);
//            return true;
//        }
//        return false;
//    }
//
}
