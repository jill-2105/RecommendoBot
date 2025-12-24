package spellcheckingusingtrie;

public class EditDistance {

    public static int calculateEditDistance(String originalWord, String comparedWord) {
        int originalLength = originalWord.length();
        int comparedLength = comparedWord.length();

        // Create a 2D matrix (DP table) to store results of subproblems.
        int[][] editDistanceTable = new int[originalLength + 1][comparedLength + 1];

        // Build the table in a bottom-up manner
        for (int i = 0; i <= originalLength; i++) {
            for (int j = 0; j <= comparedLength; j++) {

                // If the original word is empty, it requires j insertions to form the compared word
                if (i == 0) {
                    editDistanceTable[i][j] = j;
                }

                // If the compared word is empty, it requires i deletions to remove all characters from original
                else if (j == 0) {
                    editDistanceTable[i][j] = i;
                }

                // If the current characters in both words match, no edit is needed
                else if (originalWord.charAt(i - 1) == comparedWord.charAt(j - 1)) {
                    editDistanceTable[i][j] = editDistanceTable[i - 1][j - 1];
                }

                // If characters differ, consider all three edit operations:
                else {
                    editDistanceTable[i][j] = 1 + Math.min(
                        editDistanceTable[i - 1][j],        // Deletion
                        Math.min(
                            editDistanceTable[i][j - 1],    // Insertion
                            editDistanceTable[i - 1][j - 1] // Substitution
                        )
                    );
                }
            }
        }

        // The result at the bottom-right corner is the total edit distance
        return editDistanceTable[originalLength][comparedLength];
    }
}
