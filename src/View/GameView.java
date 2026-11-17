package view; // everything the player sees is in this package

import java.util.ArrayList; // list of messages
import java.util.Scanner; // reads what the player types

import model.Direction; // the four directions
import model.Floor; // a floor
import model.Inventory; // the inventory
import model.Item; // an item
import model.Monster; // a monster
import model.Player; // the player
import model.Puzzle; // a puzzle
import model.Room; // a room
import model.Weapon; // a weapon

public class GameView { // prints stuff and reads input, no game rules in here
    private Scanner keyboard = new Scanner(System.in); // reads from the keyboard

    public String getCommand() { // shows the prompt and reads a command, null if input ended
        System.out.print("> "); // the prompt
        if (keyboard.hasNextLine()) { // there is more input
            return keyboard.nextLine().trim(); // return the line
        }
        return null; // no more input
    }

    public String ask(String question) { // asks a question and returns the answer
        System.out.print(question); // show the question
        if (keyboard.hasNextLine()) { // there is more input
            return keyboard.nextLine().trim(); // return the answer
        }
        return ""; // no more input
    }

    public void print(String message) { // prints one message
        System.out.println(message); // print it
    }

    public void printAll(ArrayList<String> messages) { // prints a list of messages
        for (String message : messages) { // go through the list
            System.out.println(message); // print each one
        }
    }

    public void showWelcome() { // first screen of the game
        System.out.println("=== HORROR HOTEL ==="); // title
        System.out.println("You wake up on the 10th floor of an abandoned hotel with no memory of how you got here."); // story
        System.out.println("The only way out is the ground floor, but the stairway doors are locked."); // story
        System.out.println("Clear every monster and puzzle room on a floor to get its key, then go down."); // goal
        System.out.println(); // blank line
        showCommands(); // show the commands
    }

    public void showCommands() { // list of commands
        System.out.println("Available Commands:"); // heading
        System.out.println("  north, east, south, west (or n, e, s, w) - move to another room"); // move
        System.out.println("  map             - show the floor map"); // map
        System.out.println("  attack          - attack the monster with your equipped weapon"); // attack
        System.out.println("  use <slot>      - use a potion or device, like: use C"); // use
        System.out.println("  flee            - try to run from a fight"); // flee
        System.out.println("  pickup          - pick up an item in the room"); // pickup
        System.out.println("  drop <slot>     - drop an item"); // drop
        System.out.println("  equip <slot>    - equip a weapon, like: equip B"); // equip
        System.out.println("  view inventory  - show your 5 slots"); // inventory
        System.out.println("  close inventory - close the inventory"); // close inventory
        System.out.println("  inspect <item>  - look at the room or at an item"); // inspect
        System.out.println("  solve <answer>  - answer the puzzle in the room"); // solve
        System.out.println("  hint            - get a hint"); // hint
        System.out.println("  save            - save and exit"); // save
        System.out.println("  commands        - show this list"); // commands
        System.out.println("  quit            - exit without saving"); // quit
    }

    public void showFloor(int number) { // announces a new floor
        System.out.println(); // blank line
        System.out.println("---------- FLOOR " + number + " ----------"); // banner
    }

    public void showRoom(int floorNumber, Room room, boolean wasVisited) { // describes the room you are in
        System.out.println(); // blank line
        System.out.println("Floor " + floorNumber + " - " + room.getName() + " [" + room.getId() + "]"); // room title
        if (wasVisited) { // been here before
            System.out.println("(This room looks familiar. Room visited.)"); // remind the player
        }
        System.out.println("Description: " + room.getDescription()); // room description
        printExits(room); // show the exits
    }

    public void showRoomDetails(Room room) { // inspect with nothing typed after it
        System.out.println(room.getDescription()); // room description
        if (room.getItems().size() == 0) { // no items
            System.out.println("There are no items here."); // say so
        } else { // has items
            System.out.println("Items here:"); // heading
            for (Item item : room.getItems()) { // each item
                System.out.println("  - " + item.getName()); // show the name
            }
        }
        if (room.getMonster() != null && !room.isCleared()) { // a monster is alive here
            System.out.println("A monster is here: " + room.getMonster().getName()); // warn the player
        }
        if (room.getPuzzle() != null && !room.getPuzzle().isSolved()) { // an unsolved puzzle
            showPuzzle(room.getPuzzle()); // show the puzzle
        }
        printExits(room); // show the exits
    }

    private void printExits(Room room) { // prints "Exits: North, East"
        String text = "Exits:"; // start of the line
        for (Direction dir : Direction.values()) { // check all 4 directions
            if (room.getExit(dir) != null) { // there is a door that way
                text = text + " " + dir.label(); // add it to the line
            }
        }
        System.out.println(text); // print the line
    }

    public void showItem(Item item) { // inspect for an item
        System.out.println(item.getName() + " (" + item.getId() + ")"); // name and id
        System.out.println(item.getDescription()); // description
        if (item instanceof Weapon) { // weapons also show their condition
            System.out.println("Condition: " + ((Weapon) item).getStatus()); // durability or arrows
        }
    }

    public void showInventory(Inventory inventory, Weapon equipped) { // shows all 5 slots
        System.out.println("INVENTORY"); // heading
        for (int i = 0; i < Inventory.SIZE; i++) { // each slot
            char letter = (char) ('A' + i); // A, B, C, D, E
            Item item = inventory.get(i); // item in the slot
            if (item == null) { // empty slot
                System.out.println("  " + letter + ". (empty)"); // show empty
            } else { // has an item
                String line = "  " + letter + ". " + item.getName(); // letter and name
                if (item instanceof Weapon) line = line + " [" + ((Weapon) item).getStatus() + "]"; // weapon condition
                if (item == equipped) line = line + " (equipped)"; // mark the equipped weapon
                System.out.println(line); // print the line
            }
        }
    }

    public void showMap(Floor floor, Room current) { // draws the rooms in a grid
        int maxRow = 0; // biggest row number
        int maxCol = 0; // biggest column number
        for (Room room : floor.getRooms()) { // find the size of the grid
            if (room.getRow() > maxRow) maxRow = room.getRow(); // new biggest row
            if (room.getCol() > maxCol) maxCol = room.getCol(); // new biggest column
        }
        System.out.println("MAP - Floor " + floor.getNumber()); // heading
        for (int r = 0; r <= maxRow; r++) { // each row of the grid
            String line = "  "; // the line we are building
            for (int c = 0; c <= maxCol; c++) { // each column of the grid
                String symbol = "   "; // blank if there is no room here
                for (Room room : floor.getRooms()) { // look for a room at this spot
                    if (room.getRow() == r && room.getCol() == c) { // found it
                        symbol = "[" + mapLetter(room, current) + "]"; // box with a letter in it
                    }
                }
                line = line + symbol + " "; // add it to the line
            }
            System.out.println(line); // print the row
        }
        System.out.println("@ you, ? not visited yet, S start, I item, M monster, P puzzle, E empty, B boss, > stairs, X exit"); // legend
    }

    private String mapLetter(Room room, Room current) { // letter shown for a room on the map
        if (room == current) return "@"; // where the player is
        if (!room.isVisited()) return "?"; // haven't been there
        switch (room.getType()) { // letter for the room type
            case START: return "S"; // start
            case STAIRS: return ">"; // stairs
            case ITEM: return "I"; // item room
            case MONSTER: return "M"; // monster room
            case PUZZLE: return "P"; // puzzle room
            case BOSS: return "B"; // boss room
            case EXIT: return "X"; // exit
            default: return "E"; // empty room
        }
    }

    public void showPuzzle(Puzzle puzzle) { // shows the puzzle and tries left
        System.out.println(puzzle.getText()); // the puzzle text
        System.out.println("Attempts remaining: " + puzzle.getAttemptsLeft()); // tries left
    }

    public void showFightStatus(Player player, Monster monster) { // health of both fighters
        System.out.println("[Your HP: " + player.getHp() + "/" + player.getMaxHp() + "]  [" + monster.getName() + " HP: " + monster.getHp() + "/" + monster.getMaxHp() + "]"); // both hp
        System.out.println("What do you do? attack, use <slot>, or flee"); // options
    }

    public void showDeath() { // shown when the player dies
        System.out.println(); // blank line
        System.out.println("*** YOU DIED ***"); // headline
        System.out.println("You wake up in this floor's start room with full health."); // what happens
        System.out.println("The floor was reset: monsters are back and puzzles are locked again. You keep your items."); // what happens
    }

    public void showWin() { // shown when the player beats the game
        System.out.println(); // blank line
        System.out.println("=== YOU ESCAPED THE HOTEL! YOU WIN! ==="); // headline
    }
}
