package com.java_project.file_service.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {
    public Object uploadFile(MultipartFile file) throws IOException {
        //Gọi đến folder lưu file
        Path folder = Paths.get("D:/upload");

        //Lấy phần mở rộng của file (png, pdf, ...)
        String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        //Tạo file name với UUID
        String fileName = ObjectUtils.isEmpty(fileExtension) ? UUID.randomUUID().toString() :
                UUID.randomUUID() + "." + fileExtension;

        //Tạo đường dẫn của ảnh
        Path filePath = folder.resolve(fileName) //ghép đường dẫn folder với filename để được đường dẫn đến file
                .normalize() //chuẩn hoá đường dẫn, loại bỏ các phần tử thừa như "." (thư mục hiện tại) và ".." (thư mục cha).
                .toAbsolutePath(); //Chuyển đường dẫn tương đối thành đường dẫn tuyệt đối

        //Copy file vào folder với việc chèn file nếu file đã tồn tại
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return null;
    }
}
