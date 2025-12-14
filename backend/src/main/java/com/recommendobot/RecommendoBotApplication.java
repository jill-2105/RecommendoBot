package com.recommendobot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RecommendoBot Spring Boot Application
 * Main entry point for the laptop recommendation system
 */
@SpringBootApplication
public class RecommendoBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommendoBotApplication.class, args);
        System.out.println("========================================");
        System.out.println("🚀 RecommendoBot Server Started!");
        System.out.println("📍 API Base URL: http://localhost:8080/api");
        System.out.println("🔐 Authentication: JWT Token Required");
        System.out.println("📊 Database: MySQL (laptops_data)");
        System.out.println("========================================");
    }
}
