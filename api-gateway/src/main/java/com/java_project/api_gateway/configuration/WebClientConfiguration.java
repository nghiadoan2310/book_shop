package com.java_project.api_gateway.configuration;

import com.java_project.api_gateway.repository.IdentityClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfiguration {
    @Bean
    //Set baseUrl cho các request (1 cách gọi Api từ 1 service khác mà không cần cài depend feign)
    WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/identity")
                .build();
    }

    @Bean
    //Khi dùng interface IdentityClient (khai báo trong repository), HttpServiceProxyFactory giúp tự động tạo HTTP Client từ Interface
    IdentityClient identityClient(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();

        return httpServiceProxyFactory.createClient(IdentityClient.class);
    }
}
