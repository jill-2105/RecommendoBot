package com.recommendobot.controller;

import com.recommendobot.model.*;
import com.recommendobot.service.LaptopService;
import com.recommendobot.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LaptopController - Laptop-related endpoints
 * Handles laptop listing, details, search, filtering, and helpers
 */
@RestController
@RequestMapping("/api/laptops")
public class LaptopController {

    @Autowired
    private LaptopService laptopService;

    @Autowired
    private SearchService searchService;

    /**
     * GET /api/laptops
     * Get all laptops
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LaptopResponse>>> getAllLaptops() {
        List<LaptopResponse> laptops = laptopService.getAllLaptops();
        return ResponseEntity.ok(ApiResponse.success("Laptops retrieved successfully", laptops));
    }

    /**
     * GET /api/laptops/{id}
     * Get laptop by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LaptopResponse>> getLaptopById(@PathVariable Long id) {
        LaptopResponse laptop = laptopService.getLaptopById(id);
        return ResponseEntity.ok(ApiResponse.success("Laptop found", laptop));
    }

    /**
     * POST /api/laptops/search
     * Accepts the complex search request from frontend and returns paginated results
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchLaptops(@RequestBody LaptopService.SearchRequestDto request) {
        LaptopService.SearchResponseDto resp = laptopService.searchLaptops(request);
        return ResponseEntity.ok(resp);
    }

    /**
     * POST /api/laptops/search (legacy/simple)
     * Search laptops using the existing SearchService (keeps backward compatibility)
     */
    @PostMapping(value = "/search", consumes = "text/plain", produces = "application/json")
    public ResponseEntity<ApiResponse<List<LaptopResponse>>> searchLaptopsLegacy(@RequestBody String query) {
        List<LaptopResponse> results = searchService.searchLaptops(query);
        return ResponseEntity.ok(ApiResponse.success("Search completed", results));
    }

    /**
     * POST /api/laptops/filter
     * Filter laptops by criteria
     */
    @PostMapping("/filter")
    public ResponseEntity<ApiResponse<List<LaptopResponse>>> filterLaptops(@RequestBody FilterRequest filterRequest) {
        // For now, return all laptops (can wire to search/filter logic later)
        List<LaptopResponse> laptops = laptopService.getAllLaptops();
        return ResponseEntity.ok(ApiResponse.success("Filtered results", laptops));
    }

    /**
     * GET /api/laptops/brands
     * Get all distinct brands
     */
    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<String>>> getAllBrands() {
        List<String> brands = laptopService.getAllBrands();
        return ResponseEntity.ok(ApiResponse.success("Brands retrieved", brands));
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam(name = "q", required = false) String q) {
        List<String> suggestions = laptopService.getAutocompleteSuggestions(q);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/spellcheck")
    public ResponseEntity<?> spellcheck(@RequestParam(name = "q", required = false) String q) {
        String suggestion = laptopService.spellCheckQuery(q);
        return ResponseEntity.ok(java.util.Map.of("suggestion", suggestion));
    }
}
