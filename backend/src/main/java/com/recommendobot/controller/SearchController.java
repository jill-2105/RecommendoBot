package com.recommendobot.controller;

import com.recommendobot.model.ApiResponse;
import com.recommendobot.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SearchController - Search-related endpoints
 * Handles autocomplete, spell-check, and search analytics
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * GET /api/search/autocomplete?prefix=gam
     * Get autocomplete suggestions
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<ApiResponse<List<String>>> getAutocompleteSuggestions(@RequestParam String prefix) {
        List<String> suggestions = searchService.getAutocompleteSuggestions(prefix);
        return ResponseEntity.ok(ApiResponse.success("Autocomplete suggestions", suggestions));
    }

    /**
     * GET /api/search/spellcheck?word=aple
     * Get spell-check suggestions
     */
    @GetMapping("/spellcheck")
    public ResponseEntity<ApiResponse<List<String>>> getSpellCheckSuggestions(@RequestParam String word) {
        List<String> suggestions = searchService.getSpellCheckSuggestions(word);
        return ResponseEntity.ok(ApiResponse.success("Spell-check suggestions", suggestions));
    }

    /**
     * GET /api/search/top-searches
     * Get top 5 most searched terms
     */
    @GetMapping("/top-searches")
    public ResponseEntity<ApiResponse<List<String>>> getTopSearches() {
        List<String> topSearches = searchService.getTopSearchedTerms();
        return ResponseEntity.ok(ApiResponse.success("Top searches retrieved", topSearches));
    }
}
