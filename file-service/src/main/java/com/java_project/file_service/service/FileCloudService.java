package com.java_project.file_service.service;

import com.java_project.file_service.dto.response.FileResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileCloudService {
    MinioClient minioClient;

    @Value("${minio.bucket.default}")
    @NonFinal
    String bucket;

    @Value("${minio.endpoint}")
    @NonFinal
    String endpoint;

    @SneakyThrows
    public FileResponse uploadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );
        } catch (Exception e) {
            System.out.println("Error saving file " + e.getMessage());
        }

        //Dùng getPresignedObjectUrl sẽ get đường link presigned URL (có chứa query)
//        return FileResponse.builder()
//                .originalFileName(fileName)
//                .url(minioClient.getPresignedObjectUrl(
//                        GetPresignedObjectUrlArgs.builder()
//                                .method(Method.GET)
//                                .bucket(bucket)
//                                .object(fileName)
//                                .build()
//                ))
//                .build();

        //Tự custom đường link trả về
        return FileResponse.builder()
                .originalFileName(fileName)
                .url(endpoint + "/" + bucket + "/" + fileName)
                .build();
    }

    @SneakyThrows
    public String deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            );

            return "Delete file success";
        } catch (Exception e) {
            System.out.println("Error delete file " + e.getMessage());
            return "Delete file fail";
        }
    }
}
