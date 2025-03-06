package com.java_project.profile_service.mapper;

import com.java_project.profile_service.dto.request.ProfileCreationRequest;
import com.java_project.profile_service.dto.response.UserProfileResponse;
import com.java_project.profile_service.entity.UserProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);

    UserProfileResponse toUserProfileResponse(UserProfile userProfile);
}
