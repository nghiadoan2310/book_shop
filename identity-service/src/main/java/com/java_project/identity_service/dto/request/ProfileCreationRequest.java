package com.java_project.identity_service.dto.request;

import com.java_project.identity_service.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data //Tự động tạo getter, setter, constructor,...
@NoArgsConstructor //cho phép có thể không cần giá trị khởi tạo cho class
@AllArgsConstructor //cho phép có tất cả các giá trị đầu vào của class
@Builder //Rút gọn code khi sử dụng class
@FieldDefaults(level = AccessLevel.PRIVATE) //Các field không định nghĩa mặc định sẽ là private
public class ProfileCreationRequest {
    String userId;
    String firstName;
    String lastName;
    LocalDate dob;
    String city;
}
