package model; // all the game data classes live in this package

public class Consumable extends Item { // potions and the flash bomb, they get used up
    private String effect; // vanish, shrink, heal, shield, clone or stun

    public Consumable(String id, String name, String description, String effect) { // constructor
        super(id, name, description); // id, name and description go to Item
        this.effect = effect; // save the effect
    }

    public String getEffect() { // gets the effect
        return effect; // send it back
    }

    public boolean usedOnMonster() { // true for the items that hit the monster instead of the player
        return effect.equals("vanish") || effect.equals("shrink") || effect.equals("stun"); // these three
    }
}
