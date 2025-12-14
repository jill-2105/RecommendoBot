package com.recommendobot.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * FilterRequest DTO - Receives filter criteria from frontend
 * Used in POST /api/laptops/filter endpoint
 */
public class FilterRequest {

    private List<String> brands;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private List<String> ramOptions;
    private List<String> storageOptions;
    private List<String> displaySizes;
    private List<String> graphicsOptions;

    public FilterRequest() {}

    // Getters and Setters
    public List<String> getBrands() {
        return brands;
    }

    public void setBrands(List<String> brands) {
        this.brands = brands;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<String> getRamOptions() {
        return ramOptions;
    }

    public void setRamOptions(List<String> ramOptions) {
        this.ramOptions = ramOptions;
    }

    public List<String> getStorageOptions() {
        return storageOptions;
    }

    public void setStorageOptions(List<String> storageOptions) {
        this.storageOptions = storageOptions;
    }

    public List<String> getDisplaySizes() {
        return displaySizes;
    }

    public void setDisplaySizes(List<String> displaySizes) {
        this.displaySizes = displaySizes;
    }

    public List<String> getGraphicsOptions() {
        return graphicsOptions;
    }

    public void setGraphicsOptions(List<String> graphicsOptions) {
        this.graphicsOptions = graphicsOptions;
    }
}
