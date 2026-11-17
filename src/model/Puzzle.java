package model; // all the game data classes live in this package

import java.util.Random; // needed to pick a random puzzle and cipher shift

public class Puzzle { // a riddle, word scramble or caesar cipher in a puzzle room
    // each row: id, name, answer, text shown to the player
    private static String[][] TABLE = { // the data table
        // section 1 riddles (floors 10 to 8)
        {"P00", "Room Key", "key", "Engraved on a brass tag hanging from the nightstand:\n\"I open your door, but I'm not a hand.\nLose me, and you're locked out until someone lets you back in.\nWhat am I?\""}, // Room Key
        {"P01", "Minibar", "minibar", "A note taped to a small locked cabinet:\n\"I'm small, I'm cold, and everything inside me costs more than it should. What am I?\""}, // Minibar
        {"P02", "Do Not Disturb", "sign", "Hanging crooked from a doorknob:\n\"I hang on a handle and speak for you when you can't answer the door. What am I?\""}, // Do Not Disturb
        {"P03", "Elevator", "elevator", "Scratched into the call button panel:\n\"I go up, I go down, but I never take a single step. What am I?\""}, // Elevator
        // section 2 word scrambles (floors 7 to 5)
        {"P04", "Housekeeping Cart", "cart", "Left abandoned in the halls by the weeping maids:\n\"T R C A\"\nUnscramble the letters into one real word."}, // Housekeeping Cart
        {"P05", "Vacancy Sign", "vacancy", "Flickering outside the window, the sign's letters have rearranged themselves:\n\"Y A C V A N C\"\nUnscramble the letters into one real word."}, // Vacancy Sign
        {"P06", "Ice Machine", "icemachine", "Frost has fogged the label into an unreadable jumble:\n\"M C A N I E H C I E\"\nUnscramble the letters into one real word."}, // Ice Machine
        {"P07", "Luggage", "luggage", "Sitting alone by the elevator, letters have fallen out of order:\n\"E G G A L U G\"\nUnscramble the letters into one real word."}, // Luggage
        // section 3 caesar ciphers (floors 4 to 2), the coded word gets added when the puzzle is made
        {"P08", "The Guest Book", "guestbook", "Open on the front desk, pages damp."}, // The Guest Book
        {"P09", "The Bathroom Mirror", "mirror", "Fogged despite the cold in the bathroom."}, // The Bathroom Mirror
        {"P10", "The Wake-Up Call", "phone", "It rings once, then falls silent."}, // The Wake-Up Call
        {"P11", "The Bellboy", "bellboy", "A brass name tag lies abandoned on the carpet, the name beneath it scratched into code."} // The Bellboy
    }; // end of the table

    private String id; // puzzle id like P00
    private String name; // puzzle name
    private String answer; // the right answer
    private String type; // riddle, scramble or caesar
    private String text; // what the player reads
    private int maxAttempts; // tries allowed (4, 3 or 2)
    private int attemptsLeft; // tries still left
    private boolean solved; // true once it's solved
    private boolean locked; // true when the player ran out of tries
    private int visits; // rooms visited since it got locked

    public Puzzle(String id, String name, String answer, String type, String text, int maxAttempts) { // constructor
        this.id = id; // save the id
        this.name = name; // save the name
        this.answer = answer; // save the answer
        this.type = type; // save the type
        this.text = text; // save the text
        this.maxAttempts = maxAttempts; // save the max tries
        this.attemptsLeft = maxAttempts; // start with all the tries
    }

    public static Puzzle random(int floor, Random rand) { // picks a random puzzle for the floor
        int section = (10 - floor) / 3; // 0 for floors 10-8, 1 for 7-5, 2 for 4-2
        String[] row = TABLE[section * 4 + rand.nextInt(4)]; // one of the 4 puzzles in that section
        String text = row[3]; // the text from the table
        String type = "riddle"; // section 1 is riddles
        int attempts = 4; // riddles get 4 tries
        if (section == 1) { // section 2 is scrambles
            type = "scramble"; // set the type
            attempts = 3; // scrambles get 3 tries
        }
        if (section == 2) { // section 3 is caesar ciphers
            type = "caesar"; // set the type
            attempts = 2; // ciphers get 2 tries
            int shift = 2 + rand.nextInt(3); // shift by 2, 3 or 4
            text = text + " A note beside it reads \"shifted by " + shift + "\": " + shiftWord(row[2], shift); // add the coded word
            text = text + "\nMove each letter back " + shift + " places to decode it."; // tell them how to decode
        }
        text = text + "\nType: solve <answer>"; // every puzzle says how to answer
        return new Puzzle(row[0], row[1], row[2], type, text, attempts); // make the puzzle
    }

    private static String shiftWord(String word, int shift) { // moves every letter forward in the alphabet
        String result = ""; // the coded word
        for (int i = 0; i < word.length(); i++) { // go through each letter
            int letter = word.charAt(i) - 'a'; // a is 0, b is 1, and so on
            letter = (letter + shift) % 26; // shift it and wrap around after z
            result = result + (char) ('A' + letter) + " "; // add the new letter and a space
        }
        return result.trim(); // remove the extra space on the end
    }

    public boolean checkAnswer(String guess) { // true if the guess is right, otherwise uses up a try
        guess = guess.toLowerCase().replace(" ", ""); // ignore capitals and spaces
        if (guess.equals(answer)) { // correct
            solved = true; // mark it solved
            return true; // right answer
        }
        attemptsLeft--; // wrong answer costs a try
        if (attemptsLeft <= 0) { // no tries left
            locked = true; // lock the room
            visits = 0; // start counting visits
        }
        return false; // wrong answer
    }

    public void roomVisited() { // called when the player goes into some other room
        if (locked) { // only matters if the puzzle is locked
            visits++; // one more room visited
            if (visits >= 2) { // visited at least 2 other rooms
                locked = false; // unlock it
                attemptsLeft = maxAttempts; // get all the tries back
            }
        }
    }

    public void reset() { // puts the puzzle back how it started (used when the player dies)
        solved = false; // not solved
        locked = false; // not locked
        attemptsLeft = maxAttempts; // all tries back
        visits = 0; // reset the counter
    }

    public void setSolved(boolean solved) { // sets solved (used when loading a save)
        this.solved = solved; // save it
    }

    public String getId() { // gets the id
        return id; // send it back
    }

    public String getName() { // gets the name
        return name; // send it back
    }

    public String getType() { // gets the type
        return type; // send it back
    }

    public String getText() { // gets the text
        return text; // send it back
    }

    public int getAttemptsLeft() { // gets tries left
        return attemptsLeft; // send it back
    }

    public boolean isSolved() { // gets solved flag
        return solved; // send it back
    }

    public boolean isLocked() { // gets locked flag
        return locked; // send it back
    }
}
