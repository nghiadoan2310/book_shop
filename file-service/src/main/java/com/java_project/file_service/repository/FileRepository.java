package com.java_project.file_service.repository;

import com.java_project.file_service.dto.FileInfo;
import com.java_project.file_service.entity.FileMgmt;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileRepository {

    @Value("${app.services.file.storage-dir}")
    String storageDir;

    @Value("${app.services.file.download-prefix}")
    String urlPrefix;

    public FileInfo store(MultipartFile file) throws IOException {
        //Gọi đến folder lưu file
        Path folder = Paths.get(storageDir);

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

        return FileInfo.builder()
                .name(fileName)
                .size(file.getSize())
                .contentType(file.getContentType()) //trả về loại file và phần mở rộng (VD: image/png)
                .md5Checksum(DigestUtils.md5DigestAsHex(file.getInputStream()))
                .path(filePath.toString())
                .url(urlPrefix + fileName)
                .build();
    }

    public Resource read(FileMgmt fileMgmt) throws IOException {
        var data = Files.readAllBytes(Path.of(fileMgmt.getPath()));
        return new ByteArrayResource(data);
    }
}
