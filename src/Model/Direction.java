package Model; // all the game data classes live in this package

public enum Direction { // the four ways the player can move
    NORTH, EAST, SOUTH, WEST; // order matters, opposite() relies on it

    public Direction opposite() { // gives the direction facing the other way
        return values()[(ordinal() + 2) % 4]; // north and south are 2 apart, east and west are 2 apart
    }

    public String label() { // nice looking name like "North"
        return name().substring(0, 1) + name().substring(1).toLowerCase(); // first letter big, rest small
    }

    public static Direction fromText(String text) { // turns what the user typed into a direction
        text = text.toLowerCase(); // so "North" and "north" both work
        if (text.equals("north") || text.equals("n")) return NORTH; // north
        if (text.equals("east") || text.equals("e")) return EAST; // east
        if (text.equals("south") || text.equals("s")) return SOUTH; // south
        if (text.equals("west") || text.equals("w")) return WEST; // west
        return null; // it wasn't a direction
    }
}
