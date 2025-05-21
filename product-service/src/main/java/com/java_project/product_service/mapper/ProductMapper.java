package com.java_project.product_service.mapper;

import com.java_project.product_service.dto.request.ProductRequest;
import com.java_project.product_service.dto.response.ProductResponse;
import com.java_project.product_service.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "imageFile", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "distributor", ignore = true)
    Product toProduct(ProductRequest request);

    @Mapping(target = "category", ignore = true)
    ProductResponse toProductResponse(Product product);
}
