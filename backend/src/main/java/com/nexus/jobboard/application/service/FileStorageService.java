package com.nexus.jobboard.application.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * File storage service interface following DIP and SRP
 * - Single responsibility: Handle file storage operations
 * - Abstraction for different storage implementations (local, S3, etc.)
 */
public interface FileStorageService {
    
    /**
     * Store uploaded file and return file URL
     */
    String storeFile(MultipartFile file, String directory);
    
    /**
     * Store resume file specifically
     */
    String storeResume(MultipartFile resume, Long userId);
    
    /**
     * Delete file by URL
     */
    void deleteFile(String fileUrl);
    
    /**
     * Get file content for processing
     */
    byte[] getFileContent(String fileUrl);
    
    /**
     * Check if file exists
     */
    boolean fileExists(String fileUrl);
    
    /**
     * Get file metadata
     */
    FileMetadata getFileMetadata(String fileUrl);
    
    /**
     * File metadata class
     */
    class FileMetadata {
        private String fileName;
        private String contentType;
        private long size;
        private String url;
        
        // Constructors, getters, setters
        public FileMetadata(String fileName, String contentType, long size, String url) {
            this.fileName = fileName;
            this.contentType = contentType;
            this.size = size;
            this.url = url;
        }
        
        // Getters
        public String getFileName() { return fileName; }
        public String getContentType() { return contentType; }
        public long getSize() { return size; }
        public String getUrl() { return url; }
    }
}
