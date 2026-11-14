package Model; // all the game data classes live in this package

import java.util.Random; // needed to pick random items

public abstract class Item { // parent class for weapons and consumables
    // ids of every item that can show up in a room (A01 is the key so it isn't here)
    public static final String[] ALL_IDS = {"A02", "A03", "A04", "A05", "A06", "A07", "A08", "A09", "A10"}; // every id that can be found

    private String id; // item code like A02
    private String name; // name the player sees
    private String description; // text for the inspect command

    public Item(String id, String name, String description) { // constructor
        this.id = id; // save the id
        this.name = name; // save the name
        this.description = description; // save the description
    }

    public String getId() { // gets the id
        return id; // send it back
    }

    public String getName() { // gets the name
        return name; // send it back
    }

    public String getDescription() { // gets the description
        return description; // send it back
    }

    public static Item make(String id) { // builds a brand new item from its id
        if (id.equals("A02")) { // sword
            return new Weapon("A02", "Sword", "An old sword with a scratched blade. Deals 5-8 damage and breaks after 20 attacks.", 5, 8, 20, false, false); // make the Sword
        }
        if (id.equals("A03")) { // bow
            return new Weapon("A03", "Bow & Arrow", "A wooden bow. Deals 10-15 damage. It holds 15 arrows and never breaks.", 10, 15, 15, true, false); // make the Bow & Arrow
        }
        if (id.equals("A04")) { // fire axe
            return new Weapon("A04", "Fire Axe", "A heavy axe. Deals 20-28 damage but only swings every other turn. Breaks after 8 attacks.", 20, 28, 8, false, true); // make the Fire Axe
        }
        if (id.equals("A05")) { // vanishing potion
            return new Consumable("A05", "Vanishing Potion", "A smoky black potion. Makes the monster disappear.", "vanish"); // make the Vanishing Potion
        }
        if (id.equals("A06")) { // shrinking potion
            return new Consumable("A06", "Shrinking Potion", "A bright green potion. Takes away half of the monster's health.", "shrink"); // make the Shrinking Potion
        }
        if (id.equals("A07")) { // healing potion
            return new Consumable("A07", "Healing Potion", "A glowing red potion. Heals 25% of your health.", "heal"); // make the Healing Potion
        }
        if (id.equals("A08")) { // shield potion
            return new Consumable("A08", "Shield Potion", "A silver potion. You take 25% less damage for 2 turns.", "shield"); // make the Shield Potion
        }
        if (id.equals("A09")) { // clone potion
            return new Consumable("A09", "Clone Potion", "A blue potion. A clone doubles your damage for 2 turns.", "clone"); // make the Clone Potion
        }
        if (id.equals("A10")) { // flash bomb
            return new Consumable("A10", "Flash Bomb", "A small device. Stuns the monster for 2 turns.", "stun"); // make the Flash Bomb
        }
        return null; // unknown id
    }

    public static Item randomItem(Random rand) { // picks a random item
        return make(ALL_IDS[rand.nextInt(ALL_IDS.length)]); // random id from the list
    }
}
