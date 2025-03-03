package com.java_project.identity_service.service;

import com.java_project.identity_service.dto.request.UserCreationRequest;
import com.java_project.identity_service.dto.request.UserUpdateRequest;
import com.java_project.identity_service.dto.response.UserResponse;
import com.java_project.identity_service.entity.User;
import com.java_project.identity_service.enums.Role;
import com.java_project.identity_service.exception.AppException;
import com.java_project.identity_service.exception.ErrorCode;
import com.java_project.identity_service.mapper.UserMapper;
import com.java_project.identity_service.repository.RoleRepository;
import com.java_project.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;

    UserMapper userMapper;
    PasswordEncoder passwordEncoder;


    public UserResponse createUser(UserCreationRequest request) {

        //Nếu username đã tồn tại
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());

        //user.setRoles(roles);

        return userMapper.userResponse(userRepository.save(user));
    }

    //Kiểm tra trước khi tới endpoint
    @PreAuthorize("hasRole('ADMIN')") //Bảo vệ endpoint chỉ có user role admin mới có thể truy cập
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    //Kiểm tra sau khi endpoint xử lý xong, nếu đúng thì trả về
    @PostAuthorize("returnObject.username= authentication.name") //Chỉ cho chính người dùng xem thông tin của chính họ
    public UserResponse getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.userResponse(user);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user =  userRepository.findByUsername(name).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.userResponse(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword())); //Mã hoá password khi cập nhật lại user

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.userResponse(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
