package Controller;

import Model.DataParser;
import Model.Floor;
import Model.Player;
import View.View;

public class Controller {
    private final View view = new View();
    private final Player player;
    private final Floor floor;

    public Controller() {
        floor = new Floor(new DataParser());
        player = new Player(floor.getStartRoom(), Floor.START_FLOOR);
    }

    // Commands are not implemented yet; this only shows the intro, the floor and the starting room
    public void run() {
        view.showIntro();
        int current = player.getFloor();
        view.showFloor(current, floor.getDescription(current));
        view.showRoom(player.getLocation(), player.getFloor());
    }
}
