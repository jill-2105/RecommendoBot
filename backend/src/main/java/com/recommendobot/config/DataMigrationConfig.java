package com.recommendobot.config;

import com.recommendobot.model.SearchFrequency;
import com.recommendobot.repository.SearchFrequencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * DataMigrationConfig - Migrates search_freq.txt to database on first startup
 * Runs once when application starts
 */
@Configuration
public class DataMigrationConfig {

    @Autowired
    private SearchFrequencyRepository searchFrequencyRepository;

    @Bean
    public CommandLineRunner migrateSearchFrequencyData() {
        return args -> {
            // Check if data already migrated
            if (searchFrequencyRepository.count() > 0) {
                System.out.println("✅ Search frequency data already migrated.");
                return;
            }

            System.out.println("🔄 Migrating search_freq.txt to database...");

            try {
                ClassPathResource resource = new ClassPathResource("data/search_freq.txt");
                
                if (!resource.exists()) {
                    System.out.println("⚠️  search_freq.txt not found. Skipping migration.");
                    return;
                }

                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream())
                );

                String line;
                int count = 0;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    
                    if (line.isEmpty()) continue;

                    // Expected format: "searchTerm=count"
                    String[] parts = line.split("=");
                    
                    if (parts.length == 2) {
                        String term = parts[0].trim();
                        Integer frequency = Integer.parseInt(parts[1].trim());

                        SearchFrequency sf = new SearchFrequency(term, frequency);
                        searchFrequencyRepository.save(sf);
                        count++;
                    }
                }

                reader.close();

                System.out.println("✅ Successfully migrated " + count + " search frequency records!");

            } catch (Exception e) {
                System.err.println("❌ Error migrating search frequency data: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
