package com.java_project.identity_service.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({FIELD}) //Chọn nơi annotation được apply
@Retention(RUNTIME) //Annotation được xử lý lúc nào
@Constraint(validatedBy = { DobValidator.class })
public @interface DobConstraint {
    //3 props cơ bản khi tạo annotation validate
    String message() default "Invalid date of birth"; //Thông báo lỗi nếu validate thất bại

    int min();

    //Dùng khi muốn chia validation thành các nhóm khác nhau
    Class<?>[] groups() default { };

    //Đính kèm metadata (thêm thông tin vào validation), thường dùng cho mục đích logging hoặc phân loại lỗi.
    Class<? extends Payload>[] payload() default { };

}
