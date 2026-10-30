package Model;

import java.util.Random;

public class Monster extends Character {
    private static final Random RNG = new Random();

    private final int section;
    private final int minHp;
    private final int maxHp;
    private final int minDamage;
    private final int maxDamage;

    /** Rolls HP within [minHp, maxHp]. Each copy placed in a room rolls its own, when the hotel is built. */
    public Monster(String name, int minHp, int maxHp, int minDamage, int maxDamage, int section) {
        super(name, roll(minHp, maxHp));
        this.minHp = minHp;
        this.maxHp = maxHp;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.section = section;
    }

    /** A new monster of the same kind with its own freshly rolled HP. */
    public Monster copy() {
        return new Monster(getName(), minHp, maxHp, minDamage, maxDamage, section);
    }

    private static int roll(int min, int max) { return min + RNG.nextInt(max - min + 1); }

    public int getSection() { return section; }

    /** Damage is re-rolled within the monster's range on every hit. */
    public int attackDamage() { return roll(minDamage, maxDamage); }
}
