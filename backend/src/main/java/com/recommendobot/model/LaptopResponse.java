package com.recommendobot.model;

/**
 * LaptopResponse DTO - Sends laptop data to frontend
 */
public class LaptopResponse {

    private Long id;
    private String brandName;
    private String product;
    private String price;
    private String processor;
    private String memory;
    private String storage;
    private String graphics;
    private String display;
    private String os;
    private String imageUrl;

    public LaptopResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    
    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
    
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    
    public String getProcessor() { return processor; }
    public void setProcessor(String processor) { this.processor = processor; }
    
    public String getMemory() { return memory; }
    public void setMemory(String memory) { this.memory = memory; }
    
    public String getStorage() { return storage; }
    public void setStorage(String storage) { this.storage = storage; }
    
    public String getGraphics() { return graphics; }
    public void setGraphics(String graphics) { this.graphics = graphics; }
    
    public String getDisplay() { return display; }
    public void setDisplay(String display) { this.display = display; }
    
    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
