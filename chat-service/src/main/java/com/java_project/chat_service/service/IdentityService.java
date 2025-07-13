package com.java_project.chat_service.service;

import com.java_project.chat_service.dto.request.IntrospectRequest;
import com.java_project.chat_service.dto.response.IntrospectResponse;
import com.java_project.chat_service.repository.httpClient.IdentityClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;

    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            return identityClient.introspect(request).getResult();
        } catch (FeignException e) {
            return IntrospectResponse.builder()
                    .valid(false)
                    .build();
        }
    }
}
