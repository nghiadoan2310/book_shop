package com.java_project.identity_service.service;

import com.java_project.identity_service.constant.PredefinedRole;
import com.java_project.identity_service.dto.request.UserCreationRequest;
import com.java_project.identity_service.dto.request.UserUpdateRequest;
import com.java_project.identity_service.dto.response.UserResponse;
import com.java_project.identity_service.entity.Role;
import com.java_project.identity_service.entity.User;
import com.java_project.identity_service.exception.AppException;
import com.java_project.identity_service.exception.ErrorCode;
import com.java_project.identity_service.mapper.ProfileMapper;
import com.java_project.identity_service.mapper.UserMapper;
import com.java_project.identity_service.repository.RoleRepository;
import com.java_project.identity_service.repository.UserRepository;
import com.java_project.identity_service.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    ProfileClient profileClient;

    ProfileMapper profileMapper;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    KafkaTemplate<String, String> kafkaTemplate;

    public UserResponse createUser(UserCreationRequest request) {

        //Nếu username đã tồn tại (bỏ vì email là unique - được set trong entity)
//        if(userRepository.existsByUsername(request.getUsername())) {
//            throw new AppException(ErrorCode.USER_EXISTED);
//        }

        User user = userMapper.toUser(request);
        //Mã hoá password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        //Tạo set roles chứa role của user
        HashSet<Role> roles = new HashSet<>();
        //Kiểm tra trong DB role đã có role user chưa, nếu có thì add vào set roles
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        //Set role USER là role mặc định của các tài khoản mới tạo
        user.setRoles(roles);
        user.setEmailVerified(false);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        var profileRequest = profileMapper.toProfileCreateRequest(request);
        profileRequest.setUserId(user.getId());
        //Tạo profile bằng cách gọi đến profile service
        var profile = profileClient.createProfile(profileRequest);

        // Publish message to kafka
        kafkaTemplate.send("onboard-successful", "Welcome our new member " + user.getUsername());

        var userCreationReponse = userMapper.userResponse(user);
        userCreationReponse.setId(profile.getResult().getId());

        return userCreationReponse;
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
