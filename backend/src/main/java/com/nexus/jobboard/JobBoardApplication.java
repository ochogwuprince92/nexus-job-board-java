package com.nexus.jobboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class following SOLID principles
 * - Single entry point for the application
 * - Enables necessary Spring features
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class JobBoardApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(JobBoardApplication.class, args);
    }
}
