import java.util.ArrayList;
import java.util.List;

public class World {

    private List<Room> rooms;

    private UserIO userIO;

    private Translator translator;

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
        currentRoom = "room-1";
    }

//    private void loadTestWorld_old() {
//        Room diningRoom = new Room("1", "Dining Room");
//        Room livingRoom = new Room("2", "Living Room");
//        Room kitchen = new Room("3", "Kitchen");
//        Room basement = new Room( "4","Basement");
//
//        rooms.add(diningRoom);
//        rooms.add(livingRoom);
//        rooms.add(kitchen);
//        rooms.add(basement);
//
//        Item sword = new Item("1", "swordy-mc-swordface", Item.WEAPON);
//        Item matilda = new Item("2", "tilda", Item.ANIMAL);
//        Item key = new Item("3", "key", Item.KEY);
//        Item glass = new Item("4", "glassface", Item.OTHER);
//
//        List<Item> items = new ArrayList<>();
//        items.add(sword);
//        items.add(matilda);
//        items.add(key);
//        items.add(glass);
//
//        diningRoom.setItems(items);
//
//        Exit e1 = new Exit("1", Translator.EAST,"3");
//        Exit e2 = new Exit("2", Translator.WEST,"1");
//        Exit e3 = new Exit("3", Translator.SOUTH,"2");
//        Exit e4 = new Exit("4", Translator.NORTH,"1");
//        Exit e5 = new Exit("5", Translator.DOWN,"4");
//        Exit e6 = new Exit("6", Translator.UP,"3");
//
//        List <Exit>  diningRoomExits = new ArrayList<>();
//        diningRoomExits.add(e1);
//        diningRoomExits.add(e3);
//
//        List <Exit>  livingRoomExits = new ArrayList<>();
//        livingRoomExits.add(e4);
//
//        List <Exit>  kitchenExits = new ArrayList<>();
//        kitchenExits.add(e2);
//        kitchenExits.add(e5);
//
//        List <Exit>  basementExits = new ArrayList<>();;
//        basementExits.add(e6);
//
//       diningRoom.setExit(diningRoomExits);
//       livingRoom.setExit(livingRoomExits);
//       kitchen.setExit(kitchenExits);
//       basement.setExit(basementExits);
//
//        currentRoom = "1";
//    }

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
