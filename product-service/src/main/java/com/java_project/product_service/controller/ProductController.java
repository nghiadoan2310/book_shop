package com.java_project.product_service.controller;

import com.java_project.product_service.dto.ApiResponse;
import com.java_project.product_service.dto.request.ProductRequest;
import com.java_project.product_service.dto.response.ProductResponse;
import com.java_project.product_service.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //Dùng annotation ModelAttribute khi đầu vào là kiểu form data
    ApiResponse<ProductResponse> createProduct(@ModelAttribute ProductRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(request))
                .build();
    }

    @GetMapping(value = "/{productId}")
    ApiResponse<ProductResponse> getProduct(@PathVariable("productId") String id) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProduct(id))
                .build();
    }
}
