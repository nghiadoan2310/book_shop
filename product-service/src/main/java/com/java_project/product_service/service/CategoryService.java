package com.java_project.product_service.service;

import com.java_project.product_service.dto.request.CategoryRequest;
import com.java_project.product_service.dto.response.CategoryResponse;
import com.java_project.product_service.dto.response.SimpleCategoryResponse;
import com.java_project.product_service.entity.Category;
import com.java_project.product_service.exception.AppException;
import com.java_project.product_service.exception.ErrorCode;
import com.java_project.product_service.mapper.CategoryMapper;
import com.java_project.product_service.repository.CategoryRepository;
import com.java_project.product_service.repository.httpClient.FileClient;
import com.java_project.product_service.utils.SlugUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    FileClient fileClient;

    CategoryMapper categoryMapper;

    public CategoryResponse createCategory(CategoryRequest request) {
        var category = categoryMapper.toCategory(request);

        if(!request.getParent().isEmpty()) {
            var parentCategory = categoryRepository.findById(request.getParent()).orElseThrow(() ->
                    new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            category.setParent(parentCategory);
        }

        category.setImageFile(fileClient.uploadFile(request.getImageFile()).getResult().getUrl());
        category.setSlug(SlugUtils.toSlug(category.getName()));

        categoryRepository.save(category);

        CategoryResponse categoryResponse = categoryMapper.toCategoryResponse(category);

        if(category.getParent() != null) {
            categoryResponse.setParent(new SimpleCategoryResponse(category.getParent().getId(), category.getParent().getName(),
                    category.getParent().getImageFile()));
        }

        return categoryResponse;
    }

    public CategoryResponse getCategory(String id) {
        var category = categoryRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Set<Category> categorieChild = categoryRepository.findByParentId(id);

        CategoryResponse categoryResponse = categoryMapper.toCategoryResponse(category);
//        categoryResponse.setParent(new SimpleCategoryResponse(category.getParent().getId(), category.getParent().getName(),
//                category.getParent().getImageFile()));

        return categoryResponse;
    }
}
