package com.java_project.profile_service.service;

import com.java_project.profile_service.dto.request.ProfileCreationRequest;
import com.java_project.profile_service.dto.response.UserProfileResponse;
import com.java_project.profile_service.entity.UserProfile;
import com.java_project.profile_service.mapper.UserProfileMapper;
import com.java_project.profile_service.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;

    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        System.out.println(request);
        UserProfile userProfile = userProfileMapper.toUserProfile(request);
        System.out.println(userProfile.getLastName());
        userProfile = userProfileRepository.save(userProfile);

        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public List<UserProfileResponse> getAllProfiles() {
        return userProfileRepository.findAll().stream().map(userProfileMapper::toUserProfileResponse).toList();
    }

    public UserProfileResponse getProfile(String id) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return userProfileMapper.toUserProfileResponse(userProfile);
    }
}
