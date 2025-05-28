package com.java_project.cart_service.service;

import com.java_project.cart_service.Repository.httpClient.ProductClient;
import com.java_project.cart_service.dto.request.CartRequest;
import com.java_project.cart_service.dto.response.AddToCartResponse;
import com.java_project.cart_service.mapper.CartMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    ProductClient productClient;

    CartMapper cartMapper;

    RedisTemplate<String, Object> template;

    static String CART_KEY_PREFIX = "cart:";

    public AddToCartResponse addToCart(CartRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String key = CART_KEY_PREFIX + authentication.getName();

        var addtoCartResponse = cartMapper.toAddToCartResponse(
                productClient.getProduct(request.getProductId()).getResult());

        addtoCartResponse.setProductId(request.getProductId());
        addtoCartResponse.setQuantity(request.getQuantity());

        HashOperations<String, String, Object> hashOperations = template.opsForHash();
        hashOperations.put(key, request.getProductId(), addtoCartResponse);

        return addtoCartResponse;
    }

    public Set<AddToCartResponse> getCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String key = CART_KEY_PREFIX + authentication.getName();

        HashOperations<String, String, Object> hashOperations = template.opsForHash();
        Map<String, Object> entries = hashOperations.entries(key);

        return entries.values().stream()
                .map(entrie -> (AddToCartResponse) entrie)
                .collect(Collectors.toSet());
    }

    public void deleteProductInCart(List<String> productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String key = CART_KEY_PREFIX + authentication.getName();

        HashOperations<String, String, Object> hashOperations = template.opsForHash();
        hashOperations.delete(key, productId.toArray());
    }
}
