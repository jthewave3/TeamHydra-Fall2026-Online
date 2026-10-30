package View;

import Model.Room.Direction;
import Model.Room;

public class View {
    public void print(String message) { System.out.println(message); }

    public void showIntro() {
        print("=== THE HOTEL ===");
        print("You wake on the 10th floor of a hotel that should not exist.");
        print("Descend through the floors, survive what lives in them, and defeat");
        print("The Manager on floor 1 to escape.");
    }

    public void showFloor(int floor, String description) {
        print("\n=== Floor " + floor + " ===");
        print(description);
    }

    public void showRoom(Room room, int floor) {
        print("\n--- " + room.getName() + " [Floor " + floor + "] ---");
        print(room.getDescription());
        for (String line : room.describeContents()) print(line);
        StringBuilder exits = new StringBuilder();
        for (Direction d : room.getExits().keySet()) {
            if (exits.length() > 0) exits.append(", ");
            exits.append(d.label());
        }
        print("Exits: " + exits);
    }
}
