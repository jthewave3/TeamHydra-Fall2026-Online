package Model; // all the game data classes live in this package

import java.util.ArrayList; // list of rooms

public class Floor { // one level of the hotel
    private int number; // floor number, 10 down to 1
    private ArrayList<Room> rooms = new ArrayList<Room>(); // every room on this floor
    private Room start; // room where the player arrives
    private Room exit; // the stairs (or the hotel exit on floor 1)
    private boolean keyGiven; // true once the player got the floor key

    public Floor(int number) { // constructor
        this.number = number; // save the floor number
    }

    public void addRoom(Room room) { // adds a room to the floor
        rooms.add(room); // put it in the list
        if (room.getType() == RoomType.START) start = room; // remember the start room
        if (room.getType() == RoomType.STAIRS || room.getType() == RoomType.EXIT) exit = room; // remember the exit room
    }

    public boolean isCleared() { // true when every monster and puzzle room is cleared
        for (Room room : rooms) { // check each room
            if (!room.isDone()) return false; // found one that isn't done yet
        }
        return true; // all done
    }

    public void reset() { // used when the player dies, monsters come back and puzzles lock again
        for (Room room : rooms) { // go through each room
            room.setCleared(false); // needs to be cleared again
            if (room.getMonster() != null) room.getMonster().resetHealth(); // monster back to full hp
            if (room.getPuzzle() != null) room.getPuzzle().reset(); // puzzle back to unsolved
        }
        keyGiven = false; // the key has to be earned again
    }

    public void playerMoved(Room newRoom) { // lets locked puzzles know the player went to another room
        for (Room room : rooms) { // check each room
            if (room != newRoom && room.getPuzzle() != null) { // a different room with a puzzle
                room.getPuzzle().roomVisited(); // count the visit
            }
        }
    }

    public Room findRoom(String id) { // finds a room by its id, null if there isn't one
        for (Room room : rooms) { // check each room
            if (room.getId().equals(id)) return room; // found it
        }
        return null; // no room with that id
    }

    public int getNumber() { // gets the floor number
        return number; // send it back
    }

    public ArrayList<Room> getRooms() { // gets all the rooms
        return rooms; // send it back
    }

    public Room getStart() { // gets the start room
        return start; // send it back
    }

    public Room getExit() { // gets the exit room
        return exit; // send it back
    }

    public boolean isKeyGiven() { // gets key flag
        return keyGiven; // send it back
    }

    public void setKeyGiven(boolean keyGiven) { // sets key flag
        this.keyGiven = keyGiven; // save it
    }
}
