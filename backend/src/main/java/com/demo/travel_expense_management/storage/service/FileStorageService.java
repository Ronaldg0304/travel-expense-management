package com.demo.travel_expense_management.storage.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String store(MultipartFile file, Long legalizationId, Integer version);

    Resource load(String storedFileName);

    void delete(String storedFileName);

    boolean exists(String storedFileName);
}
