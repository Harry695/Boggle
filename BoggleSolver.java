import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TrieSET;

public class BoggleSolver
{
    private ExposedTrieSET trie = new ExposedTrieSET();
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (String word : dictionary) {
            if (word.length() > 2) {
                trie.add(word);
            }
        }
        // trie.add("mar");
        // trie.add("marry");
        // trie.add("marvellous");
    }

    private class Index2D {
        public int i, j;
        
        public Index2D(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Index2D that = (Index2D) obj;
            return this.i == that.i && this.j == that.j;
        }

        @Override
        public int hashCode() {
            return i * 100 + j;
        }
    }

    private class Query {
        public boolean isWord;
        public String queryWord;
        public Index2D currentCoords;
        public Set<Index2D> usedCoords;
        public ArrayList<Query> nextQueries = new ArrayList<>();
        public int length;

        /**
         * The success constructor, used when adding a char.
         * @param query current query
         * @param c char to add
         * @param coord coord of the new char
         */
        public Query(Query query, char c, Index2D coord) {
            this.queryWord = query.queryWord + addChar(c);
            this.isWord = trie.contains(queryWord.toString());
            this.currentCoords = coord;
            this.usedCoords = new HashSet<>(query.usedCoords); // don't need to copy?
            this.usedCoords.add(this.currentCoords);
            this.length = query.length++;

            // if (this.isWord) {
            //     System.out.println("\n!" + this.queryWord + "!\n");
            // }
        }

        /**
         * The initialization constructor, used when starting a query.
         * @param currentString string containing the starting char
         * @param currentCoords coords of starting char
         */
        public Query(char startingChar, Index2D currentCoords) {
            this.isWord = false;
            this.queryWord = addChar(startingChar);
            this.currentCoords = currentCoords;
            this.usedCoords = new HashSet<>();
            this.usedCoords.add(this.currentCoords);
            this.length = this.queryWord.length();
        }

        private String addChar(char c) {
            if (c == 'Q') {
                return "QU";
            }
            return c + "";
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Queue<Query> allQueries = new Queue<>();
        TrieSET allWords = new TrieSET();
        
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                allQueries.enqueue(addChar(
                        board, 
                        new Query(
                            board.getLetter(i, j), 
                            new Index2D(i, j))));
            }
        }

        // collect all words recursively
        for (Query q : allQueries) {
            collect(q, allWords);
        }
        return allWords;
    }

    private void collect(Query query, TrieSET wordList) {
        if (query.isWord) {
            wordList.add(query.queryWord);
        }

        for (Query q : query.nextQueries) {
            collect(q, wordList);
        }
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!trie.contains(word)) {
            return 0;
        } else if (word.length() <= 4) {
            return 1;
        } else if (word.length() <= 5) {
            return 2;
        } else if (word.length() <= 6) {
            return 3;
        } else if (word.length() <= 7) {
            return 5;
        } else {
            return 11;
        }
    }

    /* private Query addChar(String cur, int i, int j) 
        if no unused letter added to cur can give a valid word, return unsuccessful query
        if can add any surrounding letter, return a list of all new string after adding those letters
        can add if the Node[char] != null -> need modified ExposedTrieSET that extends TrieSET
        for each new word, run addChar

        gotta keep track of the unsable squares somehow

        then just loop through all squares as starting String?

        at the end, if query success, check all queries following successful query
        if failed query, add word to list

    */
    private Query addChar(BoggleBoard board, Query query) {
        // System.out.println("start addChar");
        Set<Character> validChars = trie.nextCharacters(query.queryWord.toString());
        // System.out.println("found next chars of length " + validChars.size());

        for (Index2D coord : getAllValidNeighbors(board, query)) {
            char c = board.getLetter(coord.i, coord.j);

            if (!validChars.contains(c)) {
                // System.out.println("no valid char found");
                continue;
            }

            // System.out.println("valid path through " + query.queryWord + c);
            Query newQuery = new Query(query, c, coord);
            query.nextQueries.add(addChar(board, newQuery)); 
        }

        // System.out.println("Query for " + query.queryWord + " complete with " + query.nextQueries.size() + " found");
        return query;
    }

    private Queue<Index2D> getAllValidNeighbors(BoggleBoard board, Query query) { // check used letters
        Queue<Index2D> neighbors = new Queue<>();

        int i = query.currentCoords.i;
        int j = query.currentCoords.j;
        for (int deltaI = -1; deltaI < 2; deltaI++) {
            for (int deltaJ = -1; deltaJ < 2; deltaJ++) {
                int newI = i + deltaI;
                int newJ = j + deltaJ;
                Index2D newIndex = new Index2D(newI, newJ);

                if (newI >= 0 && newI < board.rows() && newJ >= 0 && newJ < board.cols() && !query.usedCoords.contains(newIndex)) {
                    neighbors.enqueue(newIndex);
                }
            }
        }

        return neighbors;
    }

    public static void main(String[] args) {
    In in = new In("boggle\\dictionary-algs4.txt");
    String[] dictionary = in.readAllStrings();
    BoggleSolver solver = new BoggleSolver(dictionary);
    BoggleBoard board = new BoggleBoard("boggle\\board-q.txt");
    int score = 0;
    for (String word : solver.getAllValidWords(board)) {
        StdOut.println(word);
        score += solver.scoreOf(word);
    }
    StdOut.println("Score = " + score);
}

}
