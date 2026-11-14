package Model; // all the game data classes live in this package

public enum RoomType { // the kinds of rooms on a floor
    START("Hallway"), // where you arrive on each floor
    STAIRS("Stairwell"), // the way down to the next floor
    ITEM("Supply Room"), // has items to pick up
    MONSTER("Haunted Suite"), // has a monster
    PUZZLE("Locked Study"), // has a puzzle
    EMPTY("Empty Room"), // nothing in it
    BOSS("Manager's Office"), // the boss on floor 1
    EXIT("Hotel Exit"); // the way out after the boss

    private String name; // name that gets shown to the player

    RoomType(String name) { // constructor for each constant above
        this.name = name; // save the name
    }

    public String getName() { // gets the name
        return name; // send it back
    }
}
