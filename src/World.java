import java.util.*;

public class World {

    // List of rooms, containing items and exits
    private List<Room> rooms;

    // Class which takes user input and writes back output to screen
    private UserIO userIO;

    // Processes user input and tells you what the user is trying to do
    private Translator translator;

    // Current room - updated each time we move room
    private String currentRoom;

    private Player player;

    World() {
        rooms = new ArrayList<>();
        userIO = new UserIO();
        translator = new Translator();
        player = new Player();
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

        String newRoom = "";
        while (true) {
            int movement = 1;

            if (!newRoom.equals(currentRoom)) {
                userIO.printToUser("Currently in room: " + getRoom(currentRoom).getName());
            }
            newRoom = currentRoom;

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
                    Door door = getDoor(currentRoom, direction);
                    if (door != null){
                        if (!door.getlocked()) {
                            if (!id.isEmpty()) {
                                currentRoom = id;
                            } else {
                                userIO.printToUser("no exit");
                            }
                        } else {
                            userIO.printToUser("the door is locked");
                        }
                    }
                    else{
                        userIO.printToUser("invalid direction");
                    }


                } else {
                    userIO.printToUser("invalid direction");

                }
            } else if (command == Translator.LOOK) {
                Room r = getRoom(currentRoom);

                // Prints the description
                String roomDesc = r.getDescription();
                userIO.printToUser(roomDesc);

                // Print the available directions to move
                List<Door> exitList = r.getDoors();
                userIO.printToUser("Exits: ");
                for (Door e : exitList) {
                    Room d = getRoom(e.getDestinationRoomId());
                    //make a sub routine that takes e.getdirection into a direction then put that subroutine into the code
                    userIO.printToUser("    " + translator.getdirectionstring(e.getDirection()) + " - " + d.getName());
                }

                List<Item> itemList = r.getItems();
                if (itemList != null && !itemList.isEmpty()) {
                    userIO.printToUser("Items: ");
                    for (Item i : itemList) {
                        userIO.printToUser("    " + i.getName());
                    }
                }

                List<NPC> npcList = r.getNpcs();
                if (r.getNpcs()!= null && !r.getNpcs().isEmpty()) {
                    userIO.printToUser("Characters in the room:");
                    for (NPC n : npcList) {
                        userIO.printToUser("    " + n.getName());
                    }
                }


            } else if (command == Translator.PICKUP) {
                String itemName = translator.getItemToPickup(input);
                Item item = getItemFromRoomByName(itemName);
                if (item == null) {
                    userIO.printToUser("pffff i dont know what youve done");
                } else {
                    Inventory inventory = player.getInventory();
                    if (inventory.addItem(item)){
                        userIO.printToUser("Added " + item.getName() + " to your inventory");
                        removeItemFromRoomByName(itemName);
                    }
                }


            } else if (command == Translator.INVENTORY) {
                Inventory inv = player.getInventory();
                List<Item> Items = inv.getItems();
                userIO.printToUser("Inventory: ");

                for (Item i : Items) {
                    userIO.printToUser("name: " + i.getName());
                }
                if (Items.isEmpty()){
                    userIO.printToUser("You're inventory is empty");
                }

            } else if (command == Translator.DROP){
                String itemName = translator.getItemToPickup(input);
                Room r = getRoom(currentRoom);
                List<Item> itemList = r.getItems();
                itemList.add(getItemFromInv(itemName));
                removeItemFromInv(itemName);

            } else if (command == translator.USE){
                String[] itemAndDoor = translator.getitemandoorstring_new(input);
                String  itemName= itemAndDoor[0];
                String doorDestination =itemAndDoor[1];
                if (player.getInventory().doesexistByName(itemName)){
                    Door d = getDoorfromdoorid(currentRoom , doorDestination);
                    if (d != null && d.tryUnlock(itemName)){
                        Door d2 = getDoor(d.getDestinationRoomId(), flipDirection(d.getDirection()));
                        if (d2 != null && d2.tryUnlock(itemName)){
                            userIO.printToUser("the door opens");
                        } else {
                            userIO.printToUser("error, check key settings for doors");
                        }
                    }
                    else{
                        userIO.printToUser("the item was innefective");
                    }
                }
                else{
                    userIO.printToUser("this item is not in your inventory");
                }
            }
            else {
                userIO.printToUser("I'm sorry, I don't recognise that");
                movement = 0;
            }
            if (movement == 1){

                Set<String> movedPlayers = new HashSet<>();

                for (Room r: rooms){
                    if (r.getNpcs() != null) {
                        List<NPC> copyOfList = new ArrayList<>(r.getNpcs());
                        for (NPC n : copyOfList) {

                            if (!movedPlayers.contains(n.getId()) && n.shouldMove()){
                                Random rand = new Random();
                                int randExit = rand.nextInt(r.getDoors().size());
                                Door randDirection = r.getDoors().get(randExit);

                               // userIO.printToUser("NPC: " + n.getName() + " is going to move to room " + getRoom(randDirection.getDestinationRoomId()).getName());

                                if (!randDirection.getlocked()) {
                                    String randDID = randDirection.getDestinationRoomId();
                                    removeNpcFromRoom( r, n  );
                                    addNpcFromRoom(getRoom(randDID), n);
                                  //  userIO.printToUser("move worked!");

                                    movedPlayers.add(n.getId());
                                } else {
                                   // userIO.printToUser("room was locked!");
                                }


                            }

                        }
                    }
                    else{
                        //userIO.printToUser("ahhhhhhhhhhhh you broke it");
                    }

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

    private Item getItemFromRoomById(String itemId) {
        Room r = getRoom(currentRoom);
        List<Item> itemList = r.getItems();
        for (Item i : itemList) {
            if (i.getId().equals(itemId) ){
                return i;
            }

        }
        return null;
    }

    private Item getItemFromRoomByName(String itemName) {
        Room r = getRoom(currentRoom);
        List<Item> itemList = r.getItems();
        for (Item i : itemList) {
            if (i.getName().equalsIgnoreCase(itemName) ){
                return i;
            }

        }
        return null;
    }


    private Item removeItemFromRoomById(String itemId) {
        Room r = getRoom(currentRoom);
        List<Item> itemList = r.getItems();
        Item toDelete = null;
        for (Item i : itemList) {
            if (i.getId().equals(itemId)) {
                toDelete = i;
                break;
            }
        }
        if (toDelete != null) {
            itemList.remove(toDelete);
        }
        return null;
    }

    private Item removeItemFromRoomByName(String name) {
        Room r = getRoom(currentRoom);
        List<Item> itemList = r.getItems();
        Item toDelete = null;
        for (Item i : itemList) {
            if (i.getName().equalsIgnoreCase(name)) {
                toDelete = i;
                break;
            }
        }
        if (toDelete != null) {
            itemList.remove(toDelete);
        }
        return null;
    }


    private void removeNpcFromRoom(Room currentRoom, NPC NPC){
        List <NPC> npcList = currentRoom.getNpcs();
        npcList.remove(NPC);

    }

    private void addNpcFromRoom(Room destinationRoom, NPC NPC){
        List <NPC> npcList = destinationRoom.getNpcs();
        npcList.add(NPC);

    }



    private void removeItemFromInv (String itemName){
        Room r = getRoom(currentRoom);
        Inventory Inventory = player.getInventory();
        List<Item> invList = Inventory.getItems();
        Item toRemove = null;
        for (Item i:invList){
            if (i.getName().equalsIgnoreCase(itemName)) {
                toRemove = i;
            }
        }
        if (toRemove != null) {
        invList.remove(toRemove);
        }
    }

    private Item getItemFromInv(String itemName) {
        Inventory Inventory = player.getInventory();
        List<Item> invList = Inventory.getItems();
        for (Item i : invList) {
            if (i.getName().equalsIgnoreCase(itemName)) {
                return i;
            }
        }
        return null;
    }
    private Door getDoor (String roomId, int direction  ){
       Room r = getRoom(roomId);
        List<Door> doors = r.getDoors();
        for (Door d : doors) {
            if (d.getDirection() == direction){
                return d;
            }

        }
        return null;

    }
    private Door getDoorfromdoorid (String roomId,String destinationRoom){
        Room r = getRoom(roomId);
        List<Door> doors = r.getDoors();
        for (Door d : doors) {
           String DRI = d.getDestinationRoomId();
            Room destRoom = getRoom(DRI);
            if (destRoom.getName().equalsIgnoreCase (destinationRoom)){
                return d;
            }

        }
        return null;

    }

    public static void main(String[] args) {
        World w = new World();
        w.doGameLoop();
    }

    public int flipDirection (int direction) {
        if (direction == translator.NORTH) {
            return translator.SOUTH;
        } else if (direction == translator.SOUTH) {
            return translator.NORTH;
        } else if (direction == translator.EAST) {
            return translator.WEST;
        } else if (direction == translator.WEST) {
            return translator.EAST;
        } else if (direction == translator.UP) {
            return translator.DOWN;
        } else if (direction == translator.DOWN) {
            return translator.UP;
        }
        return translator.ERROR;
    }

}