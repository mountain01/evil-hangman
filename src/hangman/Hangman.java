package hangman;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.lang.StringBuilder;

/**
 * Created by Matt on 9/16/2014.
 */
public class Hangman implements EvilHangmanGame {

    private Set<String> words;
    private int length;
    private String key;

    public void setWords(Set<String> words) {
        this.words = words;
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
           newKey.append('_');
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
        Map<String,Set<String>> group = new HashMap<String, Set<String>>();
        for(String word:words){
            String key = makeKey(word,guess);
            if(!group.containsKey(key)){
                group.put(key,new HashSet<String>());
            }
            group.get(key).add(word);
        }
        return makeSingleSet(group,guess);
    }

    private Set<String> makeSingleSet(Map<String,Set<String>> subsets,char guess){
        // get largest sets
        subsets = getLargestSets(subsets);
        if(subsets.size() == 1){
            return extractSet(subsets);
        }
        // if more than one of same size, return one with key with no guessed letters in it.
        subsets = noGuessedLetters(subsets,guess);
        if(subsets.size() == 1){
            return extractSet(subsets);
        }
        // get right most guess;
        subsets = getRightmost(subsets,guess,length);
        if(subsets.size() == 1){
            return extractSet(subsets);
        }

       return null;
    }

    private Map<String,Set<String>> getRightmost(Map<String,Set<String>> subsets,char guess,int myIndex){
        Map<String,Set<String>> tempMap = new HashMap<String, Set<String>>();
        int smallestIndex = 0;
        for(String key:subsets.keySet()){
            int index = indexOf(key,guess,myIndex);
            smallestIndex = index > smallestIndex ? index : smallestIndex;
        }
        for(String key:subsets.keySet()){
            int index = indexOf(key,guess,myIndex);
            if(index == smallestIndex){
                tempMap.put(key,subsets.get(key));
            }
        }
        if(tempMap.size() > 1){
            tempMap = getRightmost(tempMap,guess,smallestIndex);
        }
        return tempMap;
    }

    private Map<String,Set<String>> noGuessedLetters(Map<String,Set<String>> subsets,char guess){
        Map<String,Set<String>> prunedMap = new HashMap<String, Set<String>>();
        Map<String,Set<String>> prunedMap2 = new HashMap<String, Set<String>>();
        for(String key:subsets.keySet()){
            Set<String> set = subsets.get(key);
            if(indexOf(key,guess,length) == -1){
                prunedMap.put(key,set);
            }
            else{
                prunedMap2.put(key,set);
            }
        }
        return prunedMap.size() > 0 ? prunedMap : prunedMap2;
    }

    private int indexOf(String inString,  char inChar, int index){
        for(int i = index-1;i>=0;i--){
            if(inString.charAt(i) == inChar){
                return i;
            }
        }
        return -1;
    }

    private Set<String> extractSet(Map<String,Set<String>> subsets){
        for(String newKey:subsets.keySet()){
            key = newKey;
            return subsets.get(newKey);
        }
        return null;
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
