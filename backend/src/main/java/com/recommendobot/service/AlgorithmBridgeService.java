package com.recommendobot.service;

import com.recommendobot.algorithms.laptoprecommendation.Features;
import com.recommendobot.algorithms.pageranking.Laptop;
import com.recommendobot.algorithms.wordcompletion.wordcompletionTries;
import com.recommendobot.algorithms.spellcheckingusingtrie.SpellCheckingMainClass;
import com.recommendobot.model.LaptopResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * AlgorithmBridgeService - Bridge between Spring Boot and your existing algorithms
 * Converts between database/DTO formats and your algorithm's expected formats
 */
@Service
public class AlgorithmBridgeService {

    @Value("${csv.data.path}")
    private String csvDataPath;

    /**
     * Search laptops using your InvertedIndex algorithm
     */
    public List<LaptopResponse> searchUsingInvertedIndex(String query) {
        try {
            // Your algorithm expects CSV file path
            File csvFile = new File(csvDataPath);
            
            if (!csvFile.exists()) {
                throw new RuntimeException("CSV file not found: " + csvDataPath);
            }

            // Call your existing Features class
            List<Laptop> results = Features.SearchProduct(query);

            // Convert your Laptop objects to LaptopResponse DTOs
            return convertAlgorithmLaptopsToResponses(results);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Get word completions using your Trie algorithm
     */
    public List<String> getWordCompletions(String prefix) {
        try {
            wordcompletionTries wc = new wordcompletionTries(csvDataPath);
            return wc.wordCompletion(prefix);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Get spell check suggestions using your Trie algorithm
     */
    public List<String> getSpellCheckSuggestions(String word) {
        try {
            SpellCheckingMainClass spellCheck = new SpellCheckingMainClass(csvDataPath);
            return spellCheck.SpellCheckingUsingTrie(word);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Convert your algorithm's Laptop objects to LaptopResponse DTOs
     */
    private List<LaptopResponse> convertAlgorithmLaptopsToResponses(List<Laptop> laptops) {
        List<LaptopResponse> responses = new ArrayList<>();

        for (Laptop laptop : laptops) {
            LaptopResponse response = new LaptopResponse();
            response.setBrandName(laptop.brandName);
            response.setProduct(laptop.product);
            response.setPrice(laptop.price);
            response.setProcessor(laptop.processor);
            response.setMemory(laptop.memory);
            response.setStorage(laptop.storage);
            response.setGraphics(laptop.graphics);
            response.setDisplay(laptop.display);
            response.setOs(laptop.os);
            response.setImageUrl(laptop.image);

            responses.add(response);
        }

        return responses;
    }
}
