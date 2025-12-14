package com.recommendobot.controller;

import com.recommendobot.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/search-frequency")
    public ResponseEntity<?> getSearchFrequency() {
        Map<String, Integer> map = analyticsService.getSearchFrequency();
        return ResponseEntity.ok(map);
    }

    @PostMapping("/search-frequency")
    public ResponseEntity<?> increaseSearchFrequency(@RequestBody Map<String, String> body) {
        String query = body.get("query");
        analyticsService.increaseSearchFrequencyCount(query);
        return ResponseEntity.ok(java.util.Map.of("ok", true));
    }

    @GetMapping("/word-frequency")
    public ResponseEntity<?> getWordFrequency(@RequestParam(name = "q", required = false) String q) {
        Map<String, Integer> map = analyticsService.getWordFrequency(q);
        return ResponseEntity.ok(map);
    }
}
