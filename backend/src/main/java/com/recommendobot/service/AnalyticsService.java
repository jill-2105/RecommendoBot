package com.recommendobot.service;

import com.recommendobot.model.SearchFrequency;
import com.recommendobot.repository.SearchFrequencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyticsService {

    @Autowired
    private SearchFrequencyRepository searchFrequencyRepository;

    public Map<String, Integer> getSearchFrequency() {
        List<SearchFrequency> list = searchFrequencyRepository.findAll();
        Map<String, Integer> map = new LinkedHashMap<>();
        list.forEach(sf -> map.put(sf.getSearchTerm(), sf.getCount()));
        return map;
    }

    public void increaseSearchFrequencyCount(String query) {
        if (query == null || query.trim().isEmpty()) return;
        String key = query.trim().toLowerCase();
        Optional<SearchFrequency> opt = searchFrequencyRepository.findBySearchTerm(key);
        if (opt.isPresent()) {
            SearchFrequency sf = opt.get();
            sf.incrementCount();
            searchFrequencyRepository.save(sf);
        } else {
            SearchFrequency sf = new SearchFrequency();
            sf.setSearchTerm(key);
            sf.setCount(1);
            searchFrequencyRepository.save(sf);
        }
    }

    public Map<String, Integer> getWordFrequency(String q) {
        List<SearchFrequency> list = searchFrequencyRepository.findAll();
        Map<String, Integer> counts = new HashMap<>();
        for (SearchFrequency sf : list) {
            String term = sf.getSearchTerm();
            if (q != null && !q.trim().isEmpty()) {
                if (!term.contains(q.toLowerCase())) continue;
            }
            String[] parts = term.split("\\W+");
            for (String p : parts) {
                if (p == null || p.isBlank()) continue;
                String w = p.toLowerCase();
                counts.put(w, counts.getOrDefault(w, 0) + sf.getCount());
            }
        }
        // sort by value desc
        LinkedHashMap<String,Integer> sorted = new LinkedHashMap<>();
        counts.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue(Comparator.reverseOrder()))
                .forEach(e -> sorted.put(e.getKey(), e.getValue()));
        return sorted;
    }
}
