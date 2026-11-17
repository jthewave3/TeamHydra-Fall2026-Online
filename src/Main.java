import controller.GameController; // runs the game
import view.GameView; // prints to the screen

public class Main { // where the program starts
    public static void main(String[] args) { // java starts running here
        GameView view = new GameView(); // make the view
        GameController controller = new GameController(view); // make the controller and give it the view
        controller.run(); // start the game
    }
}
