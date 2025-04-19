package com.java_project.file_service.repository;

import com.java_project.file_service.entity.FileMgmt;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileMgmtRepository extends MongoRepository<FileMgmt, String> {

}
