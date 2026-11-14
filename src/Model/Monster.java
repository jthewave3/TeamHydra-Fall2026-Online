package Model; // all the game data classes live in this package

import java.util.Random; // needed for random hp and damage

public class Monster extends Character { // an enemy the player fights
    // each row: id, name, min hp, max hp, min damage, max damage, enter text, win text, flee text
    private static String[][] TABLE = { // the data table
        // section 1 (floors 10 to 8)
        {"M00", "Bellhop Ghost", "20", "30", "4", "6", "A translucent bellhop drags luggage and blocks your way.", "The ghost lets go of its baggage and dissolves into the dark.", "You back away. The bellhop keeps dragging its invisible bags, uninterested in you."}, // Bellhop Ghost
        {"M01", "Room Service Ghoul", "20", "30", "4", "6", "A ghoul pushes a squeaking cart of rotten food, calling out a name that isn't yours.", "The cart tips over as the ghoul collapses and fades.", "You slip past. It's still calling that name, over and over."}, // Room Service Ghoul
        {"M02", "Laundry Wraith", "20", "30", "4", "6", "A shape hunches over a laundry cart, folding sheets that are already clean.", "It stops folding at last, and the sheets settle into a still, empty pile.", "You back away. It keeps folding, never looking up."}, // Laundry Wraith
        // section 2 (floors 7 to 5)
        {"M03", "The Concierge", "35", "45", "7", "13", "Its smile is stitched too wide to be real. It already knows your room number.", "The smile finally falls, and it fades behind its podium.", "You leave before it can 'assist' you further."}, // The Concierge
        {"M04", "Housekeeping Horror", "35", "45", "7", "13", "Its cart is full of things that used to be people.", "The cart falls still as it collapses beside it.", "You leave the cart exactly where it is."}, // Housekeeping Horror
        {"M05", "Weeping Maid", "35", "45", "7", "13", "She never stops scrubbing the same spot of floor, and she's noticed you watching.", "She stops scrubbing at last, and the room goes quiet.", "She keeps scrubbing. The stain never comes out."}, // Weeping Maid
        // section 3 (floors 4 to 2)
        {"M06", "Night Auditor", "55", "65", "15", "20", "It balances the books in blood, and the ledger is never full.", "The ledger slams shut as it fades from its desk.", "You slip out. The pen keeps scratching without you."}, // Night Auditor
        {"M07", "The Doorman", "55", "65", "15", "20", "He holds the door, and he has never once let anyone through it.", "He finally steps aside and fades from the doorway.", "He doesn't move. Neither does the door."}, // The Doorman
        {"M08", "Checked In, Never Out", "55", "65", "15", "20", "A guest stands at the front desk, still waiting on a reservation that was never real.", "It finally stops waiting, and the desk bell goes silent.", "It doesn't move. It's still waiting for someone to check it in."} // Checked In, Never Out
    }; // end of the table

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
        String[] row = TABLE[section * 3 + rand.nextInt(3)]; // one of the 3 monsters in that section
        int minHp = Integer.parseInt(row[2]); // lowest hp from the table
        int maxHp = Integer.parseInt(row[3]); // highest hp from the table
        int hp = minHp + rand.nextInt(maxHp - minHp + 1); // random hp in that range
        return new Monster(row[0], row[1], hp, Integer.parseInt(row[4]), Integer.parseInt(row[5]), row[6], row[7], row[8], false); // build the monster
    }

    public static Monster makeBoss() { // the manager on floor 1
        return new Monster("BOSS_00", "The Manager", 90, 20, 30, "It owns this hotel, and everyone who ever checked in. He's been expecting you.", "It finally lets go, and the hotel around you seems to exhale.", "There is no leaving this room.", true); // build the monster
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
