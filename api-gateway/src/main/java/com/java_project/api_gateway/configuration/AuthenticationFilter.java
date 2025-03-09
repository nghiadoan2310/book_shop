package com.java_project.api_gateway.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_project.api_gateway.dto.ApiResponse;
import com.java_project.api_gateway.service.IdentityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    IdentityService identityService;
    ObjectMapper objectMapper;

    @Override
    //Nơi kiểm tra request hoặc thay đổi response trước khi vào microservice hoặc trước khi trả về client
    // ServerWebExchange exchain : Đại diện cho req/res giúp truy cập, chỉnh sửa dữ liệu Http
    // GatewayFilterChain chain: Dùng để tiếp tục xử lý request (nếu không muốn chặn lại).
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //Lấy List chứa token từ authentication filter
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);

        if(CollectionUtils.isEmpty(authHeader))
            return unauthenticated(exchange.getResponse());

        //Lấy token từ trong List authHeader
        String token = authHeader.getFirst().replace("Bearer", "");

        //Kiểm tra token
        return identityService.introspect(token).flatMap(introspectResponse -> {
            if(introspectResponse.getResult().isValid())
                return chain.filter(exchange);
            else
                return unauthenticated(exchange.getResponse());
            //Nếu như trong quá trình introspect gặp lỗi khác thì cũng trả về unauthenticated
        }).onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }

    @Override
    //Set thứ tự chạy GlobalFilter (số càng nhỏ ưu tiên càng lớn, mặc định các filter của gateway đều > 1)
    public int getOrder() {
        return -1; //Đặt mức độ ưu tiên là -1, sẽ được chạy trước
    }

    //Set response khi auth lỗi
    Mono<Void> unauthenticated(ServerHttpResponse response) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(1401)
                .message("unauthenticated")
                .build();

        String body = null;

        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //Set trạng thái code trả về
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //add content-type là application/json, dữ liệu phản hồi dưới dạng JSON
//        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith( //ghi dữ liệu vào http response body
                Mono.just( //Tạo mono để chứa dữ liệu cần ghi vào response
                        response.bufferFactory() //Tạo dataBuffer
                                .wrap(body.getBytes()))); //chuyển dữ liệu String sang byte và gói(wrap) vào dataBuffer
    }
}
