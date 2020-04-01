public class Translator {

    // Commands
    public static final int MOVE = 1;
    public static final int PICKUP = 2;
    public static final int DROP = 3;
    public static final int LOOK = 4;


    // Movement directions
    public static final int NORTH = 1;
    public static final int EAST = 2;
    public static final int SOUTH = 3;
    public static final int WEST = 4;
    public static final int UP = 5;
    public static final int DOWN = 6;

    // Error
    public static final int ERROR = -1;

    public int getCommand(String input) {
        if (input.startsWith("move ")) {
            return MOVE;
        }
        else if (input == "pickup ") {
            return PICKUP;
        }
        else if (input == "drop ") {
            return DROP;
        }
        else if (input.startsWith ("look"))  {
            return LOOK;
        }
        else {
            return ERROR;
        }
    }

    public int getEndMovementArg(String input) {
        if (input.endsWith("east")) {
            return EAST;
        }
        else if (input.endsWith("west")) {
            return WEST;
        }
        else if (input.endsWith("north")) {
            return NORTH;
        }
        else if (input.endsWith("south")) {
            return SOUTH;
        }
        else if (input.endsWith("up")) {
            return UP;
        }
        else if (input.endsWith("down")) {
            return DOWN;
        }
        else {
            return ERROR;
        }
    }

    public String getdirectionstring(int input) {
        if (input == NORTH) {
            return "north";
        } else if (input == EAST) {
            return "east";
        } else if (input == SOUTH) {
            return "south";
        } else if (input == WEST) {
            return "west";
        } else if (input == UP) {
            return "up";
        } else if (input == DOWN) {
            return "down";
        } else {
            return "error";
        }
    }
}
