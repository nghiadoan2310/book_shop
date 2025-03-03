package com.java_project.identity_service.repository;

import com.java_project.identity_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByUsername(String username); //Kiểm tra username đã tồn tại trong db chưa
    Optional<User> findByUsername(String username);
}
