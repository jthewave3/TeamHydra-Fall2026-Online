package model; // all the game data classes live in this package

import java.io.File; // the save file
import java.io.PrintWriter; // writes text to the file
import java.util.Scanner; // reads text from the file

public class SaveManager { // saves and loads the game using save.txt
    private static String FILE_NAME = "save.txt"; // name of the save file

    public static boolean saveExists() { // true if there is a save file
        return new File(FILE_NAME).exists(); // check for the file
    }

    // file format, one thing per line:
    //   seed, floor number, room id, hp, equipped slot (-1 if none),
    //   5 inventory lines ("EMPTY" or "id uses"),
    //   then one line per room: "roomId visited cleared items" (items look like A02/20,A07/0 or - for none)
    public static void save(GameState state) throws Exception { // writes the game to the file
        PrintWriter out = new PrintWriter(FILE_NAME); // open the file
        Player player = state.getPlayer(); // the player
        Floor floor = state.getFloor(); // the floor
        out.println(state.getSeed()); // seed
        out.println(floor.getNumber()); // floor number
        out.println(state.getCurrent().getId()); // room id
        out.println(player.getHp()); // health
        out.println(player.getInventory().indexOf(player.getEquipped())); // equipped slot, -1 if none
        for (int i = 0; i < Inventory.SIZE; i++) { // each inventory slot
            Item item = player.getInventory().get(i); // item in the slot
            if (item == null) { // empty slot
                out.println("EMPTY"); // write EMPTY
            } else { // has an item
                out.println(itemToText(item)); // write the item
            }
        }
        for (Room room : floor.getRooms()) { // each room on the floor
            String items = ""; // items in the room as text
            for (Item item : room.getItems()) { // each item in the room
                if (!items.equals("")) items = items + ","; // comma between items
                items = items + itemToText(item); // add the item
            }
            if (items.equals("")) items = "-"; // dash means no items
            int visited = room.isVisited() ? 1 : 0; // 1 or 0 for visited
            int cleared = room.isCleared() ? 1 : 0; // 1 or 0 for cleared
            out.println(room.getId() + " " + visited + " " + cleared + " " + items); // write the room line
        }
        out.close(); // close the file
    }

    public static GameState load() { // reads the file, returns null if something is wrong with it
        try { // reading might fail if the file is messed up
            Scanner in = new Scanner(new File(FILE_NAME)); // open the file
            long seed = Long.parseLong(in.nextLine().trim()); // seed
            int floorNumber = Integer.parseInt(in.nextLine().trim()); // floor number
            String roomId = in.nextLine().trim(); // room id
            int hp = Integer.parseInt(in.nextLine().trim()); // health
            int equipped = Integer.parseInt(in.nextLine().trim()); // equipped slot
            GameState state = new GameState(seed); // new game with the same seed
            state.loadFloor(floorNumber); // make the saved floor
            Player player = state.getPlayer(); // the player
            player.setHp(hp); // set health
            for (int i = 0; i < Inventory.SIZE; i++) { // each inventory slot
                String line = in.nextLine().trim(); // read the slot line
                if (!line.equals("EMPTY")) { // there is an item
                    player.getInventory().set(i, textToItem(line)); // rebuild it
                }
            }
            if (equipped >= 0) { // a weapon was equipped
                player.equip((Weapon) player.getInventory().get(equipped)); // equip it again
            }
            Floor floor = state.getFloor(); // the floor
            while (in.hasNextLine()) { // read the room lines
                String[] parts = in.nextLine().trim().split(" "); // roomId visited cleared items
                Room room = floor.findRoom(parts[0]); // find the room
                room.setVisited(parts[1].equals("1")); // visited
                room.setCleared(parts[2].equals("1")); // cleared
                if (room.isCleared() && room.getPuzzle() != null) { // a solved puzzle
                    room.getPuzzle().setSolved(true); // mark it solved
                }
                room.getItems().clear(); // throw away the items it started with
                if (!parts[3].equals("-")) { // the room has items
                    for (String text : parts[3].split(",")) { // each item
                        room.getItems().add(textToItem(text)); // rebuild it
                    }
                }
            }
            in.close(); // close the file
            if (floor.isCleared()) floor.setKeyGiven(true); // if the floor was cleared the player already has the key
            state.setCurrent(floor.findRoom(roomId)); // put the player back in their room
            return state; // done
        } catch (Exception e) { // anything went wrong
            return null; // tell the caller the save was no good
        }
    }

    private static String itemToText(Item item) { // item as text like A02/20
        int uses = 0; // only weapons have uses
        if (item instanceof Weapon) uses = ((Weapon) item).getUses(); // get the uses left
        return item.getId() + "/" + uses; // id and uses
    }

    private static Item textToItem(String text) { // text like A02/20 back into an item
        String[] parts = text.split("/"); // id and uses
        Item item = Item.make(parts[0]); // make a new item
        if (item instanceof Weapon) { // weapons remember their uses
            ((Weapon) item).setUses(Integer.parseInt(parts[1])); // set the uses
        }
        return item; // done
    }
}
