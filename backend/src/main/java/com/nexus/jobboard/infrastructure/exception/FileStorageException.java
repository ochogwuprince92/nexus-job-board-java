package com.nexus.jobboard.infrastructure.exception;

/**
 * File storage exception following SRP
 * - Single responsibility: Handle file storage related errors
 */
public class FileStorageException extends RuntimeException {
    
    public FileStorageException(String message) {
        super(message);
    }
    
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
