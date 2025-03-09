package com.java_project.identity_service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_project.identity_service.dto.ApiResponse;
import com.java_project.identity_service.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        //Config dữ liệu Servlet trả về
        response.setStatus(errorCode.getStatusCode().value()); //Status code
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); //Dữ liệu trả về dạng JSON

        //Tạo object là chứa những gì cần trả về
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        //ObjectMapper sử dụng để chuyển đổi kiểu dữ liệu object (ở đây là object -> String)
        ObjectMapper objectMapper = new ObjectMapper();

        //Ghi vào response
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        //Gửi về client
        response.flushBuffer();
    }
}
