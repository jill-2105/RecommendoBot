package com.recommendobot.service;

import com.recommendobot.model.Laptop;
import com.recommendobot.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * CsvSyncService - Keeps CSV file in sync with database
 * When admin adds/updates/deletes laptops, this updates the CSV file
 * so your algorithms continue to work with updated data
 */
@Service
public class CsvSyncService {

    @Value("${csv.data.path}")
    private String csvDataPath;

    @Autowired
    private LaptopRepository laptopRepository;

    /**
     * Export all laptops from database to CSV file
     */
    public void syncDatabaseToCsv() {
        try {
            List<Laptop> laptops = laptopRepository.findAll();

            PrintWriter writer = new PrintWriter(new FileWriter(csvDataPath));

            // Write CSV header
            writer.println("Brand Name,Product,Price,Processor,Memory,Storage,Graphics,Display,OS,Image");

            // Write each laptop
            for (Laptop laptop : laptops) {
                writer.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    escapeCsv(laptop.getBrandName()),
                    escapeCsv(laptop.getProduct()),
                    laptop.getPrice().toString(),
                    escapeCsv(laptop.getProcessor()),
                    escapeCsv(laptop.getMemory()),
                    escapeCsv(laptop.getStorage()),
                    escapeCsv(laptop.getGraphics()),
                    escapeCsv(laptop.getDisplay()),
                    escapeCsv(laptop.getOs()),
                    escapeCsv(laptop.getImageUrl())
                ));
            }

            writer.close();

            System.out.println("✅ Synced " + laptops.size() + " laptops to CSV file");

        } catch (IOException e) {
            System.err.println("❌ Error syncing to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Escape CSV special characters
     */
    private String escapeCsv(String value) {
        if (value == null) return "";
        
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        
        return value;
    }
}
