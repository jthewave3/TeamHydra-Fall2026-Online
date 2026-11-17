package model; // all the game data classes live in this package

import java.util.ArrayList; // list of room types
import java.util.Collections; // used to shuffle
import java.util.Random; // random numbers

public class FloorGenerator { // builds the floors
    // description for each floor, index 0 is floor 1 and index 9 is floor 10
    private static String[] THEMES = { // one description per floor
        "The hallway ends at a single set of double doors, heavier than any other on the way down. A brass nameplate reads \"Manager\" in handwriting that doesn't match the rest of the hotel's signage.", // floor 1
        "This floor looks like someone desperately tried to keep something trapped inside. Some doors are barricaded with furniture and loud banging comes from inside the rooms.", // floor 2
        "Strange symbols and handprints cover the walls and doors. Candles line the hallway, and some suddenly light on their own as you walk past.", // floor 3
        "Thick fog fills the entire floor, making the doors hard to see. Shadows move through the fog and strange noises come from different rooms.", // floor 4
        "Old newspapers, photographs, and hotel records are scattered across the hallway. Some mention missing guests and unexplained incidents.", // floor 5
        "Vines and roots have grown through cracks in the walls and across the floor. Bugs crawl between the plants and disappear into the rooms.", // floor 6
        "Water drips constantly from the ceiling and puddles cover the hallway. Wet footprints lead between rooms, but there is never anyone there.", // floor 7
        "Red lights flicker while smoke slips out from under some of the doors. The air smells burnt and black marks from an old fire stretch across the walls.", // floor 8
        "Deep scratches cover the walls and doors. Shattered glass covers parts of the hallway, spiders crawl along the walls, and some doors look like something tried to break through.", // floor 9
        "You wake up alone on floor 10 after a long coma, with no memory of how you got here. The floor is dark and silent, with dusty furniture and old supplies scattered around the hallway." // floor 10
    }; // end of the list

    public Floor generate(int number, long seed) { // builds one floor, the same seed always gives the same floor
        Random rand = new Random(seed + number * 1000); // random numbers for this game and floor
        if (number == 1) { // floor 1 is the boss floor
            return makeBossFloor(rand); // it has its own layout
        }
        Floor floor = new Floor(number); // empty floor
        ArrayList<RoomType> types = new ArrayList<RoomType>(); // room types for rooms A to E
        if (number == 10) { // floor 10 is always the same
            types.add(RoomType.MONSTER); // A
            types.add(RoomType.ITEM); // B
            types.add(RoomType.PUZZLE); // C
            types.add(RoomType.ITEM); // D
            types.add(RoomType.ITEM); // E
        } else { // floors 9 to 2 are random
            types.add(RoomType.ITEM); // at least one item room
            types.add(RoomType.MONSTER); // at least one monster room
            types.add(RoomType.PUZZLE); // at least one puzzle room
            RoomType[] choices = {RoomType.ITEM, RoomType.MONSTER, RoomType.PUZZLE, RoomType.EMPTY}; // what the last two can be
            types.add(choices[rand.nextInt(4)]); // fourth room
            types.add(choices[rand.nextInt(4)]); // fifth room
            Collections.shuffle(types, rand); // mix them up so every game is different
        }
        // the layout is the same on every floor:
        //   A B C
        //   D H E     (H is the hallway where you start)
        //     S       (S is the stairs)
        Room a = makeRoom(floor, "A", types.get(0), 0, 0, rand); // top left
        Room b = makeRoom(floor, "B", types.get(1), 0, 1, rand); // top middle
        Room c = makeRoom(floor, "C", types.get(2), 0, 2, rand); // top right
        Room d = makeRoom(floor, "D", types.get(3), 1, 0, rand); // middle left
        Room e = makeRoom(floor, "E", types.get(4), 1, 2, rand); // middle right
        Room hall = new Room(makeId(number, "H"), RoomType.START, THEMES[number - 1], 1, 1); // hallway
        Room stairs = new Room(makeId(number, "S"), RoomType.STAIRS, "A heavy fire door leads down to the next floor.", 2, 1); // stairs
        floor.addRoom(hall); // add the hallway
        floor.addRoom(stairs); // add the stairs
        a.connect(Direction.EAST, b); // A to B
        b.connect(Direction.EAST, c); // B to C
        a.connect(Direction.SOUTH, d); // A to D
        b.connect(Direction.SOUTH, hall); // B to hallway
        c.connect(Direction.SOUTH, e); // C to E
        d.connect(Direction.EAST, hall); // D to hallway
        hall.connect(Direction.EAST, e); // hallway to E
        hall.connect(Direction.SOUTH, stairs); // hallway to stairs
        if (number == 10) { // floor 10 has set items so the player has stuff to start with
            setItem(b, "A02"); // sword
            setItem(d, "A07"); // healing potion
            setItem(e, "A08"); // shield potion
        }
        return floor; // done
    }

    private Floor makeBossFloor(Random rand) { // floor 1: entrance, item room, boss room, exit in a line
        Floor floor = new Floor(1); // empty floor
        Room start = new Room(makeId(1, "H"), RoomType.START, THEMES[0], 3, 0); // entrance
        Room itemRoom = makeRoom(floor, "I", RoomType.ITEM, 2, 0, rand); // item room before the boss
        Room bossRoom = makeRoom(floor, "B", RoomType.BOSS, 1, 0, rand); // boss room
        Room exit = new Room(makeId(1, "X"), RoomType.EXIT, "Cold night air comes through an open doorway. The way out is finally clear.", 0, 0); // exit
        floor.addRoom(start); // add the entrance
        floor.addRoom(exit); // add the exit
        String[] weapons = {"A02", "A03", "A04"}; // the item room always has a weapon
        setItem(itemRoom, weapons[rand.nextInt(3)]); // pick one
        start.connect(Direction.NORTH, itemRoom); // entrance to item room
        itemRoom.connect(Direction.NORTH, bossRoom); // item room to boss room
        bossRoom.connect(Direction.NORTH, exit); // boss room to exit
        return floor; // done
    }

    private Room makeRoom(Floor floor, String letter, RoomType type, int row, int col, Random rand) { // makes one room and fills it
        Room room = new Room(makeId(floor.getNumber(), letter), type, describe(type), row, col); // new room
        if (type == RoomType.MONSTER) { // monster room
            room.setMonster(Monster.random(floor.getNumber(), rand)); // add a monster
        }
        if (type == RoomType.BOSS) { // boss room
            room.setMonster(Monster.makeBoss()); // add the manager
        }
        if (type == RoomType.PUZZLE) { // puzzle room
            room.setPuzzle(Puzzle.random(floor.getNumber(), rand)); // add a puzzle
        }
        if (type == RoomType.ITEM) { // item room
            int howMany = 1 + rand.nextInt(2); // 1 or 2 items
            while (room.getItems().size() < howMany) { // keep going until it has enough
                Item item = Item.randomItem(rand); // random item
                if (!room.hasItemId(item.getId())) { // no repeats in the same room
                    room.getItems().add(item); // put it in the room
                }
            }
        }
        floor.addRoom(room); // add it to the floor
        return room; // send it back
    }

    private void setItem(Room room, String itemId) { // replaces whatever is in the room with one item
        room.getItems().clear(); // empty the room
        room.getItems().add(Item.make(itemId)); // add the item
    }

    private String makeId(int floorNumber, String letter) { // room id like F09-C
        if (floorNumber < 10) return "F0" + floorNumber + "-" + letter; // add a zero for single digit floors
        return "F" + floorNumber + "-" + letter; // floor 10
    }

    private String describe(RoomType type) { // short description for each room type
        if (type == RoomType.ITEM) return "Shelves and drawers here hold things someone left behind."; // item room
        if (type == RoomType.MONSTER) return "The air is cold here. Something is waiting in the dark."; // monster room
        if (type == RoomType.PUZZLE) return "A strange challenge has been left here. The door won't let you leave until it is solved."; // puzzle room
        if (type == RoomType.BOSS) return "A huge office. Every guest the hotel ever had is written in the ledger on the desk."; // boss room
        return "Nothing but dust and silence."; // empty room
    }
}
