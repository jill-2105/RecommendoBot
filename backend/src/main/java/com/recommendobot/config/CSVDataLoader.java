package com.recommendobot.config;

import com.recommendobot.model.Laptop;
import com.recommendobot.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;

/**
 * CSVDataLoader - Imports laptop data from CSV on first startup
 */
@Configuration
public class CSVDataLoader {

    @Autowired
    private LaptopRepository laptopRepository;

    @Bean
    public CommandLineRunner loadLaptopData() {
        return args -> {
            // Check if data already loaded
            if (laptopRepository.count() > 0) {
                System.out.println("✅ Laptop data already loaded (" + laptopRepository.count() + " laptops)");
                return;
            }

            System.out.println("🔄 Loading laptop data from CSV...");

            try {
                ClassPathResource resource = new ClassPathResource("data/all_laptops_data.csv");
                
                if (!resource.exists()) {
                    System.out.println("⚠️  CSV file not found. Skipping data load.");
                    return;
                }

                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream())
                );

                String line;
                int count = 0;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    // Skip header row
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    // Parse CSV line (handle commas in quotes)
                    String[] parts = parseCsvLine(line);
                    
                    if (parts.length >= 10) {
                        Laptop laptop = new Laptop();
                        laptop.setBrandName(parts[0].trim());
                        laptop.setProduct(parts[1].trim());
                        // Clean price: remove $, CA$, C$, and commas
                        String priceStr = parts[2].trim()
                                .replace("$", "")
                                .replace("CA$", "")
                                .replace("C$", "")
                                .replace("CAD", "")
                                .replace(",", "")
                                .trim();
                        laptop.setPrice(new BigDecimal(priceStr));
                        laptop.setProcessor(parts[3].trim());
                        laptop.setMemory(parts[4].trim());
                        laptop.setStorage(parts[5].trim());
                        laptop.setGraphics(parts[6].trim());
                        laptop.setDisplay(parts[7].trim());
                        laptop.setOs(parts[8].trim());
                        laptop.setImageUrl(parts[9].trim());

                        laptopRepository.save(laptop);
                        count++;
                    }
                }

                reader.close();

                System.out.println("✅ Successfully loaded " + count + " laptops from CSV!");

            } catch (Exception e) {
                System.err.println("❌ Error loading laptop data: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    /**
     * Parse CSV line handling quoted commas
     */
    private String[] parseCsvLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
