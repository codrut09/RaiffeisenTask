package com.raiffeisentask.service.product;

import com.raiffeisentask.dto.ProductDto;

import java.util.List;

public interface ProductService {

    ProductDto findById(Long id);

    List<ProductDto> insertData(List<ProductDto> products);

    List<ProductDto> updateData(List<ProductDto> products);

    void deleteData(Long id);
}
