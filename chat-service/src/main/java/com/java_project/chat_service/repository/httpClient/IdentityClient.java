package com.java_project.chat_service.repository.httpClient;

import com.java_project.chat_service.configuration.AuthenticationRequestInterceptor;
import com.java_project.chat_service.dto.ApiResponse;
import com.java_project.chat_service.dto.request.IntrospectRequest;
import com.java_project.chat_service.dto.response.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "identity-service", url = "${app.services.identity.url}",
        configuration = AuthenticationRequestInterceptor.class)
public interface IdentityClient {
    @PostMapping("/auth/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request);
}
