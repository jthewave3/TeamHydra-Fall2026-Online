package Model; // all the game data classes live in this package

import java.util.Random; // needed to roll damage

public class Weapon extends Item { // something you equip and attack with
    private int minDamage; // lowest damage
    private int maxDamage; // highest damage
    private int uses; // hits left (or arrows left for the bow)
    private int maxUses; // how many uses it started with
    private boolean bow; // true if it uses arrows instead of durability
    private boolean slow; // true if it can only attack every other turn (fire axe)
    private boolean resting; // true while a slow weapon is recovering

    public Weapon(String id, String name, String description, int minDamage, int maxDamage, int uses, boolean bow, boolean slow) { // constructor
        super(id, name, description); // id, name and description go to Item
        this.minDamage = minDamage; // save min damage
        this.maxDamage = maxDamage; // save max damage
        this.uses = uses; // starts full
        this.maxUses = uses; // remember the max
        this.bow = bow; // save bow flag
        this.slow = slow; // save slow flag
    }

    public int rollDamage(Random rand) { // random damage between min and max
        return minDamage + rand.nextInt(maxDamage - minDamage + 1); // min plus a random amount
    }

    public void useOnce() { // one attack uses one hit or one arrow
        uses--; // take one away
    }

    public boolean canAttack() { // false if the bow has no arrows
        return uses > 0; // needs at least 1 use left
    }

    public boolean isBroken() { // the bow never breaks, everything else breaks at 0
        return !bow && uses <= 0; // broken when out of uses
    }

    public int getUses() { // gets uses left
        return uses; // send it back
    }

    public void setUses(int uses) { // sets uses left (used when loading a save)
        this.uses = uses; // save it
    }

    public boolean isSlow() { // gets slow flag
        return slow; // send it back
    }

    public boolean isResting() { // gets resting flag
        return resting; // send it back
    }

    public void setResting(boolean resting) { // sets resting flag
        this.resting = resting; // save it
    }

    public String getStatus() { // text like "13/20 hits available"
        if (bow) { // bow counts arrows
            return uses + "/" + maxUses + " arrows left"; // arrows text
        }
        return uses + "/" + maxUses + " hits available"; // hits text
    }
}
