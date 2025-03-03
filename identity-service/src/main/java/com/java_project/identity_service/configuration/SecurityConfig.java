package com.java_project.identity_service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity //Security theo endpoint
@EnableMethodSecurity //Security theo method
//Spring security filter gồm nhiều filter (chức năng khá giống với middleware trong nodejs)
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINT = {"/users", "/auth/login", "/auth/introspect", "/auth//logout",
        "/auth/refresh"
    };

//    @Value("${jwt.signerKey}")
//    private String signerKey;

    @Autowired
    private CustomJwtEncoder customJwtEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(request ->
                request
                        ////Không authenticated với các endpoint có permitAll
                        .requestMatchers(HttpMethod.POST , PUBLIC_ENDPOINT).permitAll()
                        //Những user có role admin mới có thể truy cập endpoint
                        //Sử dụng security theo endpoint (theo method sẽ dùng trong Service)
                        //C1:
                        //.requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        //C2:
                        //.requestMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN")
                        //C3: dùng PreAuthorize và PostAuthorize (sử dụng ngay trong file Service)
                        .anyRequest().authenticated());

        //Những endpoint khác cần xác thực token thì mới tới Controller
        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer ->
                        jwtConfigurer.decoder(customJwtEncoder) //decoder JWT
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        //Xử lý lỗi xác thực JWT (không đúng, hết hạn,..)
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );

        //Tắt bảo vệ tấn công csrf
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        //Chuyển những giá trị key scope trong JWT sang Authority và thêm ROLE_ ở mỗi role trong scope
        //Security spring quy định là key scope dùng để nhận biết quyền của mỗi user
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        //Sau khi set thực hiện việc convert
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    //config cors
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("http://localhost:3000");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        //Áp dụng cors cho các endpoint
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        //áp dụng cors cho tất cả endpoint
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

//    @Bean
//    JwtDecoder jwtDecoder() {
//        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
//
//        return NimbusJwtDecoder
//                .withSecretKey(secretKeySpec)
//                .macAlgorithm(MacAlgorithm.HS512)
//                .build();
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
