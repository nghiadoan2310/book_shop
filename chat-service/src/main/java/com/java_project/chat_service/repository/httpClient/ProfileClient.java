package com.java_project.chat_service.repository.httpClient;

import com.java_project.chat_service.configuration.AuthenticationRequestInterceptor;
import com.java_project.chat_service.dto.ApiResponse;
import com.java_project.chat_service.dto.response.ProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "profile-service", url = "${app.services.profile.url}",
        configuration = AuthenticationRequestInterceptor.class)
public interface ProfileClient {
    @GetMapping("/users/my-profile")
    ApiResponse<ProfileResponse> getMyProfile();

    @GetMapping("/internal/users/{userId}")
    ApiResponse<ProfileResponse> getProfile(@PathVariable("userId") String userId);

    @PostMapping("/users")
    ApiResponse<List<ProfileResponse>> getProfiles(@RequestBody List<String> userIds);
}
