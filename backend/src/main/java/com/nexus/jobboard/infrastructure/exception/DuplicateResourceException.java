package com.nexus.jobboard.infrastructure.exception;

/**
 * Duplicate resource exception following SRP
 * - Single responsibility: Handle duplicate resource scenarios
 */
public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DuplicateResourceException(String resourceType, String field, String value) {
        super(String.format("%s with %s '%s' already exists", resourceType, field, value));
    }
}
