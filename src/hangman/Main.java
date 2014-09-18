package hangman;

import java.io.File;
import java.util.Set;

/**
 * Created by Matt on 9/18/2014.
 */
public class Main {

    public static void main(String[] args){
        Hangman hm = new Hangman();
        hm.startGame(new File("dictionary.txt"), 5);
        try {
            for(int i = 0;i<26;i++){
                char c = (char) ('a'+i);
                hm.setWords(hm.makeGuess(c));
            }
        } catch (EvilHangmanGame.GuessAlreadyMadeException e) {
            e.printStackTrace();
        }
    }
}
