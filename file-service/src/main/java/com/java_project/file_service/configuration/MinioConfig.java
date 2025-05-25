package com.java_project.file_service.configuration;

import io.minio.MinioClient;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinioConfig {
    @Value("${minio.access_key}")
    String access_key;

    @Value("${minio.secret_key}")
    String secret_key;

    @Value("${minio.endpoint}")
    String endpoint;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(access_key, secret_key)
                .build();
    }
}
