package com.recommendobot.controller;

import com.recommendobot.model.*;
import com.recommendobot.service.LaptopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * AdminController - Admin-only endpoints
 * Requires ROLE_ADMIN to access
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private LaptopService laptopService;

    /**
     * POST /api/admin/laptops
     * Add new laptop
     */
    @PostMapping("/laptops")
    public ResponseEntity<ApiResponse<LaptopResponse>> createLaptop(@RequestBody Laptop laptop) {
        LaptopResponse created = laptopService.createLaptop(laptop);
        return ResponseEntity.ok(ApiResponse.success("Laptop created successfully", created));
    }

    /**
     * PUT /api/admin/laptops/{id}
     * Update laptop
     */
    @PutMapping("/laptops/{id}")
    public ResponseEntity<ApiResponse<LaptopResponse>> updateLaptop(@PathVariable Long id, @RequestBody Laptop laptop) {
        LaptopResponse updated = laptopService.updateLaptop(id, laptop);
        return ResponseEntity.ok(ApiResponse.success("Laptop updated successfully", updated));
    }

    /**
     * DELETE /api/admin/laptops/{id}
     * Delete laptop
     */
    @DeleteMapping("/laptops/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLaptop(@PathVariable Long id) {
        laptopService.deleteLaptop(id);
        return ResponseEntity.ok(ApiResponse.success("Laptop deleted successfully", null));
    }
}
