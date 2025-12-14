-- ============================================
-- RecommendoBot Database Setup Script
-- Creates database and all required tables
-- ============================================

-- Drop database if exists (for clean setup)
DROP DATABASE IF EXISTS laptops_data;

-- Create database
CREATE DATABASE laptops_data CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the database
USE laptops_data;

-- ============================================
-- TABLE 1: users
-- Stores user authentication data
-- ============================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE 2: laptops
-- Stores laptop product data
-- ============================================
CREATE TABLE laptops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_name VARCHAR(100) NOT NULL,
    product VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    processor VARCHAR(255),
    memory VARCHAR(100),
    storage VARCHAR(100),
    graphics VARCHAR(255),
    display VARCHAR(100),
    os VARCHAR(100),
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_brand_name (brand_name),
    INDEX idx_price (price),
    INDEX idx_product (product),
    FULLTEXT idx_product_fulltext (product, processor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE 3: search_frequency
-- Tracks search term frequency for analytics
-- ============================================
CREATE TABLE search_frequency (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    search_term VARCHAR(255) NOT NULL UNIQUE,
    count INT NOT NULL DEFAULT 0,
    last_searched TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_search_term (search_term),
    INDEX idx_count (count DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- VERIFICATION
-- ============================================
SHOW TABLES;

SELECT 'Database setup completed successfully!' AS Status;
