package Model;

import java.util.Random;

/** A weapon (hits for a random bonus in a range), the stairway key, or a potion with its own effect. */
public class Item {
    private static final Random RNG = new Random();

    private final String name;
    private final String description;
    private final ItemEffect effect;
    private final int value;
    private final int duration;
    private final int minAttack;
    private final int maxAttack;
    private final int section;

    public Item(String name, String description, ItemEffect effect, int value, int duration,
                int minAttack, int maxAttack, int section) {
        this.name = name;
        this.description = description;
        this.effect = effect;
        this.value = value;
        this.duration = duration;
        this.minAttack = minAttack;
        this.maxAttack = maxAttack;
        this.section = section;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public ItemEffect getEffect() { return effect; }
    /** Effect strength, usually a percentage. */
    public int getValue() { return value; }
    /** Turns the effect lasts; 0 if it is instant. */
    public int getDuration() { return duration; }
    public int getMinAttack() { return minAttack; }
    public int getMaxAttack() { return maxAttack; }
    public boolean isWeapon() { return effect == ItemEffect.WEAPON; }
    public boolean isPotion() { return effect != ItemEffect.WEAPON && effect != ItemEffect.KEY; }

    /** A random weapon bonus within this weapon's range. */
    public int rollAttack() { return minAttack + RNG.nextInt(maxAttack - minAttack + 1); }

    /** For a section's unique weapon: the section (0-2) it is placed in. -1 for anything else. */
    public int getSection() { return section; }
}
