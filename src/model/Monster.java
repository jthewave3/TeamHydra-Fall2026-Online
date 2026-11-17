package model; // all the game data classes live in this package

import java.util.ArrayList; // list of data rows
import java.util.Random; // needed for random hp and damage

public class Monster extends Character { // an enemy the player fights
    // each row in monsters.txt: id, name, section, min hp, max hp, min damage, max damage, enter text, win text, flee text
    private static ArrayList<String[]> TABLE = null; // loaded from the file the first time it's needed

    private static ArrayList<String[]> table() { // loads the monster data once
        if (TABLE == null) { // not loaded yet
            TABLE = DataFile.read("monsters.txt"); // read the file
        }
        return TABLE; // send it back
    }

    private static Monster fromRow(String[] row, int hp, boolean boss) { // builds a monster from a data row
        return new Monster(row[0], row[1], hp, Integer.parseInt(row[5]), Integer.parseInt(row[6]), row[7], row[8], row[9], boss); // build the monster
    }

    private String id; // monster id like M00
    private int minDamage; // lowest damage it can do
    private int maxDamage; // highest damage it can do
    private String enterText; // shown when the fight starts
    private String winText; // shown when you beat it
    private String fleeText; // shown when you run away
    private boolean boss; // true only for the manager

    public Monster(String id, String name, int hp, int minDamage, int maxDamage, String enterText, String winText, String fleeText, boolean boss) { // constructor
        super(name, hp); // name and hp go to Character
        this.id = id; // save the id
        this.minDamage = minDamage; // save min damage
        this.maxDamage = maxDamage; // save max damage
        this.enterText = enterText; // save enter text
        this.winText = winText; // save win text
        this.fleeText = fleeText; // save flee text
        this.boss = boss; // save boss flag
    }

    public static Monster random(int floor, Random rand) { // picks a random monster for the floor
        if (floor == 1) { // floor 1 only has the boss
            return makeBoss(); // so make the boss
        }
        int section = (10 - floor) / 3; // floors 10-8 are 0, 7-5 are 1, 4-2 are 2
        ArrayList<String[]> choices = new ArrayList<String[]>(); // every monster in that section
        for (String[] r : table()) { // go through the table
            if (r[2].equals(String.valueOf(section))) { // same section
                choices.add(r); // keep it
            }
        }
        String[] row = choices.get(rand.nextInt(choices.size())); // pick one
        int minHp = Integer.parseInt(row[3]); // lowest hp from the table
        int maxHp = Integer.parseInt(row[4]); // highest hp from the table
        int hp = minHp + rand.nextInt(maxHp - minHp + 1); // random hp in that range
        return fromRow(row, hp, false); // build the monster
    }

    public static Monster makeBoss() { // the manager on floor 1
        for (String[] row : table()) { // find the boss row
            if (row[2].equals("boss")) { // the boss section
                return fromRow(row, Integer.parseInt(row[3]), true); // build the monster
            }
        }
        throw new IllegalStateException("monsters.txt has no boss row"); // the file is missing the boss
    }

    public int rollDamage(Random rand) { // random attack damage
        return minDamage + rand.nextInt(maxDamage - minDamage + 1); // min plus a random amount
    }

    public void resetHealth() { // monsters come back at full health when the player dies
        setHp(getMaxHp()); // back to max
    }

    public String getId() { // gets the id
        return id; // send it back
    }

    public String getEnterText() { // gets the enter text
        return enterText; // send it back
    }

    public String getWinText() { // gets the win text
        return winText; // send it back
    }

    public String getFleeText() { // gets the flee text
        return fleeText; // send it back
    }

    public boolean isBoss() { // gets the boss flag
        return boss; // send it back
    }
}
