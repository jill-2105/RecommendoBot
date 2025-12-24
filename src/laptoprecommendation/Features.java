package laptoprecommendation;

import SearchFrequency.SearchFreq;
import spellcheckingusingtrie.SpellCheckingMainClass;
import Wordcompletion.wordcompletionTries;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.net.URI;

import Crawler.HTMLtoTextConverter;
import Crawler.RegexExtractor;
import Crawler.UWCrawler;
import pageRanking.PageRankerMainClass;
import pageRanking.Laptop;
import FrequencyFinder.FrequencyFinder;
import FrequencyFinder.FrequencyFinder.MatchRecord;
import InvertedIndex.InvertedIndexCSV;

public class Features {

    // Initialize with default, but static block will update it to absolute path
    private static String DATA_FILE = "all_laptops_data.csv";
    private static final SearchFreq sf = new SearchFreq();

    // -------------------------------------------------------------------------
    // STATIC BLOCK: Runs once when the app starts to extract the CSV from JAR
    // -------------------------------------------------------------------------
    static {
        try {
            System.out.println("Attempting to extract CSV from JAR...");
            // Get the file stream from inside the JAR
            InputStream is = Features.class.getClassLoader().getResourceAsStream("all_laptops_data.csv");
            
            if (is != null) {
                // Create a temporary file on the real server disk
                File tempFile = File.createTempFile("laptops_data_extracted", ".csv");
                tempFile.deleteOnExit(); // Auto-delete when app stops

                // Copy data from JAR stream to the temp file
                Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                // Update the DATA_FILE path to point to the real temp file
                DATA_FILE = tempFile.getAbsolutePath();
                System.out.println("SUCCESS: Extracted CSV to " + DATA_FILE);
            } else {
                System.err.println("CRITICAL ERROR: Could not find 'all_laptops_data.csv' in JAR resources!");
            }
        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: Failed to extract CSV file.");
            e.printStackTrace();
        }
    }
    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        // Main method for testing crawler logic locally
        Map<String, String> result = crawlAndExtract("https://www.hp.com/ca-en/shop/offer.aspx?p=contact-hp-store");

        System.out.println("----- Final Extracted Data -----");
        result.forEach((key, value) -> System.out.println(key + ": " + value));
    }

    public static Map<String, Integer> addSearchedWordCount(String word) {
        return sf.addSearchedWordCount(word);
    }

    public static List<Map.Entry<String, Integer>> getTop5SearchedWords() {
        return sf.getTop5SearchedWords();
    }

    public static List<String> WordCompletion(String prefix) {
        // Uses the updated DATA_FILE path (temp file)
        wordcompletionTries wordCompletionTries = new wordcompletionTries(DATA_FILE);
        return wordCompletionTries.wordCompletion(prefix);
    }

    public static List<String> SpellCheck(String word) {
        // Uses the updated DATA_FILE path (temp file)
        SpellCheckingMainClass spc = new SpellCheckingMainClass(DATA_FILE);
        return spc.SpellCheckingUsingTrie(word);
    }

    public static List<Laptop> SearchProduct(String word) {
        try {
            if (word == null || word.trim().isEmpty()) {
                return PageRankerMainClass.loadAllLaptops(DATA_FILE);
            }
            Set<Integer> rowsToRank = InvertedIndexCSV.InvertedIndexing(word, DATA_FILE);

            if (rowsToRank.isEmpty()) {
                return Collections.emptyList();
            }
            return PageRankerMainClass.pageRanking(word, rowsToRank, DATA_FILE);
        } catch (IOException e) {
            System.out.println("Error in SearchProduct: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static Map<String, Object> FrequencySearch(String keyword) {
        List<MatchRecord> matches = FrequencyFinder.findMatches(keyword, DATA_FILE);
        int totalCount = matches.size();
        Map<String, Object> result = new HashMap<>();
        result.put("word", keyword);
        result.put("occurrence", totalCount);
        return result;
    }

    // -------------------- Crawler Features --------------------
    public static Map<String, String> crawlAndExtract(String url) {
        runWebCrawler(url);

        String domain = getDomainName(url); // extract domain like "uwaterloo.ca"

        runHtmlToTextConverter(domain);     // only process HTMLs from this domain
        return runRegexExtractor(domain);   // only extract from domain-specific text
    }

    public static void runWebCrawler(String startUrl) {
        UWCrawler crawler = new UWCrawler();
        crawler.crawl(startUrl);
    }

    public static void runHtmlToTextConverter(String domain) {
        HTMLtoTextConverter.convertDomain(domain);
    }

    public static Map<String, String> runRegexExtractor(String domain) {
        return RegexExtractor.extractMatchesForDomain(domain);
    }

    private static String getDomainName(String url) {
        try {
            return new URI(url).getHost().replaceFirst("^www\\.", "");
        } catch (Exception e) {
            throw new RuntimeException("Invalid URL: " + url);
        }
    }
}
