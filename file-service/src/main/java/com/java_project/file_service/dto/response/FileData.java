package com.java_project.file_service.dto.response;

import org.springframework.core.io.Resource;

//khi dùng record sẽ không cần phải getter, setter, ... vì nó sẽ sinh tự động => giảm thiểu code (từ java 16)
public record FileData(String contentType, Resource resource) {}
