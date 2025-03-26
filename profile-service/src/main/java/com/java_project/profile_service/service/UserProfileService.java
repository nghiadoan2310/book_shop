package com.java_project.profile_service.service;

import com.java_project.profile_service.dto.request.ProfileCreationRequest;
import com.java_project.profile_service.dto.response.UserProfileResponse;
import com.java_project.profile_service.entity.UserProfile;
import com.java_project.profile_service.exception.AppException;
import com.java_project.profile_service.exception.ErrorCode;
import com.java_project.profile_service.mapper.UserProfileMapper;
import com.java_project.profile_service.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        //Map dữ liệu request sang userProfile
        UserProfile userProfile = userProfileMapper.toUserProfile(request);
        //Lưu vào db
        userProfile = userProfileRepository.save(userProfile);

        //Trả về dữ liệu theo UserProfileResponse
        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserProfileResponse> getAllProfiles() {
        return userProfileRepository.findAll().stream().map(userProfileMapper::toUserProfileResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    //Get profile(dùng cho admin)
    public UserProfileResponse getByUserId(String userId) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    //Get profile của chính nguời dùng
    public UserProfileResponse getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return userProfileMapper.toUserProfileResponse(
                userProfileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }
}
