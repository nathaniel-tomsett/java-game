package engine;

public class CommandTranslator {

    /**
     * All Possible Actions in game
      */
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

    /**
     * All possible Movement directions in game
     */
    public static final int NORTH = 1;
    public static final int EAST = 2;
    public static final int SOUTH = 3;
    public static final int WEST = 4;
    public static final int UP = 5;
    public static final int DOWN = 6;


    public static final int ERROR = -1;

    /**
     * figures out which Action was input based on the input then translates to an enum
     * @param input the Players input that get checked
     * @return
     */
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
        } else if (input.startsWith("inv")|| input.startsWith(("inventory"))) {
            return INVENTORY;
        } else if (input.startsWith("u ")|| input.startsWith("use ")) {
            return USE;
        } else if (input.startsWith("t ")|| input.startsWith("talk to ") || input.startsWith(("talk "))) {
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

    /**
     * similar to the last one but for finding which direction the player wants to go
     * @param input the Players input that get checked
     * @return
     */
    public int getEndMovementArg(String input) {
        if (input.endsWith("east")|| input.endsWith("e")) {
            return EAST;
        } else if (input.endsWith("west")|| input.endsWith("w")) {
            return WEST;
        } else if (input.endsWith("north")|| input.endsWith("n")) {
            return NORTH;
        } else if (input.endsWith("south")|| input.endsWith("s")) {
            return SOUTH;
        } else if (input.endsWith("up")|| input.endsWith("u")) {
            return UP;
        } else if (input.endsWith("down")|| input.endsWith("d")) {
            return DOWN;
        } else {
            return ERROR;
        }
    }

    /**
     * this does the opposite of the last one and using the Enum value
     * gets the string equivalent
     * @return String Direction
     */
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

    /**
     * this is used for Doors mainly to make sure doors when unlocked get unlocked from both sides
     * since the connections between rooms are two sided
     * so when unlocking a door it set the unlock variable to TRUE on the side your on
     * but needs to orient itself on the other side to what it needs to unlock
     * @param direction the direction the Player is trying to unlock in
     * @return the opposite of direction
     */
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

    /**
     * this is a Parser made to interpret a string input
     * here this is used to get a string name of an Item
     * that the Player wants to Pickup
     * @param input  (example: "pickup matilda")
     * @return in example should return "matilda"
     */
    public String getItemToPickup(String input) {

        int firstSpace = input.indexOf(" ");
        if (input.substring(firstSpace) == ""){
            return null;
    }
        String thing = input.substring(firstSpace);
        return thing.trim();
    }

    /**
     * this is a Parser made to interpret a string input
     * here it is used to split a string into its item and Door
     * this will most likely be used for unlocking a door
     * @param input example ( "use key on newport road")
     * @return in example should return ["key", "newport road"]
     */
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

    /**
     * this is a Parser made to interpret a string input
     * here it is getting just a direction to return
     * @param input example ("move north")
     * @return in example should return "north"
     */
    public String getDirectionName(String input) {
        int firstSpace = input.indexOf(" ");
        if (firstSpace == -1) {
            return null;
        }
        String direction = input.substring(firstSpace);
        return direction.trim();
    }


}


