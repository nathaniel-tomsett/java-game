package engine;

public class CommandTranslator {

    // Commands
    public static final int MOVE = 1;
    public static final int PICKUP = 2;
    public static final int DROP = 3;
    public static final int LOOK = 4;
    public static final int INVENTORY = 5;
    public static final int USE = 6;
    public static final int TALK = 7;
    public static final int HELP = 8;
    public static final int ABOUT = 9;
    public static final int ATTACK= 10;

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
         if (input.startsWith("m ")|| input.startsWith("move "))
        {
            return MOVE;
        } else if (input.startsWith("p ") || input.startsWith("pickup ")) {
            return PICKUP;
        } else if (input.startsWith("d ")|| input.startsWith("drop ")) {
            return DROP;
        } else if (input.equalsIgnoreCase("l") || input.equalsIgnoreCase("look")) {
            return LOOK;
        } else if (input.startsWith("inv")) {
            return INVENTORY;
        } else if (input.startsWith("u ")|| input.startsWith("use ")) {
            return USE;
        } else if (input.startsWith("t ")|| input.startsWith("talk to ")) {
            return TALK;
        }  else if (input.equalsIgnoreCase("h")|| input.startsWith("help")) {
        return HELP;
        }  else if (input.startsWith("abt")|| input.startsWith("about")) {
        return ABOUT;
        } else if (input.startsWith("atk ")|| input.startsWith("attack ")) {
             return ATTACK;
         } else {
            return ERROR;
        }
    }

    public int getEndMovementArg(String input) {
        if (input.endsWith("east")) {
            return EAST;
        } else if (input.endsWith("west")) {
            return WEST;
        } else if (input.endsWith("north")) {
            return NORTH;
        } else if (input.endsWith("south")) {
            return SOUTH;
        } else if (input.endsWith("up")) {
            return UP;
        } else if (input.endsWith("down")) {
            return DOWN;
        } else {
            return ERROR;
        }
    }

    public String getDirectionString(int input) {
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
    public int flipDirection (int direction) {
        if (direction == NORTH) {
            return SOUTH;
        } else if (direction == SOUTH) {
            return NORTH;
        } else if (direction == EAST) {
            return WEST;
        } else if (direction == WEST) {
            return EAST;
        } else if (direction == UP) {
            return DOWN;
        } else if (direction == DOWN) {
            return UP;
        }
        return ERROR;
    }

    public String getItemString(String input) {
        String[] strings = input.split(" ");
        if (strings.length == 2) {
            return strings[1];
        } else {
            return "";
        }
    }

    public String getItemToPickup(String input) {

        int firstSpace = input.lastIndexOf(" ");
        String thing = input.substring(firstSpace);
        return thing.trim();
    }
    public String getItemToPickup2(String input) {

        int firstSpace = input.indexOf(" ");
        if (input.substring(firstSpace) == ""){
            return null;
    }
        String thing = input.substring(firstSpace);
        return thing.trim();
    }

    public String[] getItemAndDoorString_old(String input) {
        String[] strings = input.split(" ");
        if (strings.length >= 4) {
            String[] newArray = new String[2];
            newArray[0] = strings[1];
            newArray[1] = strings[3];
            return newArray;
        } else {
            return null;
        }
    }

    public String[] getItemAndDoorString(String input) {
        // turn input into two strings
        int onIndex = input.indexOf(" on ");
        if (onIndex == -1){
            return null;
        }
        String doorIndex = input.substring(onIndex);
        int SpaceIndex = doorIndex.indexOf(" on ");
        if (SpaceIndex == -1) {
            return null;
        }
        String subDoorIndex = doorIndex.substring(4).trim();

        int UseIndex = input.indexOf(" ");
        String itemIndex = input.substring(UseIndex, onIndex).trim();
        String[] returnArray = {itemIndex, subDoorIndex};
        return returnArray;


    }

    public String getDirectionName(String input) {
        int firstSpace = input.indexOf(" ");
        if (firstSpace == -1) {
            return null;
        }
        String thing = input.substring(firstSpace);
        return thing.trim();
    }


}


