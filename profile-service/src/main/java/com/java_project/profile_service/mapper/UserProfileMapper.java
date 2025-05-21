package com.java_project.profile_service.mapper;

import com.java_project.profile_service.dto.request.ProfileCreationRequest;
import com.java_project.profile_service.dto.request.UpdateProfileRequest;
import com.java_project.profile_service.dto.response.UserProfileResponse;
import com.java_project.profile_service.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);

    UserProfileResponse toUserProfileResponse(UserProfile userProfile);

    void update(@MappingTarget UserProfile entity, UpdateProfileRequest request);
}
