package Model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A place in the hotel. Rooms parsed from Room.txt are templates; {@link #spawn} makes the
 * instances that go into the hotel, which Hotel then fills with a monster, puzzle or items.
 */
public class Room {
    public enum Direction {
        NORTH, SOUTH, EAST, WEST, UP, DOWN;

        public Direction opposite() {
            switch (this) {
                case NORTH: return SOUTH;
                case SOUTH: return NORTH;
                case EAST: return WEST;
                case WEST: return EAST;
                case UP: return DOWN;
                default: return UP;
            }
        }

        public String label() { return name().toLowerCase(); }

        /** Accepts full names and the n/s/e/w shortcuts; returns null if the text is not a direction. */
        public static Direction parse(String text) {
            String t = text.trim().toLowerCase();
            switch (t) {
                case "n": return NORTH;
                case "s": return SOUTH;
                case "e": return EAST;
                case "w": return WEST;
                case "u": return UP;
                case "d": return DOWN;
                default:
                    for (Direction d : values()) {
                        if (d.label().equals(t)) return d;
                    }
                    return null;
            }
        }
    }

    private final String id;
    private final RoomType type;
    private final String name;
    private final String description;
    private final String fixedItem;
    private final boolean random;

    private final Map<Direction, Room> exits = new EnumMap<>(Direction.class);
    private final List<Item> items = new ArrayList<>();
    private Monster monster;
    private Puzzle puzzle;

    public Room(String id, RoomType type, String name, String description, String fixedItem, boolean random) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.fixedItem = fixedItem;
        this.random = random;
    }

    /** A new empty room from this template; {n} in the name becomes the room number. */
    public Room spawn(int number) {
        return new Room(id, type, name.replace("{n}", String.valueOf(number)), description, fixedItem, random);
    }

    public String getId() { return id; }
    public RoomType getType() { return type; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getFixedItem() { return fixedItem; }
    public boolean isRandom() { return random; }

    public Map<Direction, Room> getExits() { return exits; }
    public Room getExit(Direction direction) { return exits.get(direction); }
    public void addExit(Direction direction, Room room) { exits.put(direction, room); }

    /** Connects two rooms in both directions. */
    public void link(Direction direction, Room other) {
        addExit(direction, other);
        other.addExit(direction.opposite(), this);
    }

    public Monster getMonster() { return monster; }
    public void setMonster(Monster monster) { this.monster = monster; }
    public Puzzle getPuzzle() { return puzzle; }
    public void setPuzzle(Puzzle puzzle) { this.puzzle = puzzle; }
    public List<Item> getItems() { return items; }

    /** Swaps whatever is here for a different item, used to place a section's unique weapon. */
    public void replaceItem(Item item) {
        items.clear();
        items.add(item);
    }

    public Item removeItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                items.remove(item);
                return item;
            }
        }
        return null;
    }

    /** Extra lines shown when the player looks around, e.g. items, a monster or a riddle. */
    public List<String> describeContents() {
        List<String> lines = new ArrayList<>();
        for (Item item : items) {
            lines.add("Item here: " + item.getName() + " (" + item.getDescription() + ")");
        }
        if (monster != null && !monster.isDead()) {
            lines.add("DANGER: " + monster.getName() + " is here! (HP " + monster.getHealth() + ")");
        }
        if (puzzle != null && !puzzle.isSolved()) lines.add("Riddle: " + puzzle.getQuestion());
        return lines;
    }

    /** True while a living monster stops the player from leaving or taking items. */
    public boolean isBlocking() { return monster != null && !monster.isDead(); }

    /** True if entering this room ends the game with a win. */
    public boolean isGoal() { return type == RoomType.EXIT; }
}
