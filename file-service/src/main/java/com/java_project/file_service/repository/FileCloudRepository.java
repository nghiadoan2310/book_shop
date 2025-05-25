package com.java_project.file_service.repository;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileCloudRepository {

    @Value("${minio.bucket.default}")
    @NonFinal
    String bucket;

    MinioClient minioClient;

    @PostConstruct
    private void init() {
        createBucket(bucket);
    }

    @SneakyThrows
    private void createBucket(String name) {
        //Kiểm tra bucket đã tồn tại chưa
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());

        //Nếu chưa tồn tại
        if (!found) {
            // Tạo bucket với tên cho ở đầu vào.
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(name).build());

            // Thiết lập bucket là public bằng cách set policy
            final var policy = """
                        {
                          "Version": "2012-10-17",
                          "Statement": [
                           {
                              "Effect": "Allow",
                              "Principal": "*",
                              "Action": "s3:GetObject",
                              "Resource": "arn:aws:s3:::%s/*"
                            }
                          ]
                        }
                    """.formatted(name);
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder().bucket(name).config(policy).build()
            );
        } else {
            System.out.println("Bucket" + name + "already exists.");
        }
    }

}
