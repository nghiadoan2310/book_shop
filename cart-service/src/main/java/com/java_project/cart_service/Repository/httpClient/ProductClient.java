package com.java_project.cart_service.Repository.httpClient;

import com.java_project.cart_service.configuration.AuthenticationRequestInterceptor;
import com.java_project.cart_service.dto.ApiResponse;
import com.java_project.cart_service.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${app.services.product.url}",
        configuration = AuthenticationRequestInterceptor.class)
public interface ProductClient {
    @GetMapping("/{productId}")
    ApiResponse<ProductResponse> getProduct(@PathVariable("productId") String id);
}
