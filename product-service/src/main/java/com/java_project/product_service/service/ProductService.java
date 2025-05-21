package com.java_project.product_service.service;

import com.java_project.product_service.dto.request.ProductRequest;
import com.java_project.product_service.dto.response.ProductResponse;
import com.java_project.product_service.exception.AppException;
import com.java_project.product_service.exception.ErrorCode;
import com.java_project.product_service.mapper.CategoryMapper;
import com.java_project.product_service.mapper.ProductMapper;
import com.java_project.product_service.repository.CategoryRepository;
import com.java_project.product_service.repository.DistributorRepository;
import com.java_project.product_service.repository.ProductRepository;
import com.java_project.product_service.repository.httpClient.FileClient;
import com.java_project.product_service.utils.SlugUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    DistributorRepository distributorRepository;
    FileClient fileClient;

    ProductMapper productMapper;
    CategoryMapper categoryMapper;

    public ProductResponse createProduct(ProductRequest request) {
        var product = productMapper.toProduct(request);

        product.setUnitsInOrder(0);

        product.setImageFile(fileClient.uploadFile(request.getImageFile()).getResult().getUrl());

        var category = categoryRepository.findById(request.getCategory())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        var distributor = distributorRepository.findById(request.getDistributor())
                .orElseThrow(() -> new AppException(ErrorCode.DISTRIBUTOR_NOT_FOUND));

        product.setCategory(category);
        product.setDistributor(distributor);
        product.setSlug(SlugUtils.toSlug(product.getProductName()));

        var productResponse = productMapper.toProductResponse(productRepository.save(product));

        productResponse.setCategory(categoryMapper
                .toSimpleCategoryResponse(category));

        return productResponse;
    }

    public ProductResponse getProduct(String id) {
        var product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        var productResponse = productMapper.toProductResponse(product);

        productResponse.setCategory(categoryMapper.toSimpleCategoryResponse(product.getCategory()));

        return productResponse;
    }
}
