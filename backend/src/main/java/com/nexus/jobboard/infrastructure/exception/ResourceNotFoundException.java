package com.nexus.jobboard.infrastructure.exception;

/**
 * Resource not found exception following SRP
 * - Single responsibility: Handle resource not found scenarios
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ResourceNotFoundException(String resourceType, Long id) {
        super(String.format("%s not found with ID: %d", resourceType, id));
    }
    
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s not found with identifier: %s", resourceType, identifier));
    }
}
