package com.recommendobot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * SearchFrequency Entity - Tracks search term frequency
 */
@Entity
@Table(name = "search_frequency")
public class SearchFrequency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "search_term", nullable = false, unique = true, length = 255)
    private String searchTerm;

    @Column(nullable = false)
    private Integer count = 0;

    @Column(name = "last_searched")
    private LocalDateTime lastSearched;

    public SearchFrequency() {
        this.lastSearched = LocalDateTime.now();
    }

    public SearchFrequency(String searchTerm, Integer count) {
        this.searchTerm = searchTerm;
        this.count = count;
        this.lastSearched = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    
    public LocalDateTime getLastSearched() { return lastSearched; }
    public void setLastSearched(LocalDateTime lastSearched) { this.lastSearched = lastSearched; }
    
    public void incrementCount() {
        this.count++;
        this.lastSearched = LocalDateTime.now();
    }
}
