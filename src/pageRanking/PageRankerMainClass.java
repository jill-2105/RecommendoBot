package pageRanking;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import InvertedIndex.InvertedIndexCSV;

public class PageRankerMainClass {

    // Default for testing locally
    private static String DATA_FILE = "all_laptops_data.csv";

    public static List<Laptop> loadAllLaptops(String csvFilePath) throws IOException {
        List<Laptop> laptops = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);
                if (values.length >= 10) {
                    Laptop laptop = new Laptop(
                            values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(),
                            values[4].trim(), values[5].trim(), values[6].trim(), values[7].trim(),
                            values[8].trim(), values[9].trim()
                    );
                    laptops.add(laptop);
                }
            }
        }
        return laptops;
    }

    // UPDATED: Now accepts csvFilePath argument
    public static List<Laptop> pageRanking(String keyword, Set<Integer> rowsToRank, String csvFilePath) throws IOException {
        List<Laptop> rankedLaptops = new ArrayList<>();
        
        // Use the passed path, not the hardcoded one
        BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
        String line;
        int lineNum = 1;

        // Skip the header
        br.readLine(); // header
        lineNum++;

        while ((line = br.readLine()) != null) {
            if (rowsToRank.contains(lineNum)) {
                int freq = countKeywordFrequency(line, keyword);
                if (freq > 0) {
                    String[] values = line.split(",", -1); // avoid ArrayIndexOutOfBounds

                    if (values.length >= 10) {
                        Laptop laptop = new Laptop(
                            values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(),
                            values[4].trim(), values[5].trim(), values[6].trim(), values[7].trim(),
                            values[8].trim(), values[9].trim()
                        );
                        rankedLaptops.add(laptop);
                    }
                }
            }
            lineNum++;
        }
        br.close();

        // Optionally sort by frequency if needed later
        return rankedLaptops;
    }
    
    // Legacy overload method to keep old code working if needed, but not recommended for production
    public static List<Laptop> pageRanking(String keyword, Set<Integer> rowsToRank) throws IOException {
        return pageRanking(keyword, rowsToRank, DATA_FILE);
    }

    // Utility method to count keyword frequency in a line
    private static int countKeywordFrequency(String line, String keyword) {
        int count = 0;
        String lowerLine = line.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();

        int index = 0;
        while ((index = lowerLine.indexOf(lowerKeyword, index)) != -1) {
            count++;
            index += lowerKeyword.length();
        }
        return count;
    }

    // Helper for main method testing
    private static String getFilePathForMain(String filename) throws IOException {
        InputStream is = PageRankerMainClass.class.getClassLoader().getResourceAsStream(filename);
        if (is != null) {
            File tempFile = File.createTempFile("temp_pagerank_data", ".csv");
            tempFile.deleteOnExit();
            Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return tempFile.getAbsolutePath();
        }
        return filename;
    }

    // Optional main method for testing
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter keyword to rank pages: ");
        String keyword = scanner.nextLine();

        try {
            // Extract file for local testing
            String tempPath = getFilePathForMain("all_laptops_data.csv");
            DATA_FILE = tempPath; // Update static variable for this run

            Set<Integer> rowsToRank = InvertedIndexCSV.InvertedIndexing(keyword, DATA_FILE);
            if (rowsToRank.isEmpty()) {
                System.out.println("No matching rows found for keyword: " + keyword);
            } else {
                // Pass the temp path explicitly
                List<Laptop> ranked = pageRanking(keyword, rowsToRank, DATA_FILE);
                System.out.println("Ranked results:");
                for (Laptop laptop : ranked) {
                    System.out.printf("Brand: %s | Product: %s | Price: %s%n",
                        laptop.brandName, laptop.product, laptop.price);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading data file: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
