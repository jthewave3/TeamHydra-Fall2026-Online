package model; // all the game data classes live in this package

import java.util.ArrayList; // list of data rows
import java.util.Random; // needed to pick a random puzzle and cipher shift

public class Puzzle { // a riddle, word scramble or caesar cipher in a puzzle room
    // each row in puzzles.txt: id, name, section, type, tries, answer, text
    private static ArrayList<String[]> TABLE = null; // loaded from the file the first time it's needed

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
        if (TABLE == null) { // not loaded yet
            TABLE = DataFile.read("puzzles.txt"); // read the file
        }
        int section = (10 - floor) / 3; // 0 for floors 10-8, 1 for 7-5, 2 for 4-2
        ArrayList<String[]> choices = new ArrayList<String[]>(); // every puzzle in that section
        for (String[] r : TABLE) { // go through the table
            if (r[2].equals(String.valueOf(section))) { // same section
                choices.add(r); // keep it
            }
        }
        String[] row = choices.get(rand.nextInt(choices.size())); // pick one
        String type = row[3]; // riddle, scramble or caesar
        int attempts = Integer.parseInt(row[4]); // tries allowed
        String answer = row[5]; // the right answer
        String text = row[6]; // the text from the table
        if (type.equals("caesar")) { // caesar ciphers add a coded word
            int shift = 2 + rand.nextInt(3); // shift by 2, 3 or 4
            text = text + " A note beside it reads \"shifted by " + shift + "\": " + shiftWord(answer, shift); // add the coded word
            text = text + "\nMove each letter back " + shift + " places to decode it."; // tell them how to decode
        }
        text = text + "\nType: solve <answer>"; // every puzzle says how to answer
        return new Puzzle(row[0], row[1], answer, type, text, attempts); // make the puzzle
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
