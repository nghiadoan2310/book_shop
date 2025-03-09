package com.java_project.identity_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

@JsonInclude(JsonInclude.Include.NON_NULL) //Các trường có giá trị null sẽ bị loại bỏ khi trả về
//Class dùng để config kiểu dữ liệu trả về client khi server xử lý thành công
public class ApiResponse<T> {
    @Builder.Default
    int code = 1000;
    String message;
    T result;
}
