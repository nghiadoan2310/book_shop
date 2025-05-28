package com.java_project.cart_service.controller;

import com.java_project.cart_service.dto.ApiResponse;
import com.java_project.cart_service.dto.request.CartRequest;
import com.java_project.cart_service.dto.response.AddToCartResponse;
import com.java_project.cart_service.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @PostMapping("/add/product")
    ApiResponse<AddToCartResponse> addToCart(@RequestBody CartRequest request) {
        return ApiResponse.<AddToCartResponse>builder()
                .result(cartService.addToCart(request))
                .build();
    }

    @GetMapping
    ApiResponse<Set<AddToCartResponse>> getCart() {
        return ApiResponse.<Set<AddToCartResponse>>builder()
                .result(cartService.getCart())
                .build();
    }

    @DeleteMapping("/delete")
    void deleteProductInCart(@RequestBody List<String> productId) {
        cartService.deleteProductInCart(productId);
    }
}
