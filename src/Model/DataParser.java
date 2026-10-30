package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Parses Item.txt, Monster.txt, Puzzle.txt, Room.txt and Floor.txt, validates them, and hands out the data.
 * Format: one record per line, fields separated by '|'. Blank lines and lines starting with '#' are ignored.
 */
public class DataParser {
    public static final int BOSS_SECTION = 3;
    /** Regular sections: floors 10-8, 7-5 and 4-2. */
    public static final int SECTION_COUNT = 3;

    // Room ids from Room.txt that the hotel generator needs
    public static final String ROOM_HALLWAY = "hallway";
    public static final String ROOM_STAIRS = "stairs";
    public static final String ROOM_BOSS_STAIRS = "boss_stairs";
    public static final String ROOM_BOSS_CLOSET = "boss_closet";
    public static final String ROOM_BOSS_OFFICE = "boss_room";
    public static final String ROOM_EXIT = "exit";

    private static final String DATA_DIR = "src/Model/";
    private static final String CLASSPATH_DIR = "/Model/";
    private static final Random RNG = new Random();

    private final List<Item> items = new ArrayList<>();
    private final List<Monster> monsters = new ArrayList<>();
    private final List<Puzzle> puzzles = new ArrayList<>();
    private final List<Room> rooms = new ArrayList<>();
    private final List<Puzzle> puzzleDeck = new ArrayList<>();
    private final Map<Integer, String> floorDescriptions = new HashMap<>();

    /** Loads and validates all data files; fails here with a clear message instead of mid-game. */
    public DataParser() {
        parseItems();
        parseMonsters();
        parsePuzzles();
        parseRooms();
        parseFloors();
        validate();
    }

    private void parseFloors() {
        for (Record r : readRecords("Floor.txt", 2)) {
            int number;
            try {
                number = Integer.parseInt(r.f[0]);
            } catch (NumberFormatException e) {
                throw r.error("expected a floor number");
            }
            if (number < Floor.BOSS_FLOOR || number > Floor.START_FLOOR) {
                throw r.error("floor must be " + Floor.BOSS_FLOOR + "-" + Floor.START_FLOOR);
            }
            if (floorDescriptions.put(number, r.f[1]) != null) throw r.error("duplicate floor " + number);
        }
    }

    public String floorDescription(int floor) { return floorDescriptions.get(floor); }

    private void parseItems() {
        for (Record r : readRecords("Item.txt", 8)) {
            try {
                ItemEffect effect;
                try {
                    effect = ItemEffect.valueOf(r.f[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw r.error("unknown item effect '" + r.f[2] + "'");
                }
                int value = Integer.parseInt(r.f[3]);
                int duration = Integer.parseInt(r.f[4]);
                int minAttack = Integer.parseInt(r.f[5]);
                int maxAttack = Integer.parseInt(r.f[6]);
                int section = Integer.parseInt(r.f[7]);
                if (value < 0 || duration < 0) throw r.error("value and duration cannot be negative");
                if (effect == ItemEffect.WEAPON) {
                    if (minAttack <= 0 || minAttack > maxAttack) throw r.error("a weapon needs 0 < minAttack <= maxAttack");
                    if (section < -1 || section >= SECTION_COUNT) {
                        throw r.error("a weapon's section must be 0-" + (SECTION_COUNT - 1) + ", or -1 for a fixed weapon");
                    }
                } else {
                    if (minAttack != 0 || maxAttack != 0) throw r.error("only weapons have an attack range");
                    if (section != -1) throw r.error("a non-weapon's section must be -1");
                }
                items.add(new Item(r.f[0], r.f[1], effect, value, duration, minAttack, maxAttack, section));
            } catch (NumberFormatException e) {
                throw r.error("expected numbers for value, duration, minAttack, maxAttack and section");
            }
        }
    }

    private void parseMonsters() {
        for (Record r : readRecords("Monster.txt", 6)) {
            try {
                int minHp = Integer.parseInt(r.f[1]);
                int maxHp = Integer.parseInt(r.f[2]);
                int minDmg = Integer.parseInt(r.f[3]);
                int maxDmg = Integer.parseInt(r.f[4]);
                if (minHp > maxHp || minDmg > maxDmg) throw r.error("min is greater than max");
                monsters.add(new Monster(r.f[0], minHp, maxHp, minDmg, maxDmg, Integer.parseInt(r.f[5])));
            } catch (NumberFormatException e) {
                throw r.error("expected numbers for hp, damage and section");
            }
        }
    }

    private void parsePuzzles() {
        for (Record r : readRecords("Puzzle.txt", 2)) {
            puzzles.add(new Puzzle(r.f[0], r.f[1]));
        }
    }

    private void parseRooms() {
        for (Record r : readRecords("Room.txt", 6)) {
            RoomType type;
            try {
                type = RoomType.valueOf(r.f[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw r.error("unknown room type '" + r.f[1] + "'");
            }
            rooms.add(new Room(r.f[0], type, r.f[2], r.f[3], r.f[4], Boolean.parseBoolean(r.f[5])));
        }
    }

    private void validate() {
        for (String id : new String[] {ROOM_HALLWAY, ROOM_STAIRS, ROOM_BOSS_STAIRS, ROOM_BOSS_CLOSET,
                ROOM_BOSS_OFFICE, ROOM_EXIT}) {
            roomById(id);
        }
        requireRoomType(ROOM_HALLWAY, RoomType.HALLWAY);
        requireRoomType(ROOM_STAIRS, RoomType.STAIRS);
        requireRoomType(ROOM_BOSS_STAIRS, RoomType.STAIRS);
        requireRoomType(ROOM_BOSS_CLOSET, RoomType.ITEM);
        requireRoomType(ROOM_BOSS_OFFICE, RoomType.BOSS);
        requireRoomType(ROOM_EXIT, RoomType.EXIT);

        Set<String> ids = new HashSet<>();
        for (Room r : rooms) {
            if (!ids.add(r.getId())) fail("Room.txt has a duplicate room id '" + r.getId() + "'");
            if (r.getType() == RoomType.ITEM && !r.getFixedItem().isEmpty()) itemByName(r.getFixedItem());
        }
        Set<String> itemNames = new HashSet<>();
        for (Item i : items) {
            if (!itemNames.add(i.getName().toLowerCase())) fail("Item.txt has a duplicate item '" + i.getName() + "'");
        }

        for (RoomType type : new RoomType[] {RoomType.ITEM, RoomType.MONSTER, RoomType.PUZZLE}) {
            if (randomPool(type).isEmpty()) fail("Room.txt needs at least one random " + type + " room");
        }
        for (int section = 0; section < SECTION_COUNT; section++) {
            if (!hasMonster(section)) fail("Monster.txt has no monsters for section " + section);
            int weapons = 0;
            for (Item i : items) {
                if (i.isWeapon() && i.getSection() == section) weapons++;
            }
            if (weapons > 1) fail("Item.txt has " + weapons + " weapons for section " + section + " (at most one)");
        }
        if (!hasMonster(BOSS_SECTION)) fail("Monster.txt has no boss (section " + BOSS_SECTION + ")");
        if (potions().isEmpty()) fail("Item.txt has no potions");
        if (puzzles.isEmpty()) fail("Puzzle.txt has no puzzles");
        for (int floor = Floor.BOSS_FLOOR; floor <= Floor.START_FLOOR; floor++) {
            if (!floorDescriptions.containsKey(floor)) fail("Floor.txt has no entry for floor " + floor);
        }
    }

    private void requireRoomType(String id, RoomType expected) {
        RoomType actual = roomById(id).getType();
        if (actual != expected) fail("Room.txt: '" + id + "' must be type " + expected + " but is " + actual);
    }

    private boolean hasMonster(int section) {
        for (Monster m : monsters) {
            if (m.getSection() == section) return true;
        }
        return false;
    }

    private List<Room> randomPool(RoomType type) {
        List<Room> pool = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isRandom() && r.getType() == type) pool.add(r);
        }
        return pool;
    }

    private static void fail(String message) { throw new IllegalStateException(message); }

    public Item itemByName(String name) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name)) return item;
        }
        throw new IllegalStateException("Unknown item in data files: " + name);
    }

    private List<Item> potions() {
        List<Item> potions = new ArrayList<>();
        for (Item i : items) {
            if (i.isPotion()) potions.add(i);
        }
        return potions;
    }

    /** Potions can repeat, so each call picks any potion at random. */
    public Item randomPotion() {
        List<Item> potions = potions();
        return potions.get(RNG.nextInt(potions.size()));
    }

    /** The unique weapon that belongs to a section (0-2), or null if that section has none. */
    public Item weaponForSection(int section) {
        for (Item i : items) {
            if (i.isWeapon() && i.getSection() == section) return i;
        }
        return null;
    }

    public Monster spawnMonster(int section) {
        List<Monster> pool = new ArrayList<>();
        for (Monster m : monsters) {
            if (m.getSection() == section) pool.add(m);
        }
        if (pool.isEmpty()) throw new IllegalStateException("No monsters for section " + section);
        return pool.get(RNG.nextInt(pool.size())).copy();
    }

    /** Puzzles are dealt from a shuffled deck so a floor rarely repeats a riddle. */
    public Puzzle nextPuzzle() {
        if (puzzleDeck.isEmpty()) {
            puzzleDeck.addAll(puzzles);
            Collections.shuffle(puzzleDeck, RNG);
        }
        return puzzleDeck.remove(0).copy();
    }

    public Room roomById(String id) {
        for (Room r : rooms) {
            if (r.getId().equals(id)) return r;
        }
        throw new IllegalStateException("Unknown room id in data files: " + id);
    }

    public Room randomRoom(RoomType type) {
        List<Room> pool = randomPool(type);
        if (pool.isEmpty()) throw new IllegalStateException("No random rooms of type " + type);
        return pool.get(RNG.nextInt(pool.size()));
    }

    private static class Record {
        final String file;
        final int line;
        final String[] f;

        Record(String file, int line, String[] f) {
            this.file = file;
            this.line = line;
            this.f = f;
        }

        IllegalStateException error(String message) {
            return new IllegalStateException(file + " line " + line + ": " + message);
        }
    }

    private static List<Record> readRecords(String fileName, int fieldCount) {
        List<Record> records = new ArrayList<>();
        int lineNumber = 0;
        try (BufferedReader reader = open(fileName)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty() || line.trim().startsWith("#")) continue;
                String[] fields = line.split("\\|", -1);
                for (int i = 0; i < fields.length; i++) fields[i] = fields[i].trim();
                Record record = new Record(fileName, lineNumber, fields);
                if (fields.length != fieldCount) {
                    throw record.error("expected " + fieldCount + " fields but found " + fields.length);
                }
                records.add(record);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + fileName, e);
        }
        return records;
    }

    // Prefers the source folder (IDE runs from the project root), then falls back to the classpath
    private static BufferedReader open(String fileName) throws IOException {
        Path path = Paths.get(DATA_DIR + fileName);
        if (Files.exists(path)) {
            return Files.newBufferedReader(path, StandardCharsets.UTF_8);
        }
        InputStream in = DataParser.class.getResourceAsStream(CLASSPATH_DIR + fileName);
        if (in == null) throw new IOException("Data file not found: " + fileName);
        return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    }
}
