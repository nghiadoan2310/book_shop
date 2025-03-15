package com.java_project.api_gateway.configuration;

import com.java_project.api_gateway.repository.IdentityClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

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

    @Bean
    //Sử dụng CorsWebFilter để spring webFlux xử lý cors
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //addAllowed thêm các cấu hình, setAllowed ghi đè các cấu hình cors đã có
        corsConfiguration.setAllowedOrigins(List.of("*")); //cho phép tất cả các domain (VD: http://localhost:3000)
        corsConfiguration.setAllowedHeaders(List.of("*")); //cho phép tất cả các header
        corsConfiguration.setAllowedMethods(List.of("*")); //cho phép tất cả các phương thức VD: POST, GET, ...

        //Cho phép tất cả các API
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        //Có thể cấu hình
        //chỉ cho phép internal với domain set trong securedCorsConfiguration
        //urlBasedCorsConfigurationSource.registerCorsConfiguration("/internal/**", securedCorsConfiguration);

        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }
}
