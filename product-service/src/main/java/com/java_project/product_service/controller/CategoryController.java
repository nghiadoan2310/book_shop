package com.java_project.product_service.controller;

import com.java_project.product_service.dto.ApiResponse;
import com.java_project.product_service.dto.request.CategoryRequest;
import com.java_project.product_service.dto.response.CategoryResponse;
import com.java_project.product_service.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping(value = "/create/category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<CategoryResponse> createProduct(@ModelAttribute CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.createCategory(request))
                .build();
    }

    @GetMapping(value = "/category/{categoryId}")
    ApiResponse<CategoryResponse> getCategory(@PathVariable("categoryId") String id) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.getCategory(id))
                .build();
    }
}
