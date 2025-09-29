package com.nexus.jobboard.application.service.impl;

import com.nexus.jobboard.application.service.FileStorageService;
import com.nexus.jobboard.infrastructure.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * File storage service implementation following SOLID principles
 * 
 * SRP: Handles only file storage operations
 * OCP: Can be extended for different storage backends (S3, Azure, etc.)
 * LSP: Substitutable for FileStorageService interface
 * ISP: Implements only file storage methods
 * DIP: Depends on abstractions through configuration
 */
@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    
    private final Path fileStorageLocation;
    private final String baseUrl;
    
    public FileStorageServiceImpl(@Value("${file.upload-dir:./uploads}") String uploadDir,
                                 @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.baseUrl = baseUrl;
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    
    @Override
    public String storeFile(MultipartFile file, String directory) {
        validateFile(file);
        
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        
        try {
            Path directoryPath = this.fileStorageLocation.resolve(directory);
            Files.createDirectories(directoryPath);
            
            Path targetLocation = directoryPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            String fileUrl = baseUrl + "/api/v1/files/" + directory + "/" + fileName;
            log.info("File stored successfully: {}", fileUrl);
            
            return fileUrl;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
    
    @Override
    public String storeResume(MultipartFile resume, Long userId) {
        log.info("Storing resume for user: {}", userId);
        
        validateResumeFile(resume);
        
        String directory = "resumes/" + userId;
        return storeFile(resume, directory);
    }
    
    @Override
    public void deleteFile(String fileUrl) {
        try {
            String relativePath = extractRelativePathFromUrl(fileUrl);
            Path filePath = this.fileStorageLocation.resolve(relativePath);
            
            Files.deleteIfExists(filePath);
            log.info("File deleted successfully: {}", fileUrl);
        } catch (IOException ex) {
            log.error("Could not delete file: {}", fileUrl, ex);
            throw new FileStorageException("Could not delete file: " + fileUrl, ex);
        }
    }
    
    @Override
    public byte[] getFileContent(String fileUrl) {
        try {
            String relativePath = extractRelativePathFromUrl(fileUrl);
            Path filePath = this.fileStorageLocation.resolve(relativePath);
            
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not read file: " + fileUrl, ex);
        }
    }
    
    @Override
    public boolean fileExists(String fileUrl) {
        try {
            String relativePath = extractRelativePathFromUrl(fileUrl);
            Path filePath = this.fileStorageLocation.resolve(relativePath);
            
            return Files.exists(filePath);
        } catch (Exception ex) {
            log.error("Error checking file existence: {}", fileUrl, ex);
            return false;
        }
    }
    
    @Override
    public FileMetadata getFileMetadata(String fileUrl) {
        try {
            String relativePath = extractRelativePathFromUrl(fileUrl);
            Path filePath = this.fileStorageLocation.resolve(relativePath);
            
            if (!Files.exists(filePath)) {
                throw new FileStorageException("File not found: " + fileUrl);
            }
            
            String fileName = filePath.getFileName().toString();
            String contentType = Files.probeContentType(filePath);
            long size = Files.size(filePath);
            
            return new FileMetadata(fileName, contentType, size, fileUrl);
        } catch (IOException ex) {
            throw new FileStorageException("Could not get file metadata: " + fileUrl, ex);
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("Cannot store empty file");
        }
        
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (fileName.contains("..")) {
            throw new FileStorageException("Filename contains invalid path sequence: " + fileName);
        }
        
        // Check file size (10MB limit)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new FileStorageException("File size exceeds maximum limit of 10MB");
        }
    }
    
    private void validateResumeFile(MultipartFile resume) {
        validateFile(resume);
        
        String contentType = resume.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") && 
                                   !contentType.equals("application/msword") &&
                                   !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new FileStorageException("Only PDF and Word documents are allowed for resumes");
        }
    }
    
    private String generateUniqueFileName(String originalFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFileName);
        
        return timestamp + "_" + uuid + extension;
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
    
    private String extractRelativePathFromUrl(String fileUrl) {
        // Extract relative path from URL
        // Example: http://localhost:8080/api/v1/files/resumes/1/file.pdf -> resumes/1/file.pdf
        String prefix = baseUrl + "/api/v1/files/";
        if (fileUrl.startsWith(prefix)) {
            return fileUrl.substring(prefix.length());
        }
        throw new FileStorageException("Invalid file URL format: " + fileUrl);
    }
}
