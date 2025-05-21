package com.java_project.product_service.mapper;

import com.java_project.product_service.dto.request.CategoryRequest;
import com.java_project.product_service.dto.response.CategoryResponse;
import com.java_project.product_service.dto.response.ProductResponse;
import com.java_project.product_service.dto.response.SimpleCategoryResponse;
import com.java_project.product_service.entity.Category;
import com.java_project.product_service.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "imageFile", ignore = true)
    @Mapping(target = "parent", ignore = true)
    Category toCategory(CategoryRequest request);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    CategoryResponse toCategoryResponse(Category category);

    SimpleCategoryResponse toSimpleCategoryResponse(Category category);
}
