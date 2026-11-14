package Model; // all the game data classes live in this package

import java.util.Random; // random numbers for combat

public class GameState { // everything about the game right now (this is what gets saved)
    private long seed; // number used to make the floors, same seed gives the same floors
    private FloorGenerator generator = new FloorGenerator(); // makes the floors
    private Player player = new Player(); // the player
    private Random rand = new Random(); // random numbers for combat
    private Floor floor; // floor the player is on
    private Room current; // room the player is in
    private Room previous; // room the player came from
    private Combat combat; // current fight, null if not fighting
    private boolean gameOver; // true when the game should stop

    public GameState(long seed) { // starts a new game on floor 10
        this.seed = seed; // save the seed
        loadFloor(10); // make floor 10
        current.setVisited(true); // the first room counts as visited
    }

    public void loadFloor(int number) { // makes a floor and puts the player in its start room
        floor = generator.generate(number, seed); // build the floor
        current = floor.getStart(); // player starts in the start room
        previous = null; // came from nowhere
        combat = null; // not fighting
    }

    public void goDown() { // moves the player to the next floor down
        loadFloor(floor.getNumber() - 1); // build the floor below
        current.setVisited(true); // start room counts as visited
    }

    public boolean enterRoom(Room room) { // moves the player to a room, returns true if they were there before
        boolean wasVisited = room.isVisited(); // check before we change it
        previous = current; // old room becomes the previous room
        current = room; // new room is the current room
        room.setVisited(true); // mark it visited
        return wasVisited; // tell the caller
    }

    public void playerDied() { // full health, floor resets, back to the start room (items are kept)
        player.fullHeal(); // back to 100 hp
        floor.reset(); // monsters come back and puzzles lock
        current = floor.getStart(); // go to the start room
        previous = null; // forget the previous room
        combat = null; // not fighting anymore
    }

    public long getSeed() { // gets the seed
        return seed; // send it back
    }

    public Player getPlayer() { // gets the player
        return player; // send it back
    }

    public Floor getFloor() { // gets the floor
        return floor; // send it back
    }

    public Room getCurrent() { // gets the current room
        return current; // send it back
    }

    public void setCurrent(Room room) { // sets the current room (used when loading)
        current = room; // save it
    }

    public Room getPrevious() { // gets the previous room
        return previous; // send it back
    }

    public Combat getCombat() { // gets the fight
        return combat; // send it back
    }

    public void setCombat(Combat combat) { // starts or ends a fight
        this.combat = combat; // save it
    }

    public Random getRand() { // gets the random object
        return rand; // send it back
    }

    public boolean isGameOver() { // gets game over flag
        return gameOver; // send it back
    }

    public void setGameOver(boolean gameOver) { // sets game over flag
        this.gameOver = gameOver; // save it
    }
}
