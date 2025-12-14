package com.recommendobot.repository;

import com.recommendobot.model.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * LaptopRepository - Database operations for Laptop entity
 */
@Repository
public interface LaptopRepository extends JpaRepository<Laptop, Long> {

    List<Laptop> findByBrandName(String brandName);
    
    List<Laptop> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Laptop> findByProductContainingIgnoreCase(String product);
    
    @Query("SELECT DISTINCT l.brandName FROM Laptop l ORDER BY l.brandName")
    List<String> findAllDistinctBrands();
    
    @Query("SELECT DISTINCT l.memory FROM Laptop l WHERE l.memory IS NOT NULL ORDER BY l.memory")
    List<String> findAllDistinctMemoryOptions();
    
    @Query("SELECT DISTINCT l.storage FROM Laptop l WHERE l.storage IS NOT NULL ORDER BY l.storage")
    List<String> findAllDistinctStorageOptions();
    
    @Query("SELECT DISTINCT l.display FROM Laptop l WHERE l.display IS NOT NULL ORDER BY l.display")
    List<String> findAllDistinctDisplaySizes();
    
    @Query("SELECT COUNT(l) FROM Laptop l")
    Long countTotalLaptops();
}
