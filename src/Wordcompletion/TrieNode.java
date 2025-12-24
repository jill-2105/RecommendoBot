package Wordcompletion;

import java.util.HashMap;

public class TrieNode {

    // Stores child nodes: each character points to its corresponding TrieNode
    public HashMap<Character, TrieNode> children;

    // This flag is set to true if the node marks the end of a valid word
    public boolean isEndOfWord;

    public TrieNode() {
        // Initialize the child map to store branches for each character
        children = new HashMap<>();

        // Initially, the node does not represent the end of any word
        isEndOfWord = false;
    }
}
