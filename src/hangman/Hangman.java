package hangman;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.lang.StringBuilder;
import java.util.stream.Collector;

/**
 * Created by Matt on 9/16/2014.
 */
public class Hangman implements EvilHangmanGame {

    private Set<String> words;
    private int length;
    private String key;
    Set<Character> usedLetters = new TreeSet<Character>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsedLetters(){
        StringBuilder result = new StringBuilder();
        for(char c:usedLetters){
            result.append(c).append(" ");
        }
        return result == null ? "" :result.toString();
    }

    /**
     * Starts a new game of evil hangman using words from <code>dictionary</code>
     * with length <code>wordLength</code>
     *
     * @param dictionary Dictionary of words to use for the game
     * @param wordLength Number of characters in the word to guess
     */
    @Override
    public void startGame(File dictionary, int wordLength) {
       words =  addWords(dictionary, wordLength);
       length = wordLength;
       StringBuilder newKey = new StringBuilder();
       int i = 0;
       while(i < length){
           newKey.append('-');
           i++;
       }
        key = newKey.toString();
    }

    private Set<String> addWords(File dictionary, int wordLength){
        Set<String> result = new HashSet<String>();
        try {
            Scanner in = new Scanner(dictionary);
            while(in.hasNext()){
                String word = in.next();
                if (word.length() == wordLength){
                    result.add(word);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Make a guess in the current game.
     *
     * @param guess The character being guessed
     * @return The set of strings that satisfy all the guesses made so far
     * in the game, including the guess made in this call. The game could claim
     * that any of these words had been the secret word for the whole game.
     * @throws hangman.EvilHangmanGame.GuessAlreadyMadeException If the character <code>guess</code>
     *                                   has already been guessed in this game.
     */
    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        guess = Character.toLowerCase(guess);
        if(usedLetters.contains(guess)){
            throw new GuessAlreadyMadeException();
        }
        else {
            usedLetters.add(guess);
        }
        Map<String,Set<String>> group = new HashMap<String, Set<String>>();
        for(String word:words){
            String key = makeKey(word,guess);
            if(!group.containsKey(key)){
                group.put(key,new HashSet<String>());
            }
            group.get(key).add(word);
        }
        // limit by size
        group = getLargestSets(group);
        return findPrioritySet(group, guess);
    }

    private Set<String> findPrioritySet(Map<String,Set<String>> input,char guess){
        int minScore = Integer.MAX_VALUE;
        for(String temKey:input.keySet()){
            int weight = 1;
            int count = 0;
            int weightedScore = 0;
            for(int i = temKey.length()-1;i>=0;i--){
                if(temKey.charAt(i) == guess){
                    weightedScore+=++count*weight;
                }
                weight*=2;
            }
            if(weightedScore < minScore){
                key = temKey;
                minScore = weightedScore;
            }
        }
        words = input.get(key);
        return words;
    }

    private Map<String,Set<String>> getLargestSets(Map<String,Set<String>> subsets){
        int largest = 0;
        for(Set<String> set:subsets.values()){
            largest = set.size() > largest ? set.size() : largest;
        }

        Map<String,Set<String>> prunedMap = new HashMap<String, Set<String>>();
        for(String key:subsets.keySet()){
            Set<String> set = subsets.get(key);
            if(set.size() == largest){
                prunedMap.put(key,set);
            }
        }
        return prunedMap;
    }

    private String makeKey(String word, char guess){
        StringBuilder newKey = new StringBuilder();
        for(int i = 0; i < word.length();i++){
            char c = word.charAt(i);
            if(c == guess){
                newKey.append(c);
            } else {
                newKey.append(key.charAt(i));
            }
        }
        return newKey.toString();
    }
}
