package com.recommendobot.algorithms.laptoprecommendation;

import com.recommendobot.algorithms.searchfrequency.SearchFreq;
import com.recommendobot.algorithms.spellcheckingusingtrie.SpellCheckingMainClass;
import com.recommendobot.algorithms.wordcompletion.wordcompletionTries;

import java.io.IOException;
import java.util.*;

import com.recommendobot.algorithms.crawler.HTMLtoTextConverter;
import com.recommendobot.algorithms.crawler.RegexExtractor;
import com.recommendobot.algorithms.crawler.UWCrawler;
import com.recommendobot.algorithms.pageranking.PageRankerMainClass;
import com.recommendobot.algorithms.pageranking.Laptop;
import com.recommendobot.algorithms.frequencyfinder.FrequencyFinder;
import com.recommendobot.algorithms.frequencyfinder.FrequencyFinder.MatchRecord;
import com.recommendobot.algorithms.invertedindex.InvertedIndexCSV;

public class Features {

    private static final String DATA_FILE = "all_laptops_data.csv";
    private static final SearchFreq sf = new SearchFreq();

    public static void main(String[] args) {
        // Testing Laptop Recommendation System Features
        Map<String, Object> output = FrequencySearch("m1");
        System.out.println(output);

        // Testing Crawler Features
        runWebCrawler("https://www.uwindsor.ca/");
        // Note: runHtmlToTextConverter and runRegexExtractor require a domain parameter
        // runHtmlToTextConverter("uwindsor.ca");
        // runRegexExtractor("uwindsor.ca");
    }

    // -------------------- Laptop Recommendation Features --------------------
    public static Map<String, Integer> addSearchedWordCount(String word) {
        return sf.addSearchedWordCount(word);
    }

    public static List<Map.Entry<String, Integer>> getTop5SearchedWords() {
        return sf.getTop5SearchedWords();
    }

    public static List<String> WordCompletion(String prefix) {
        wordcompletionTries wordCompletionTries = new wordcompletionTries(DATA_FILE);
        return wordCompletionTries.wordCompletion(prefix);
    }

    public static List<String> SpellCheck(String word) {
        SpellCheckingMainClass spc = new SpellCheckingMainClass(DATA_FILE);
        return spc.SpellCheckingUsingTrie(word);
    }

    public static List<Laptop> SearchProduct(String word) {
        try {
            Set<Integer> rowsToRank = InvertedIndexCSV.InvertedIndexing(word, DATA_FILE);

            if (rowsToRank.isEmpty()) {
                return Collections.emptyList();
            }

            return PageRankerMainClass.pageRanking(word, rowsToRank);
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
    public static void runWebCrawler(String startUrl) {
        UWCrawler crawler = new UWCrawler();
        crawler.crawl(startUrl);
    }

    public static void runHtmlToTextConverter(String domain) {
        HTMLtoTextConverter.convertDomain(domain);
    }

    public static void runRegexExtractor(String domain) {
        RegexExtractor.extractMatchesForDomain(domain);
    }
}
