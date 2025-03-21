package com.java_project.identity_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity //Tạo table trong DB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    //unique giúp username là duy nhất kể cả khi có nhiều request cùng lúc, columnDefinition - không phân biệt hoa - thường
    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;

    @Column(name = "email", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String email;

    //Không được có giá trị null, giá trị mặc định là false
    @Column(name = "email_verified", nullable = false, columnDefinition = "boolean default false")
    boolean emailVerified;

    String password;

    @ManyToMany
    Set<Role> roles;
}

