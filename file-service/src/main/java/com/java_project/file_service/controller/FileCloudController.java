package com.java_project.file_service.controller;

import com.java_project.file_service.dto.ApiResponse;
import com.java_project.file_service.dto.response.FileResponse;
import com.java_project.file_service.service.FileCloudService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/cloud")
public class FileCloudController {
    FileCloudService fileCloudService;

    @PostMapping("/media/upload")
    ApiResponse<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<FileResponse>builder()
                .result(fileCloudService.uploadFile(file))
                .build();
    }

    @DeleteMapping("/media/delete")
    ApiResponse<String> deleteFile(@RequestBody String fileName) {
        return ApiResponse.<String>builder()
                .result(fileCloudService.deleteFile(fileName))
                .build();
    }
}
