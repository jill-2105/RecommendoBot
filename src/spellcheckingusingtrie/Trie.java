package spellcheckingusingtrie;

import java.util.*;

// Represents a single node within the Trie structure
class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>(); // Stores mappings from each character to its child node
    boolean isCompleteWord = false; // Marks if the current node signifies the end of a complete word
}

// Trie data structure for efficient insertion and lookup of vocabulary words
public class Trie {
    private final TrieNode root = new TrieNode(); // Root node of the Trie — does not store any character itself
    private final List<String> storedWords = new ArrayList<>(); // Maintains a list of all unique words added to the Trie
    public void insert(String word) {
        TrieNode current = root;
        String normalizedWord = word.toLowerCase(); // Normalize to lowercase to ensure case-insensitive storage

        // Traverse or build the path in the Trie for each character in the word
        for (char ch : normalizedWord.toCharArray()) {
            current = current.children.computeIfAbsent(ch, character -> new TrieNode());
        }
        // Mark the final node as the end of a valid word
        current.isCompleteWord = true;

        // Add the word to the list of all stored vocabulary words
        storedWords.add(normalizedWord);
    }

    public boolean search(String wordToSearch) {
        TrieNode current = root;
        String normalizedWord = wordToSearch.toLowerCase(); // Normalize input to lowercase for consistency

        // Traverse the Trie by following each character in the input word
        for (char ch : normalizedWord.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) {
                // Character path not found, word does not exist
                return false;
            }
        }
        return current.isCompleteWord;
    }

    public List<String> getAllWords() {
        return storedWords;
    }
}
