package com.java_project.identity_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.java_project.identity_service.dto.request.UserCreationRequest;
import com.java_project.identity_service.dto.response.UserResponse;
import com.java_project.identity_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
//Test với thông tin được cung cấp trong file test.properties - chạy test với h2 db(không cần start mysql)
@TestPropertySource("/test.properties")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Sử dụng để mock 1 layer khác
    private UserService userService;

    private UserCreationRequest request;
    private UserResponse userResponse;
    private LocalDate dob;

    @BeforeEach //Khởi tạo data trước khi test
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
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        //GIVEN

        //Chuyển request sang dạng JSON do dùng mockMVC để tạo request có đầu vào content dạng JSON
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        //Mock userService
        Mockito.when(userService.createUser(ArgumentMatchers.any()))
                        .thenReturn(userResponse);

        //WHEN
        //Tạo request
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk()) //kết quả đúng khi trả về: status code 200
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000) //code đúng là 1000
        );

        //THEN
    }

    @Test
    void createUser_usernameInvalid_fail() throws Exception {
        request.setUsername("Tei");

        //GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1003))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username must be at least 4 characters"));
    }
}
