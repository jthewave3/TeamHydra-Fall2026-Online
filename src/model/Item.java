package model; // all the game data classes live in this package

import java.util.ArrayList; // list of data rows
import java.util.Random; // needed to pick random items

public abstract class Item { // parent class for weapons and consumables
    // every item that can show up in a room comes from items.txt (A01 is the key so it isn't there)
    private static ArrayList<String[]> TABLE = null; // loaded from the file the first time it's needed

    private static ArrayList<String[]> table() { // loads the item data once
        if (TABLE == null) { // not loaded yet
            TABLE = DataFile.read("items.txt"); // read the file
        }
        return TABLE; // send it back
    }

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
        for (String[] row : table()) { // find the row with this id
            if (row[1].equals(id)) { // match
                if (row[0].equals("W")) { // weapon row
                    return new Weapon(row[1], row[2], row[3], Integer.parseInt(row[4]), Integer.parseInt(row[5]), Integer.parseInt(row[6]), Boolean.parseBoolean(row[7]), Boolean.parseBoolean(row[8])); // make the weapon
                }
                return new Consumable(row[1], row[2], row[3], row[4]); // make the consumable
            }
        }
        return null; // unknown id
    }

    public static Item randomItem(Random rand) { // picks a random item
        return make(table().get(rand.nextInt(table().size()))[1]); // random id from the file
    }
}
