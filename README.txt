HORROR HOTEL
=============

ABOUT THE GAME
--------------
Horror Hotel is a text-based Java adventure game. You wake up on the
10th floor of an abandoned hotel with no memory of how you arrived.
The stairway doors are locked, and the only way out is through the
hotel's ground floor.

Explore each floor, collect useful items, solve puzzles, and fight the
monsters blocking your path. Clear every monster and puzzle room on a
floor to receive the Stairway Key. Then find the stairs and descend to
the next floor.

On floor 1, defeat The Manager. Once the boss floor is cleared, the
hotel exit opens. Reach the exit to win.


REQUIREMENTS
------------
- Java Development Kit (JDK), including the javac and java commands
- A command-line terminal


HOW TO COMPILE AND RUN
----------------------
Open a terminal in the project folder:

    C:\Users\mingu\TeamHydra-Fall2026-Online

Compile the source files into the out folder:

    javac -d out src\Main.java src\controller\*.java src\model\*.java src\view\*.java

Start the game:

    java -cp out Main

If the out folder does not exist, javac will create it.


HOW TO PLAY
-----------
The game prints the current room and then waits for a command at the
">" prompt. Commands are not case-sensitive.

Movement:
    north, n       Move north
    east, e        Move east
    south, s       Move south
    west, w        Move west

Exploration:
    map            Show the current floor map
    inspect        Inspect the current room
    inspect ITEM   Inspect an item in the room or inventory
    pickup         Pick up an item
    pickup ITEM    Pick up a named item
    drop SLOT      Drop an inventory item
    view inventory Show the five inventory slots
    close inventory
                   Close the inventory display

Inventory and combat:
    equip SLOT     Equip a weapon
    use SLOT       Use a potion or device
    attack         Attack the monster with the equipped weapon
    flee           Try to escape from a fight

Puzzles and help:
    solve ANSWER   Submit an answer to the puzzle in the current room
    hint           Get a hint about the current objective
    commands       Show the command list in the game

Game control:
    save           Save the game and exit
    quit           Exit without saving

SLOT can be an inventory letter such as A, B, C, D, or E. If a command
that needs an item slot is entered without one, the game displays the
inventory and asks you to choose an item.


ROOMS AND FLOOR PROGRESSION
---------------------------
The hotel contains floors 10 through 1. Floors are generated from a
random seed, so a new game can have a different layout and item
placement.

Rooms can include:
- Hallways, where each floor begins
- Supply rooms containing items
- Haunted suites containing monsters
- Locked studies containing puzzles
- Empty rooms
- The Manager's Office on floor 1
- Stairwells leading to the next floor
- The Hotel Exit on floor 1

The stairs remain locked until every monster and puzzle room on the
current floor is cleared. Rooms that do not contain a monster or puzzle
do not need to be cleared.

The map uses these symbols:
    @  You
    ?  Unvisited room
    S  Starting hallway
    I  Item room
    M  Monster room
    P  Puzzle room
    E  Empty room
    B  Boss room
    >  Stairs
    X  Exit


PUZZLES
-------
Puzzle types change as you descend:
- Floors 10 through 8: hotel-themed riddles, with four attempts
- Floors 7 through 5: word scrambles, with three attempts
- Floors 4 through 2: Caesar ciphers, with two attempts

Type the answer using:

    solve ANSWER

Answers ignore capitalization and spaces. A wrong answer costs 3 HP.
If all attempts are used, the puzzle locks and you must visit at
least two other rooms before trying it again.


COMBAT
------
Entering a monster room starts a fight. During combat, you can only
attack, use an item, or flee.

You must equip a weapon before using attack. Each attack gives the
monster a chance to attack back. If your HP reaches zero, you die.

Fleeing has a 50 percent chance of success against ordinary monsters.
You cannot flee from The Manager.

Available weapons include:
- Sword: moderate damage and durability
- Bow & Arrow: higher damage with a limited number of arrows
- Fire Axe: high damage, but it swings every other turn and has
  limited durability

Available consumables include:
- Vanishing Potion: instantly defeats a monster
- Shrinking Potion: removes half of a monster's current HP
- Healing Potion: restores 25 percent of your maximum HP
- Shield Potion: reduces damage for two turns
- Clone Potion: doubles your damage for two turns
- Flash Bomb: stuns a monster for two turns


DEATH AND SAVING
----------------
If you die, you return to the start of the current floor with full
health. Monsters and puzzles on that floor reset, but you keep your
items.

Use save to write your progress to save.txt and exit. When the game
starts, it asks whether to load an existing save file. Use quit to
exit without saving.


PROJECT STRUCTURE
-----------------
src\Main.java                  Program entry point
src\controller\GameController.java
                               Reads commands and runs the game loop
src\view\GameView.java         Displays rooms, maps, combat, and prompts
src\model\                    Player, rooms, floors, items, monsters,
                               puzzles, combat, and save data

