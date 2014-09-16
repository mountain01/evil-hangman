package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by Matt on 9/16/2014.
 */
public class Hangman implements EvilHangmanGame {

    Set<String> words;

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
        return null;
    }
}
