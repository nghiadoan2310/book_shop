package com.java_project.file_service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileInfo {
    String name; // Tên file
    String contentType; //chứa thông tin của file(VD: image/png)
    long size; //Kích thước
    String md5Checksum; //là 1 chuỗi hash (dùng để kiểm tra tính toàn vẹn của dữ liệu)
    String path; // Đường dẫn tuyệt đối của file
    String url; //Lưu url dùng để get file vừa load
}
