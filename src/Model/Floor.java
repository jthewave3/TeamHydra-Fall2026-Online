package Model;

import Model.Room.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Builds the 10 floors of the hotel, from the boss floor (1) up to the starting floor (10). */
public class Floor {
    public static final int START_FLOOR = 10;
    public static final int BOSS_FLOOR = 1;

    private static final Random RNG = new Random();

    private final DataParser data;
    private final Room startRoom;
    // Item rooms on each section's first floor (10, 7, 4); that section's weapon goes in one of them
    private final List<List<Room>> itemRoomsBySection = new ArrayList<>();

    public Floor(DataParser data) {
        this.data = data;
        for (int s = 0; s < DataParser.SECTION_COUNT; s++) itemRoomsBySection.add(new ArrayList<Room>());
        Room entrance = buildBossFloor();
        for (int floor = BOSS_FLOOR + 1; floor <= START_FLOOR; floor++) {
            entrance = buildFloor(floor, entrance);
        }
        placeSectionWeapons();
        startRoom = entrance;
    }

    private void placeSectionWeapons() {
        for (int s = 0; s < DataParser.SECTION_COUNT; s++) {
            Item weapon = data.weaponForSection(s);
            if (weapon == null) continue;
            List<Room> candidates = itemRoomsBySection.get(s);
            candidates.get(RNG.nextInt(candidates.size())).replaceItem(weapon);
        }
    }

    public Room getStartRoom() { return startRoom; }

    public String getDescription(int floor) { return data.floorDescription(floor); }

    /** Floors 10-8 are section 0, 7-5 section 1, 4-2 section 2. */
    public static int sectionFor(int floor) {
        return (START_FLOOR - Math.max(floor, BOSS_FLOOR + 1)) / 3;
    }

    private Room buildBossFloor() {
        Room stairs = make(data.roomById(DataParser.ROOM_BOSS_STAIRS), 0, DataParser.BOSS_SECTION);
        Room closet = make(data.roomById(DataParser.ROOM_BOSS_CLOSET), 0, DataParser.BOSS_SECTION);
        Room boss = make(data.roomById(DataParser.ROOM_BOSS_OFFICE), 0, DataParser.BOSS_SECTION);
        Room exit = make(data.roomById(DataParser.ROOM_EXIT), 0, DataParser.BOSS_SECTION);

        stairs.link(Direction.NORTH, closet);
        closet.link(Direction.NORTH, boss);
        boss.link(Direction.NORTH, exit);
        return stairs;
    }

    private Room buildFloor(int floor, Room lowerEntrance) {
        int section = sectionFor(floor);
        boolean start = floor == START_FLOOR;
        RoomType[] types = start ? startFloorTypes() : randomTypes();

        Room hall = make(data.roomById(DataParser.ROOM_HALLWAY), 0, section);
        Room stairs = make(data.roomById(DataParser.ROOM_STAIRS), 0, section);
        // Positions: 0 top-left, 1 top-middle, 2 top-right, 3 left, 4 right
        Room[] rooms = new Room[5];
        for (int i = 0; i < rooms.length; i++) {
            rooms[i] = make(data.randomRoom(types[i]), floor * 100 + i + 1, section);
            if (rooms[i].getType() == RoomType.ITEM && floor == START_FLOOR - section * 3) {
                itemRoomsBySection.get(section).add(rooms[i]);
            }
        }

        hall.link(Direction.NORTH, rooms[1]);
        hall.link(Direction.WEST, rooms[3]);
        hall.link(Direction.EAST, rooms[4]);
        hall.link(Direction.SOUTH, stairs);
        rooms[1].link(Direction.WEST, rooms[0]);
        rooms[1].link(Direction.EAST, rooms[2]);
        rooms[0].link(Direction.SOUTH, rooms[3]);
        rooms[2].link(Direction.SOUTH, rooms[4]);
        stairs.addExit(Direction.DOWN, lowerEntrance);
        return hall;
    }

    // Fixed starting floor: the first three rooms reachable all hold something to learn from
    private RoomType[] startFloorTypes() {
        return new RoomType[] {RoomType.MONSTER, RoomType.ITEM, RoomType.PUZZLE, RoomType.ITEM, RoomType.ITEM};
    }

    // At least one of each type; the other two rooms and all positions are random
    private RoomType[] randomTypes() {
        RoomType[] pool = {RoomType.ITEM, RoomType.MONSTER, RoomType.PUZZLE};
        List<RoomType> list = new ArrayList<>();
        Collections.addAll(list, pool);
        list.add(pool[RNG.nextInt(pool.length)]);
        list.add(pool[RNG.nextInt(pool.length)]);
        Collections.shuffle(list, RNG);
        return list.toArray(new RoomType[0]);
    }

    private Room make(Room template, int number, int section) {
        Room room = template.spawn(number);
        switch (room.getType()) {
            case MONSTER:
                room.setMonster(data.spawnMonster(section));
                break;
            case BOSS:
                room.setMonster(data.spawnMonster(DataParser.BOSS_SECTION));
                break;
            case PUZZLE:
                room.setPuzzle(data.nextPuzzle());
                break;
            case ITEM:
                room.replaceItem(room.getFixedItem().isEmpty() ? data.randomPotion() : data.itemByName(room.getFixedItem()));
                break;
            default:
                break;
        }
        return room;
    }
}
