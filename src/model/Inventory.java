package model; // all the game data classes live in this package

public class Inventory { // the player's 5 slots, A to E
    public static final int SIZE = 5; // always 5 slots

    private Item[] slots = new Item[SIZE]; // null means the slot is empty

    public void add(Item item) { // puts the item in the first empty slot
        for (int i = 0; i < SIZE; i++) { // go through the slots
            if (slots[i] == null) { // found an empty one
                slots[i] = item; // put the item there
                return; // done
            }
        }
    }

    public Item get(int index) { // gets whatever is in a slot (could be null)
        return slots[index]; // send it back
    }

    public void set(int index, Item item) { // puts an item in a specific slot (used for loading)
        slots[index] = item; // save it
    }

    public void remove(int index) { // empties a slot
        slots[index] = null; // nothing there now
    }

    public boolean isFull() { // true when there are no empty slots
        for (int i = 0; i < SIZE; i++) { // go through the slots
            if (slots[i] == null) return false; // found an empty one
        }
        return true; // none were empty
    }

    public boolean hasId(String id) { // true if the player already has this item
        for (int i = 0; i < SIZE; i++) { // go through the slots
            if (slots[i] != null && slots[i].getId().equals(id)) return true; // same id
        }
        return false; // not found
    }

    public int indexOf(Item item) { // finds which slot an item is in, -1 if it isn't there
        for (int i = 0; i < SIZE; i++) { // go through the slots
            if (slots[i] == item) return i; // same object
        }
        return -1; // not found
    }

    public int findSlot(String text) { // finds a slot from a letter like "b" or a name like "sword"
        text = text.trim().toLowerCase(); // clean up what was typed
        if (text.length() == 1) { // one letter means a slot letter
            int index = text.charAt(0) - 'a'; // a is 0, b is 1, and so on
            if (index >= 0 && index < SIZE) return index; // valid slot letter
        }
        for (int i = 0; i < SIZE; i++) { // otherwise look for the name
            if (slots[i] != null && slots[i].getName().toLowerCase().contains(text)) return i; // name matches
        }
        return -1; // nothing matched
    }
}
