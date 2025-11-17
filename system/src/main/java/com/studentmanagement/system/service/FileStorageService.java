package com.studentmanagement.system.service;

import com.studentmanagement.system.util.AppLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads/students}")
    private String uploadDir;

    /**
     * Store uploaded file and return the file path
     */
    public String storeFile(MultipartFile file, String studentId) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cannot upload empty file"
                );
            }

            // Validate file type (only images)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Only image files are allowed"
                );
            }

            // Validate file size (max 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "File size exceeds maximum limit of 5MB"
                );
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String filename = studentId + "_" + UUID.randomUUID().toString() + fileExtension;
            Path targetLocation = uploadPath.resolve(filename);

            // Copy file to target location
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            AppLogger.info("File uploaded successfully: " + filename);
            return filename;

        } catch (IOException ex) {
            AppLogger.error("Failed to store file: " + ex.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to store file: " + ex.getMessage()
            );
        }
    }

    /**
     * Delete a file
     */
    public void deleteFile(String filename) {
        try {
            if (filename == null || filename.isEmpty()) {
                return;
            }

            Path filePath = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(filePath);

            AppLogger.info("File deleted successfully: " + filename);
        } catch (IOException ex) {
            AppLogger.error("Failed to delete file: " + ex.getMessage());
            // Don't throw exception, just log the error
        }
    }

    /**
     * Get file path
     */
    public Path getFilePath(String filename) {
        return Paths.get(uploadDir).resolve(filename);
    }
}