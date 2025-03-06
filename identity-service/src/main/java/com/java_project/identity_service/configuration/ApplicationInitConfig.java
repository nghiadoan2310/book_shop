package com.java_project.identity_service.configuration;

import com.java_project.identity_service.constant.PredefinedRole;
import com.java_project.identity_service.entity.Role;
import com.java_project.identity_service.entity.User;
//import com.java_project.identity_service.enums.Role;
import com.java_project.identity_service.repository.RoleRepository;
import com.java_project.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
// Cấu hình khi khởi tạo app
public class ApplicationInitConfig {
    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver"
    ) //Thiết lập điều kiện để chạy hàm (chỉ chạy khi kết nối với mysql không chạy khi đang test - test đang chạy h2 db)
    //Khi khởi tạo app
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            //Kiểm tra trong DB có username = admin, nếu chưa có tạo acc có username = admin
            if(userRepository.findByUsername(PredefinedRole.ADMIN_ROLE).isEmpty()) {
                roleRepository.save(Role.builder()
                        .name("USER")
                        .description("User role")
                        .build());

                Role adminRole = roleRepository.save(Role.builder()
                        .name("ADMIN")
                        .description("Admin role")
                        .build());

                var roles = new HashSet<Role>();

                roles.add(adminRole);

                User user = User.builder()
                        .username(PredefinedRole.ADMIN_ROLE)
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            }
        };
    }
}
