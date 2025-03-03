package com.java_project.identity_service.service;

import com.java_project.identity_service.dto.request.UserCreationRequest;
import com.java_project.identity_service.dto.response.UserResponse;
import com.java_project.identity_service.entity.User;
import com.java_project.identity_service.exception.AppException;
import com.java_project.identity_service.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    private UserCreationRequest request;
    private UserResponse userResponse;
    private User user;
    private LocalDate dob;

    @BeforeEach
        //Khởi tạo data trước khi test
    void initData() {
        dob = LocalDate.of(2000, 2, 1);

        request = UserCreationRequest.builder()
                .username("Teinae5")
                .password("nghia2002")
                .firstName("nghia")
                .lastName("doan")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id("a12b2c4d2e4f2g")
                .username("Teinae5")
                .firstName("nghia")
                .lastName("doan")
                .dob(dob)
                .build();

        user = User.builder()
                .id("a12b2c4d2e4f2g")
                .username("Teinae5")
                .firstName("nghia")
                .lastName("doan")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        //GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        //WHEN
        var response = userService.createUser(request);

        //THEN
        Assertions.assertThat(response.getId()).isEqualTo("a12b2c4d2e4f2g");
        Assertions.assertThat(response.getUsername()).isEqualTo("Teinae5");
    }

    @Test
    void createUser_userExisted_fail() {
        //GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        //WHEN

        //THEN
        var exception = assertThrows(AppException.class, () -> userService.createUser(request));

        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1002);
        Assertions.assertThat(exception.getErrorCode().getMessage()).isEqualTo("User existed");
    }

    @Test
    void getMyInfo_valid_success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        Assertions.assertThat(userResponse.getUsername()).isEqualTo("Teinae5");
        Assertions.assertThat(userResponse.getId()).isEqualTo("a12b2c4d2e4f2g");
    }
}
