package com.java_project.identity_service.exception;

import com.java_project.identity_service.dto.ApiResponse;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

//Xử lý lỗi tập trung xử dụng annotation @ControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min";

//    @ExceptionHandler(value = RuntimeException.class)
//    ResponseEntity<ApiResponse<?>> handlingRuntimeException(RuntimeException exception) {
//        ApiResponse<?> apiResponse = new ApiResponse<>();
//
//        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
//        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
//
//        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode()).body(apiResponse);
//    }

    //Xử lý các lỗi throw AppException
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingRuntimeException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    //Xảy ra khi dữ liệu đầu vào không hợp lệ trong một API sử dụng
    // validation với @Valid hoặc @Validated (VD như trong Controller)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingValidation(MethodArgumentNotValidException exception) {
        //Lấy message lỗi
        String enumKey = exception.getFieldError() != null ? exception.getFieldError().getDefaultMessage() : "";

        //Mặc định để lỗi trường hợp sai key dữ liệu đầu vào
        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        Map<String, Object> attributes = null;

        try {
            //Lấy trường hợp lỗi trong enums ErrorCode có message giống với message lỗi lấy được
            errorCode = ErrorCode.valueOf(enumKey);

            //Lấy thông tin lỗi
            //getPropertyPath() → Lấy tên trường bị lỗi.
            //getInvalidValue() → Lấy giá trị không hợp lệ.
            //getMessage() → Lấy thông báo lỗi.
            var constraintViolation = exception.getBindingResult() //Lấy thông tin về lỗi validate
                    .getAllErrors() //Lấy tất cả danh sách lỗi dươi dạng Object
                    .getFirst() //Lấy lỗi đầu tiên từ danh sách
                    .unwrap(ConstraintViolation.class);//Chuyển Object sang ConstraintViolation(là 1 object chứa thông tin lỗi)

            //Lấy thông tin các attributes đã truyền vào annotation
            attributes = constraintViolation.getConstraintDescriptor().getAttributes();

        } catch (IllegalArgumentException e) {
            System.out.println("invalid key");
        }

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        //Nếu lấy được thông tin thông tin các attributes đã truyền vào annotation thì
        // gọi hàm mapAttribute để custom lại message lỗi(ở đây là những message lỗi có {min} trong file ErrorCode)
        apiResponse.setMessage(Objects.nonNull(attributes) ?
                mapAttribute(errorCode.getMessage(), attributes) : errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    //Lỗi từ chối quyền truy cập (không có quyền truy cập)
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    //Custom message lỗi bằng dữ liệu được đưa vào annotation(VD ở đây mà min - trong file UserCreateRequest - @Size(min = 3))
    private String mapAttribute(String message, Map<String, Object> attributes) {
        //Lấy giá trị min được truyền vào annotation
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));

        //Trong chuỗi message thay {min} thành giá trị mới lấy được
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
