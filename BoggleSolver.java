import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BoggleSolver
{
    private ExposedTrieSET trie = new ExposedTrieSET();
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (String word : dictionary) {
            trie.add(word);
        }
        // trie.add("mar");
        // trie.add("marry");
        // trie.add("marvellous");
    }

    private class Query {
        public boolean success;
        public StringBuilder queryWord;
        public int[] currentCoords;
        public Set<int[]> usedCoords;
        public ArrayList<Query> nextQueries = new ArrayList<>();
        public int length;

        public Query(Query query, char c, int[] coord) {
            this.success = false;
            this.queryWord = new StringBuilder(query.queryWord).append(c);
            this.currentCoords = coord;
            this.usedCoords = new HashSet<>(query.usedCoords);
            usedCoords.add(query.currentCoords);
            this.length = query.length++;
        }

        public Query(StringBuilder currentString, int[] currentCoords) {
            this.success = false;
            this.queryWord = currentString;
            this.currentCoords = currentCoords;
            this.usedCoords = new HashSet<>();
            this.length = currentString.length();
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Query query = addChar(board, new StringBuilder(board.getLetter(0, 0)), 0, 0);
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    // public int scoreOf(String word)

    /* private Query addChar(StringBuilder cur, int i, int j) 
        if no unused letter added to cur can give a valid word, return unsuccessful query
        if can add any surrounding letter, return a list of all new string after adding those letters
        can add if the Node[char] != null -> need modified ExposedTrieSET that extends TrieSET
        for each new word, run addChar

        gotta keep track of the unsable squares somehow

        then just loop through all squares as starting stringbuilder?

        at the end, if query success, check all queries following successful query
        if failed query, add word to list

    */
    private Query addChar(BoggleBoard board, Query query) {
        Set<Character> nextChars = trie.nextCharacters(query.queryWord.toString());

        for (int[] coord : getAllValidNeighbors(board, query)) {
            char c = board.getLetter(coord[0], coord[1]);

            if (!nextChars.contains(c)) {
                continue;
            }

            Query newQuery = new Query(query, c, coord);
            query.nextQueries.add(addChar(board, newQuery)); // doesn't work cuz can't check for used chars properly; build in used char into query?
        }

        return query;
    }

    private int[][] getAllValidNeighbors(BoggleBoard board, Query query) { // check used letters

    }

    /* check Q: at the end of building a string and about to be added to listOfWords, check indexOf(Q) & insert U */

    public static void main(String[] args) {
        BoggleSolver solver = new BoggleSolver(new String[2]);
        System.out.println(solver.trie.longestPrefixOf("marve"));
    }

}
