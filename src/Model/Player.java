package Model; // all the game data classes live in this package

public class Player extends Character { // the person playing
    private Inventory inventory = new Inventory(); // the 5 item slots
    private Weapon equipped; // weapon in use, null if none (only one at a time)

    public Player() { // constructor
        super("You", 100); // player starts with 100 hp and that is also the max
    }

    public Inventory getInventory() { // gets the inventory
        return inventory; // send it back
    }

    public Weapon getEquipped() { // gets the equipped weapon
        return equipped; // send it back
    }

    public void equip(Weapon weapon) { // equips a weapon, replaces the old one
        equipped = weapon; // save it
    }

    public void breakEquipped() { // weapon broke so it's gone for good
        inventory.remove(inventory.indexOf(equipped)); // take it out of its slot
        equipped = null; // nothing equipped now
    }

    public void fullHeal() { // used after dying
        setHp(getMaxHp()); // back to max health
    }
}
