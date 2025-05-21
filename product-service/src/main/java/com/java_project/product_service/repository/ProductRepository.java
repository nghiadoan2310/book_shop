package com.java_project.product_service.repository;

import com.java_project.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    // Tìm sản phẩm theo category ID cụ thể
    Set<Product> findByCategoryId(String categoryId);
}
