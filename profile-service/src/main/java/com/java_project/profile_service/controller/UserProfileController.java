package com.java_project.profile_service.controller;

import com.java_project.profile_service.dto.request.ProfileCreationRequest;
import com.java_project.profile_service.dto.response.UserProfileResponse;
import com.java_project.profile_service.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {
    UserProfileService userProfileService;

    @GetMapping
    List<UserProfileResponse> getAllProfiles() {
        return userProfileService.getAllProfiles();
    }

    @GetMapping("/{profileId}")
    UserProfileResponse getProfile(@PathVariable("profileId") String profileId) {
        return userProfileService.getProfile(profileId);
    }
}
