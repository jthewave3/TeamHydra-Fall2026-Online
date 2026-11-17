package model; // all the game data classes live in this package

import java.util.ArrayList; // list for the items in the room

public class Room { // one room on a floor
    private String id; // room id like F10-A
    private RoomType type; // what kind of room it is
    private String description; // text shown when you walk in
    private int row; // row on the map grid
    private int col; // column on the map grid
    private Room[] exits = new Room[4]; // room in each direction, in the order of the Direction enum
    private ArrayList<Item> items = new ArrayList<Item>(); // items on the floor of the room
    private Monster monster; // monster in the room, null if none
    private Puzzle puzzle; // puzzle in the room, null if none
    private boolean visited; // true once the player has been here
    private boolean cleared; // true once the monster is dead or the puzzle is solved

    public Room(String id, RoomType type, String description, int row, int col) { // constructor
        this.id = id; // save the id
        this.type = type; // save the type
        this.description = description; // save the description
        this.row = row; // save the row
        this.col = col; // save the column
    }

    public void connect(Direction dir, Room other) { // joins two rooms together
        exits[dir.ordinal()] = other; // this room leads to the other one
        other.exits[dir.opposite().ordinal()] = this; // and the other one leads back
    }

    public Room getExit(Direction dir) { // room in that direction, null if there is no door
        return exits[dir.ordinal()]; // look it up
    }

    public boolean mustBeCleared() { // monster, puzzle and boss rooms have to be cleared to finish the floor
        return type == RoomType.MONSTER || type == RoomType.PUZZLE || type == RoomType.BOSS; // those three
    }

    public boolean isDone() { // true if this room isn't blocking the floor from being cleared
        return !mustBeCleared() || cleared; // other rooms are always done
    }

    public boolean hasItemId(String itemId) { // true if the room already has this item
        for (Item item : items) { // look at each item
            if (item.getId().equals(itemId)) return true; // same id
        }
        return false; // not found
    }

    public String getId() { // gets the id
        return id; // send it back
    }

    public RoomType getType() { // gets the type
        return type; // send it back
    }

    public String getName() { // gets the name from the type
        return type.getName(); // send it back
    }

    public String getDescription() { // gets the description
        return description; // send it back
    }

    public int getRow() { // gets the row
        return row; // send it back
    }

    public int getCol() { // gets the column
        return col; // send it back
    }

    public ArrayList<Item> getItems() { // gets the items in the room
        return items; // send it back
    }

    public Monster getMonster() { // gets the monster
        return monster; // send it back
    }

    public void setMonster(Monster monster) { // puts a monster in the room
        this.monster = monster; // save it
    }

    public Puzzle getPuzzle() { // gets the puzzle
        return puzzle; // send it back
    }

    public void setPuzzle(Puzzle puzzle) { // puts a puzzle in the room
        this.puzzle = puzzle; // save it
    }

    public boolean isVisited() { // gets visited flag
        return visited; // send it back
    }

    public void setVisited(boolean visited) { // sets visited flag
        this.visited = visited; // save it
    }

    public boolean isCleared() { // gets cleared flag
        return cleared; // send it back
    }

    public void setCleared(boolean cleared) { // sets cleared flag
        this.cleared = cleared; // save it
    }
}
