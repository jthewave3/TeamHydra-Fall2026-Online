package model; // all the game data classes live in this package

public class Character { // parent of Player and Monster, anything with health
    private String name; // who this is
    private int hp; // current health
    private int maxHp; // most health it can have

    public Character(String name, int hp) { // constructor
        this.name = name; // save the name
        this.hp = hp; // start at full health
        this.maxHp = hp; // the starting health is the max
    }

    public String getName() { // gets the name
        return name; // send it back
    }

    public int getHp() { // gets current health
        return hp; // send it back
    }

    public int getMaxHp() { // gets max health
        return maxHp; // send it back
    }

    public void setHp(int hp) { // sets health but keeps it between 0 and max
        if (hp > maxHp) hp = maxHp; // can't go over the max
        if (hp < 0) hp = 0; // can't go under 0
        this.hp = hp; // save it
    }

    public boolean isAlive() { // alive as long as hp is above 0
        return hp > 0; // true or false
    }

    public void takeDamage(int amount) { // lose health
        setHp(hp - amount); // subtract the damage
    }

    public void heal(int amount) { // gain health
        setHp(hp + amount); // add the healing
    }
}
