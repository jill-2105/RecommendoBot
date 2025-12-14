package com.recommendobot.service;

import com.recommendobot.model.LaptopResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SearchService - Handles search operations using your algorithms
 */
@Service
public class SearchService {

    @Autowired
    private AlgorithmBridgeService algorithmBridgeService;

    @Autowired
    private FrequencyService frequencyService;

    /**
     * Search laptops using your InvertedIndex algorithm
     */
    public List<LaptopResponse> searchLaptops(String query) {
        // Increment search frequency
        frequencyService.incrementSearchCount(query);

        // Call your algorithm via bridge service
        return algorithmBridgeService.searchUsingInvertedIndex(query);
    }

    /**
     * Get autocomplete suggestions using your Trie algorithm
     */
    public List<String> getAutocompleteSuggestions(String prefix) {
        return algorithmBridgeService.getWordCompletions(prefix);
    }

    /**
     * Get spell check suggestions using your Trie algorithm
     */
    public List<String> getSpellCheckSuggestions(String word) {
        return algorithmBridgeService.getSpellCheckSuggestions(word);
    }

    /**
     * Get top 5 most searched terms
     */
    public List<String> getTopSearchedTerms() {
        return frequencyService.getTopSearchedWords(5);
    }
}
