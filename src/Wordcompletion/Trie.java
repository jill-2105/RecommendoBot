package Wordcompletion;

import java.util.ArrayList;
import java.util.List;

public class Trie {
    private TrieNode root;
    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = root;

        // Convert word to lowercase to ensure case-insensitive matching
        for (char c : word.toLowerCase().toCharArray()) {
            // If the current character doesn't exist in the map, add a new node
            node.children.putIfAbsent(c, new TrieNode());

            // Move to the next node in the path
            node = node.children.get(c);
        }
        node.isEndOfWord = true;
    }

    public List<String> searchPrefix(String prefix) {
        TrieNode node = root;

        // Traverse down the Trie to find the node matching the last prefix character
        for (char c : prefix.toLowerCase().toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                // Prefix not found in Trie; return empty result list
                return new ArrayList<>();
            }
        }

        // If prefix exists, use DFS to collect all possible completions
        List<String> results = new ArrayList<>();
        dfs(node, prefix.toLowerCase(), results);
        return results;
    }

    private void dfs(TrieNode node, String path, List<String> results) {
        // If this node marks a valid word, add it to the result list
        if (node.isEndOfWord) {
            results.add(path);
        }

        // Explore all children recursively, appending each character to the path
        for (char c : node.children.keySet()) {
            dfs(node.children.get(c), path + c, results);
        }
    }
}
