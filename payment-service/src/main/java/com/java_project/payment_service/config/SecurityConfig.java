package com.java_project.payment_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity //Security theo endpoint
@EnableMethodSecurity //Security theo method
//Spring security filter gồm nhiều filter (chức năng khá giống với middleware trong nodejs)
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINT = {
        "/vnp-payment", "/vnpay_jsp/vnpay_return.jsp"
    };

    private final CustomJwtDecoder customJwtEncoder;

    public SecurityConfig(CustomJwtDecoder customJwtEncoder) {
        this.customJwtEncoder = customJwtEncoder;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(request ->
                request
                        //Không authenticated với các endpoint có permitAll
                        .requestMatchers(PUBLIC_ENDPOINT).permitAll()
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

}
