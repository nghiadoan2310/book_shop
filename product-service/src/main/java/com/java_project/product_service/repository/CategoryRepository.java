package com.java_project.product_service.repository;

import com.java_project.product_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    // Tìm các category gốc (không có cha)
    Set<Category> findByParentIsNull();
    Set<Category> findByParentId(String id);
}
