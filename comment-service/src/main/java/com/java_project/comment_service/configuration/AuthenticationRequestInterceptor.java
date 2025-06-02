package com.java_project.comment_service.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
//Được sử dụng khi request tới các service khác
public class AuthenticationRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //RequestContextHolder.getRequestAttributes() lấy thông tin request hiện tại đang được xử lý bởi spring
        //Trả về RequestAttributes sau đó ép kiểu về ServletRequestAttributes để lấy Authorization từ header
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String authHeader = null;
        //Nếu lấy được thông tin của request hiện tại trong spring (là các request từ client)
        if(servletRequestAttributes != null) {
            //Lấy ra thông tin Authorization của Header
            authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");
        }

        //Nếu lấy được Authorization của Header của request hiện tại
        if(StringUtils.hasText(authHeader))
            //Gắn authorization vào header của request feign (là các request giữa các service)
            requestTemplate.header("Authorization", authHeader);
    }
}
