package com.recommendobot.repository;

import com.recommendobot.model.SearchFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SearchFrequencyRepository - Database operations for SearchFrequency entity
 */
@Repository
public interface SearchFrequencyRepository extends JpaRepository<SearchFrequency, Long> {

    Optional<SearchFrequency> findBySearchTerm(String searchTerm);
    
    @Query("SELECT sf FROM SearchFrequency sf ORDER BY sf.count DESC")
    List<SearchFrequency> findTopSearchedTerms();
    
    Boolean existsBySearchTerm(String searchTerm);
}
