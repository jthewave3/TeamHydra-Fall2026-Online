package controller; // connects what the player types to the model and the view

import java.util.ArrayList; // list of messages

import model.Combat; // a fight
import model.Consumable; // potions and the flash bomb
import model.Direction; // the four directions
import model.Floor; // a floor
import model.GameState; // all the game data
import model.Inventory; // the inventory
import model.Item; // an item
import model.Monster; // a monster
import model.Player; // the player
import model.Puzzle; // a puzzle
import model.Room; // a room
import model.RoomType; // room types
import model.SaveManager; // saving and loading
import model.Weapon; // a weapon
import view.GameView; // the view

public class GameController { // runs the game loop and handles every command
    private GameView view; // used for everything that gets printed
    private GameState state; // the game data

    public GameController(GameView view) { // constructor
        this.view = view; // save the view
    }

    public void run() { // starts the game and keeps it going until it ends
        view.showWelcome(); // show the intro
        boolean loaded = false; // true if we loaded a save
        if (SaveManager.saveExists()) { // there is a save file
            String answer = view.ask("A saved game was found. Load it? (y/n): "); // ask the player
            if (answer.toLowerCase().startsWith("y")) { // they said yes
                state = SaveManager.load(); // try to load it
                if (state == null) { // the file was bad
                    view.print("Could not read the save file. Starting a new game."); // tell the player
                } else { // it worked
                    loaded = true; // remember that we loaded
                }
            }
        }
        if (!loaded) { // not using a save
            state = new GameState(System.currentTimeMillis()); // new game, the time works as a random seed
        }
        view.showFloor(state.getFloor().getNumber()); // show the floor number
        if (loaded) { // continuing a saved game
            view.print("Game loaded."); // tell the player
            enterRoom(state.getCurrent(), true); // show the room
        } else { // new game
            view.showRoom(state.getFloor().getNumber(), state.getCurrent(), false); // show the first room
        }
        while (!state.isGameOver()) { // main game loop
            String line = view.getCommand(); // read a command
            if (line == null) break; // input ended so stop
            handleCommand(line); // do what they typed
        }
    }

    private void handleCommand(String line) { // figures out what the player typed
        if (line.equals("")) return; // nothing typed
        String[] parts = line.split(" ", 2); // first word and everything after it
        String command = parts[0].toLowerCase(); // the command word
        String arg = ""; // the rest of the line
        if (parts.length > 1) arg = parts[1].trim(); // there was more after the command
        Direction dir = Direction.fromText(command); // check if it was a direction
        if (dir != null) { // it was
            move(dir); // go that way
            return; // done
        }
        switch (command) { // pick what to do
            case "map": // show the map
                view.showMap(state.getFloor(), state.getCurrent()); // draw it
                break; // done with this command
            case "attack": // attack
                attack(); // do it
                break; // done with this command
            case "use": // use an item
                useItem(arg); // do it
                break; // done with this command
            case "flee": // run away
                flee(); // do it
                break; // done with this command
            case "pickup": // pick up an item
                pickup(arg); // do it
                break; // done with this command
            case "drop": // drop an item
                drop(arg); // do it
                break; // done with this command
            case "equip": // equip a weapon
                equip(arg); // do it
                break; // done with this command
            case "view": // view inventory
                view.showInventory(state.getPlayer().getInventory(), state.getPlayer().getEquipped()); // show it
                break; // done with this command
            case "close": // close inventory
                view.print("Inventory closed."); // just a message
                break; // done with this command
            case "inspect": // look at something
                inspect(arg); // do it
                break; // done with this command
            case "solve": // answer a puzzle
                solve(arg); // do it
                break; // done with this command
            case "hint": // get a hint
                view.print(getHint()); // show the hint
                break; // done with this command
            case "commands": // list the commands
                view.showCommands(); // show them
                break; // done with this command
            case "save": // save and exit
                save(); // do it
                break; // done with this command
            case "quit": // exit without saving
                view.print("Bye!"); // goodbye
                state.setGameOver(true); // stop the game
                break; // done with this command
            default: // anything else
                view.print("Invalid command"); // not a real command
        }
    }

    private boolean fighting() { // true if there is a fight going on
        return state.getCombat() != null; // there is a Combat object
    }

    private boolean blockedByFight() { // some commands can't be used in a fight
        if (fighting()) { // in a fight
            view.print("You can't do that during a fight! Attack, use an item, or flee."); // tell the player
            return true; // blocked
        }
        return false; // fine to continue
    }

    private void move(Direction dir) { // moves the player to another room
        if (blockedByFight()) return; // can't walk away from a fight
        Room next = state.getCurrent().getExit(dir); // room in that direction
        if (next == null) { // no door that way
            view.print("You can't move in that direction."); // tell the player
            return; // stay here
        }
        Floor floor = state.getFloor(); // current floor
        boolean isExit = next.getType() == RoomType.STAIRS || next.getType() == RoomType.EXIT; // stairs or hotel exit
        if (isExit && !floor.isCleared()) { // floor isn't cleared yet
            view.print("The way down is locked. Clear every monster and puzzle room to get the floor key."); // tell the player
            return; // stay here
        }
        if (next.getPuzzle() != null && next.getPuzzle().isLocked()) { // puzzle room that kicked the player out
            view.print("The door won't open. Visit at least 2 other rooms first."); // tell the player
            return; // stay here
        }
        boolean wasVisited = state.enterRoom(next); // move there
        floor.playerMoved(next); // lets locked puzzles count this visit
        enterRoom(next, wasVisited); // show the room and start whatever is in it
    }

    private void enterRoom(Room room, boolean wasVisited) { // what happens when the player gets to a room
        if (room.getType() == RoomType.STAIRS) { // going down
            view.print("You use the stairway key to unlock the door and go down."); // story
            state.goDown(); // next floor
            view.showFloor(state.getFloor().getNumber()); // show the floor number
            view.showRoom(state.getFloor().getNumber(), state.getCurrent(), false); // show the start room
            return; // done
        }
        view.showRoom(state.getFloor().getNumber(), room, wasVisited); // describe the room
        if (room.getType() == RoomType.EXIT) { // got out of the hotel
            view.showWin(); // you win
            state.setGameOver(true); // stop the game
            return; // done
        }
        if (room.getMonster() != null && !room.isCleared()) { // there is a living monster
            Monster monster = room.getMonster(); // the monster
            state.setCombat(new Combat(state.getPlayer(), monster, state.getRand())); // start a fight
            view.print(monster.getEnterText()); // show the enter text
            view.showFightStatus(state.getPlayer(), monster); // show health
        } else if (room.getPuzzle() != null) { // there is a puzzle
            if (room.isCleared()) { // already solved
                view.print("This puzzle is already solved."); // tell the player
            } else { // not solved yet
                view.showPuzzle(room.getPuzzle()); // show it
            }
        }
    }

    private void attack() { // attack command
        if (!fighting()) { // no monster
            view.print("There's nothing to attack here."); // tell the player
            return; // done
        }
        afterFightAction(state.getCombat().attack()); // attack and see what happened
    }

    private void flee() { // flee command
        if (!fighting()) { // no monster
            view.print("There is nothing to flee from."); // tell the player
            return; // done
        }
        afterFightAction(state.getCombat().flee()); // try to flee and see what happened
    }

    private void afterFightAction(ArrayList<String> log) { // prints what happened and checks if the fight is over
        view.printAll(log); // print the messages
        Combat combat = state.getCombat(); // the fight
        Player player = state.getPlayer(); // the player
        if (!player.isAlive()) { // player died
            die(); // handle it
            return; // done
        }
        if (!combat.isOver()) { // still fighting
            view.showFightStatus(player, combat.getMonster()); // show health
            return; // done
        }
        state.setCombat(null); // fight is over
        if (combat.isWon()) { // monster is dead
            state.getCurrent().setCleared(true); // room is cleared
            checkFloorCleared(); // maybe the whole floor is cleared
        } else if (combat.hasFled()) { // ran away
            Room back = state.getPrevious(); // go back to the room we came from
            if (back == null) back = state.getFloor().getStart(); // if there isn't one use the start room
            boolean wasVisited = state.enterRoom(back); // move there
            enterRoom(back, wasVisited); // show it
        }
    }

    private void checkFloorCleared() { // gives the key when the whole floor is cleared
        Floor floor = state.getFloor(); // the floor
        if (floor.isCleared() && !floor.isKeyGiven()) { // just got cleared
            floor.setKeyGiven(true); // player gets the key (it doesn't take an inventory slot)
            if (floor.getNumber() == 1) { // boss floor
                view.print("The Manager is gone. The exit is open!"); // tell the player
            } else { // normal floor
                view.print("Floor cleared! You got the Stairway Key. The stairs are unlocked."); // tell the player
            }
        }
    }

    private void die() { // player died so reset the floor
        view.showDeath(); // show the death message
        state.playerDied(); // reset everything
        view.showRoom(state.getFloor().getNumber(), state.getCurrent(), true); // show the start room
    }

    private int chooseSlot(String arg) { // figures out which inventory slot the player means
        Inventory inv = state.getPlayer().getInventory(); // the inventory
        if (arg.equals("")) { // they didn't say which one
            view.showInventory(inv, state.getPlayer().getEquipped()); // show the slots
            arg = view.ask("Choose an item: "); // ask
        }
        return inv.findSlot(arg); // slot number, -1 if not found
    }

    private void pickup(String arg) { // pickup command
        if (blockedByFight()) return; // not during a fight
        Room room = state.getCurrent(); // current room
        if (room.getItems().size() == 0) { // nothing here
            view.print("There is no item to pick up here."); // tell the player
            return; // done
        }
        Item chosen = null; // the item the player wants
        if (!arg.equals("")) { // they named an item
            for (Item item : room.getItems()) { // look for it
                if (item.getName().toLowerCase().contains(arg.toLowerCase())) chosen = item; // name matches
            }
        } else if (room.getItems().size() == 1) { // only one item so just take it
            chosen = room.getItems().get(0); // that one
        } else { // more than one item so ask
            for (int i = 0; i < room.getItems().size(); i++) { // list them
                view.print("  " + (i + 1) + ". " + room.getItems().get(i).getName()); // numbered list
            }
            String answer = view.ask("Which item (number)? "); // ask
            try { // the answer might not be a number
                int number = Integer.parseInt(answer); // turn it into a number
                if (number >= 1 && number <= room.getItems().size()) { // valid choice
                    chosen = room.getItems().get(number - 1); // that item
                }
            } catch (NumberFormatException e) { // not a number
                chosen = null; // no item chosen
            }
        }
        if (chosen == null) { // couldn't find it
            view.print("There is no item to pick up here."); // tell the player
            return; // done
        }
        Inventory inv = state.getPlayer().getInventory(); // the inventory
        if (chosen instanceof Weapon && inv.hasId(chosen.getId())) { // already has this weapon
            view.print("You already have a " + chosen.getName() + "."); // tell the player
        } else if (inv.isFull()) { // no room
            view.print("Your inventory is full. Drop something first."); // tell the player
        } else { // it fits
            inv.add(chosen); // put it in the inventory
            room.getItems().remove(chosen); // take it out of the room
            view.print("You picked up the " + chosen.getName() + "."); // tell the player
        }
    }

    private void drop(String arg) { // drop command
        if (blockedByFight()) return; // not during a fight
        Inventory inv = state.getPlayer().getInventory(); // the inventory
        int slot = chooseSlot(arg); // which slot
        if (slot == -1 || inv.get(slot) == null) { // nothing there
            view.print("You do not have that item."); // tell the player
            return; // done
        }
        Item item = inv.get(slot); // the item
        if (item == state.getPlayer().getEquipped()) { // dropping the equipped weapon
            state.getPlayer().equip(null); // nothing equipped now
        }
        inv.remove(slot); // take it out of the inventory
        state.getCurrent().getItems().add(item); // leave it in the room
        view.print(item.getName() + " has been dropped."); // tell the player
    }

    private void equip(String arg) { // equip command
        Inventory inv = state.getPlayer().getInventory(); // the inventory
        int slot = chooseSlot(arg); // which slot
        if (slot == -1 || inv.get(slot) == null) { // nothing there
            view.print("You do not have that item."); // tell the player
            return; // done
        }
        Item item = inv.get(slot); // the item
        if (item instanceof Weapon) { // it's a weapon
            state.getPlayer().equip((Weapon) item); // equip it
            view.print(item.getName() + " has been equipped."); // tell the player
        } else { // not a weapon
            view.print("Command not valid for this item.\n" + item.getName() + " cannot be equipped."); // tell the player
        }
    }

    private void useItem(String arg) { // use command
        Player player = state.getPlayer(); // the player
        Inventory inv = player.getInventory(); // the inventory
        int slot = chooseSlot(arg); // which slot
        if (slot == -1 || inv.get(slot) == null) { // nothing there
            view.print("You do not have that item."); // tell the player (doesn't use up a turn)
            return; // done
        }
        Item item = inv.get(slot); // the item
        if (!(item instanceof Consumable)) { // weapons can't be used
            view.print("Command not valid for this item.\n" + item.getName() + " cannot be used. Use equip for weapons."); // tell the player
            return; // done
        }
        Consumable potion = (Consumable) item; // treat it as a consumable
        if (!fighting()) { // not in a fight
            if (potion.getEffect().equals("heal")) { // healing works anywhere
                inv.remove(slot); // use it up
                player.heal(player.getMaxHp() / 4); // heal 25%
                view.print("You drink the potion and heal 25%. Your HP: " + player.getHp()); // tell the player
            } else { // everything else needs a fight
                view.print("That only works during a fight."); // tell the player
            }
            return; // done
        }
        inv.remove(slot); // use it up
        afterFightAction(state.getCombat().useItem(potion)); // use it in the fight
    }

    private void inspect(String arg) { // inspect command
        if (arg.equals("")) { // no item named so look at the room
            view.showRoomDetails(state.getCurrent()); // show the room
            return; // done
        }
        Inventory inv = state.getPlayer().getInventory(); // the inventory
        int slot = inv.findSlot(arg); // check the inventory first
        if (slot != -1 && inv.get(slot) != null) { // found it
            view.showItem(inv.get(slot)); // show it
            return; // done
        }
        for (Item item : state.getCurrent().getItems()) { // check the room
            if (item.getName().toLowerCase().contains(arg.toLowerCase())) { // name matches
                view.showItem(item); // show it
                return; // done
            }
        }
        view.print("That item is not available to inspect"); // not found
    }

    private void solve(String arg) { // solve command
        if (blockedByFight()) return; // not during a fight
        Room room = state.getCurrent(); // current room
        Puzzle puzzle = room.getPuzzle(); // puzzle in the room
        if (puzzle == null) { // no puzzle
            view.print("There is no puzzle here."); // tell the player
            return; // done
        }
        if (puzzle.isSolved()) { // already solved
            view.print("You already solved this puzzle."); // tell the player
            return; // done
        }
        if (arg.equals("")) { // no answer given
            view.print("Type your answer after solve, like: solve key"); // tell the player
            return; // done
        }
        if (puzzle.checkAnswer(arg)) { // right answer
            room.setCleared(true); // room is cleared
            view.print("You solved the puzzle! Something clicks into place. You may move on."); // tell the player
            checkFloorCleared(); // maybe the floor is cleared
            return; // done
        }
        Player player = state.getPlayer(); // the player
        player.takeDamage(3); // wrong answers cost 3 hp
        view.print("Wrong. The room grows colder. You lose 3 HP. (Your HP: " + player.getHp() + ")"); // tell the player
        if (!player.isAlive()) { // that killed the player
            die(); // handle it
        } else if (puzzle.isLocked()) { // that was the last try
            view.print("The room pushes you back out. Visit at least 2 other rooms before coming back."); // tell the player
            Room back = state.getPrevious(); // go back to where we came from
            if (back == null) back = state.getFloor().getStart(); // if there isn't one use the start room
            state.enterRoom(back); // move there
            view.showRoom(state.getFloor().getNumber(), back, true); // show it
        } else { // still has tries
            view.print("Attempts remaining: " + puzzle.getAttemptsLeft()); // tell the player
        }
    }

    private String getHint() { // makes a hint for what the player is doing right now
        Puzzle puzzle = state.getCurrent().getPuzzle(); // puzzle in the room
        if (fighting()) { // in a fight
            return "Equip a weapon and attack. Potions and the flash bomb can help if you're losing. You can't flee from the boss."; // fight hint
        }
        if (puzzle != null && !puzzle.isSolved()) { // unsolved puzzle
            if (puzzle.getType().equals("riddle")) return "Think about things you would find in a hotel room."; // riddle hint
            if (puzzle.getType().equals("scramble")) return "Rearrange the letters. It's something you'd find in a hotel."; // scramble hint
            return "Move each coded letter back in the alphabet by the shift number."; // cipher hint
        }
        if (state.getFloor().isCleared()) { // everything is cleared
            return "This floor is clear. Head to the stairs, they are south of the hallway."; // stairs hint
        }
        return "Search the rooms carefully. Clear every monster and puzzle room to get the floor key."; // general hint
    }

    private void save() { // save command
        view.print("Saving the game..."); // tell the player
        try { // saving can fail
            SaveManager.save(state); // write the file
            view.print("Game saved successfully."); // tell the player
            view.print("Exiting the game."); // tell the player
            state.setGameOver(true); // stop the game
        } catch (Exception e) { // something went wrong
            view.print("Could not save the game."); // tell the player
        }
    }
}
