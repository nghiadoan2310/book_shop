package com.java_project.file_service.service;

import com.java_project.file_service.dto.response.FileData;
import com.java_project.file_service.dto.response.FileResponse;
import com.java_project.file_service.exception.AppException;
import com.java_project.file_service.exception.ErrorCode;
import com.java_project.file_service.mapper.FileMgmtMapper;
import com.java_project.file_service.repository.FileMgmtRepository;
import com.java_project.file_service.repository.FileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {
    FileRepository fileRepository;
    FileMgmtRepository fileMgmtRepository;

    FileMgmtMapper fileMgmtMapper;

    public FileResponse uploadFile(MultipartFile file) throws IOException {
        var fileInfo = fileRepository.store(file);

        var fileMgmt = fileMgmtMapper.toFileMgmt(fileInfo);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        fileMgmt.setOwnerId(authentication.getName());

        fileMgmtRepository.save(fileMgmt);

        return FileResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .url(fileInfo.getUrl())
                .build();
    }

    public FileData downloadFile(String fileName) throws IOException {
        var fileMgmt = fileMgmtRepository.findById(fileName)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));

        return new FileData(fileMgmt.getContentType(), fileRepository.read(fileMgmt));
    }
}
