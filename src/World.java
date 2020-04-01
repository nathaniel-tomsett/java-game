import java.util.ArrayList;
import java.util.List;

public class World {

    // List of rooms, containing items and exits
    private List<Room> rooms;

    // Class which takes user input and writes back output to screen
    private UserIO userIO;

    // Processes user input and tells you what the user is trying to do
    private Translator translator;

    // Current room - updated each time we move room
    private String currentRoom;

    World() {
        rooms = new ArrayList<>();
        userIO = new UserIO();
        translator = new Translator();

        currentRoom = "";

        loadTestWorld();
        setCurrentRoom();
    }

    private void loadTestWorld() {
        ResourceReader resourceReader = new ResourceReader();
        rooms = resourceReader.loadRoomsFromResources();
    }

    private void setCurrentRoom() {
        currentRoom = "1";
    }

    public void doGameLoop() {
        while (true) {
            userIO.printToUser("Currently in room: " + getRoom(currentRoom).getName());

            String input = userIO.readFromUser();
            int command = translator.getCommand(input);
            if (command == Translator.MOVE) {
                int direction = translator.getEndMovementArg(input);
                if (direction == Translator.NORTH |
                        direction == Translator.EAST |
                        direction == Translator.SOUTH |
                        direction == Translator.WEST |
                        direction == Translator.UP |
                        direction == Translator.DOWN) {
                    String id = getRoom(currentRoom).getDestination(direction);
                    if (!id.isEmpty()) {
                        currentRoom = id;
                    } else {
                        userIO.printToUser("no exit");
                    }
                } else {
                    userIO.printToUser("invalid direction");
                }
            }
            else if (command == Translator.LOOK) {
                Room r = getRoom(currentRoom);
                List<Item> itemList = r.getItems();
                for (Item i : itemList) {
                    userIO.printToUser("item number #00" + i.getId() + " name: " + i.getName());
                }

                List<Exit> exitList =r.getExits();
                for (Exit e : exitList) {
                    Room d = getRoom(e.getDestinationRoomId());
                    //make a sub routine that takes e.getdirection into a direction then put that subroutine into the code
                    userIO.printToUser("go " + translator.getdirectionstring(e.getDirection())+ " to get to the " + d.getName());
                }

            }
            else {
                userIO.printToUser("I'm sorry, I don't recognise that");
            }
        }
    }

    private Room getRoom(String id) {
        for (Room r : rooms) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        throw new RuntimeException("invalid room");
    }
}
