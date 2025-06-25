package com.raiffeisentask.service.product;

import com.raiffeisentask.dto.ProductDto;
import com.raiffeisentask.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {

    ProductDto findById(Long id);
    List<ProductDto> insertData(List<ProductDto> products);
//    Page<ProductDto> getData(Map<String, Object> filters, Pageable pageable);
//    List<ProductDto> updateData(List<ProductDto> products);
//    boolean deleteData(Long id);
}
