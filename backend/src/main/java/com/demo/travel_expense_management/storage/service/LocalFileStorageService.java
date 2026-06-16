package com.demo.travel_expense_management.storage.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path storageLocation;

    public LocalFileStorageService(@Value("${storage.location}") String storageLocation) {
        this.storageLocation = Path.of(storageLocation).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file, Long legalizationId, Integer version) {
        Path targetDir = this.storageLocation
            .resolve(String.valueOf(legalizationId))
            .resolve(String.valueOf(version));
        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directory: " + targetDir, e);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFileName = UUID.randomUUID().toString() + extension;

        Path targetPath = targetDir.resolve(storedFileName);
        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file: " + storedFileName, e);
        }

        return storedFileName;
    }

    @Override
    public Resource load(String storedFileName) {
        Path filePath = this.storageLocation.resolve(storedFileName).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException("File not found or not readable: " + storedFileName);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not resolve file: " + storedFileName, e);
        }
    }

    @Override
    public void delete(String storedFileName) {
        Path filePath = this.storageLocation.resolve(storedFileName).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + storedFileName, e);
        }
    }

    @Override
    public boolean exists(String storedFileName) {
        Path filePath = this.storageLocation.resolve(storedFileName).normalize();
        return Files.exists(filePath);
    }
}
