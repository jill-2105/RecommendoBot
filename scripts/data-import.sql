-- ============================================
-- RecommendoBot Laptop Data Import Script
-- Imports laptop data from CSV file
-- ============================================

USE laptops_data;

-- Temporarily disable constraints for faster import
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;
SET AUTOCOMMIT = 0;

-- Clear existing laptop data (if any)
TRUNCATE TABLE laptops;

-- ============================================
-- IMPORT FROM CSV FILE
-- Replace the file path with your actual CSV location
-- ============================================

-- For Windows (adjust path to your actual location):
LOAD DATA LOCAL INFILE 'C:/Users/patel/OneDrive - SARDAR VALLABHBHAI PATEL INSTITUTE OF TECHNOLOGY, SVIT/Desktop/Projects/RecommendoBot/backend/src/main/resources/data/all_laptops_data.csv'
INTO TABLE laptops
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(brand_name, product, price, processor, memory, storage, graphics, display, os, image_url);

-- Re-enable constraints
COMMIT;
SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;
SET AUTOCOMMIT = 1;

-- ============================================
-- VERIFICATION
-- ============================================
SELECT COUNT(*) AS total_laptops FROM laptops;

SELECT 
    brand_name, 
    COUNT(*) AS count 
FROM laptops 
GROUP BY brand_name 
ORDER BY count DESC 
LIMIT 10;

SELECT 'Laptop data imported successfully!' AS Status;
