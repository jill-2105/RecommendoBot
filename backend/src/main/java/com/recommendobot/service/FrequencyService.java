package com.recommendobot.service;

import com.recommendobot.algorithms.searchfrequency.SearchFreq;
import com.recommendobot.model.SearchFrequency;
import com.recommendobot.repository.SearchFrequencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.*;

/**
 * FrequencyService - Manages search frequency using your AVL tree algorithm
 * Keeps in-memory AVL tree synced with database
 */
@Service
public class FrequencyService {

    @Autowired
    private SearchFrequencyRepository searchFrequencyRepository;

    // Your AVL tree implementation
    private SearchFreq searchFreq;

    @PostConstruct
    public void init() {
        // Initialize your AVL tree
        searchFreq = new SearchFreq();

        // Load existing data from database into AVL tree
        List<SearchFrequency> existingData = searchFrequencyRepository.findAll();
        
        for (SearchFrequency sf : existingData) {
            searchFreq.addSearchedWord(sf.getSearchTerm(), sf.getCount());
        }

        System.out.println("✅ Loaded " + existingData.size() + " search frequency records into AVL tree");
    }

    /**
     * Increment search count for a term
     */
    public void incrementSearchCount(String term) {
        // Update AVL tree (in-memory)
        searchFreq.addSearchedWordCount(term);
    }

    /**
     * Get top N most searched words
     */
    public List<String> getTopSearchedWords(int limit) {
        return searchFreq.getTopSearchedWords(limit);
    }

    /**
     * Periodic sync: Flush AVL tree to database every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void syncToDatabase() {
        flushToDatabase();
    }

    /**
     * On shutdown: Flush AVL tree to database
     */
    @PreDestroy
    public void onShutdown() {
        System.out.println("🔄 Flushing search frequency data to database...");
        flushToDatabase();
        System.out.println("✅ Search frequency data saved!");
    }

    /**
     * Flush AVL tree data to database
     */
    private void flushToDatabase() {
        try {
            Map<String, Integer> allCounts = searchFreq.getAllCounts();

            for (Map.Entry<String, Integer> entry : allCounts.entrySet()) {
                String term = entry.getKey();
                Integer count = entry.getValue();

                Optional<SearchFrequency> existing = searchFrequencyRepository.findBySearchTerm(term);

                if (existing.isPresent()) {
                    SearchFrequency sf = existing.get();
                    sf.setCount(count);
                    sf.setLastSearched(java.time.LocalDateTime.now());
                    searchFrequencyRepository.save(sf);
                } else {
                    SearchFrequency sf = new SearchFrequency(term, count);
                    searchFrequencyRepository.save(sf);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error flushing frequency data: " + e.getMessage());
        }
    }
}
