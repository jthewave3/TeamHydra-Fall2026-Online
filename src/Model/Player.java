package Model;

import java.util.ArrayList;
import java.util.List;

public class Player extends Character {
    public static final int MAX_HEALTH = 100;
    private static final int BASE_ATTACK = 8;

    private Room location;
    private int floor;
    private Item weapon;
    private final List<Item> inventory = new ArrayList<>();

    public Player(Room start, int floor) {
        super("You", MAX_HEALTH);
        this.location = start;
        this.floor = floor;
    }

    public Room getLocation() { return location; }
    public void setLocation(Room room) { this.location = room; }
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public List<Item> getInventory() { return inventory; }

    public void addItem(Item item) {
        inventory.add(item);
        if (item.isWeapon() && (weapon == null || item.getMaxAttack() > weapon.getMaxAttack())) weapon = item;
    }

    public Item findItem(String name) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(name)) return item;
        }
        return null;
    }

    public void removeItem(Item item) { inventory.remove(item); }

    /** Base attack plus a random roll from the strongest weapon carried. */
    public int getAttackPower() { return BASE_ATTACK + (weapon == null ? 0 : weapon.rollAttack()); }
}
