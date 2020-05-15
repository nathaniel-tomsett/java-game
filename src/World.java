import java.lang.reflect.Array;
import java.util.*;

public class World {

    // List of rooms, containing items and exits
    private List<Room> rooms;

    // List of NPCs
    private List<NPC> npcs;

    // Class which takes user input and writes back output to screen
    private UserIO userIO;

    // Processes user input and tells you what the user is trying to do
    private Translator translator;

    // Current room - updated each time we move room
    private String currentRoom;

    private Player player;

    World() {
        rooms = new ArrayList<>();
        npcs = new ArrayList<>();
        userIO = new UserIO(false);
        translator = new Translator();
        player = new Player();
        currentRoom = "";

        loadTestWorld();
        setCurrentRoom();
    }

    private void loadTestWorld() {
        ResourceReader resourceReader = new ResourceReader();
        rooms = resourceReader.loadRoomsFromResources();
        npcs = resourceReader.loadNPCs();
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
                boolean roomValid = false;
                String directionName = translator.getDirectionName(input);
                int directionValue = translator.getEndMovementArg(directionName);
                List<Room> roomOB = getRoomDirection(directionValue);
                userIO.printToUser("you can move to these rooms");
                List<String> roomNameList =new ArrayList<>();
                for (Room i: roomOB){
                   String roomName2 = i.getName();
                    roomNameList.add(roomName2);
                    userIO.printToUser(roomName2);
                }
                userIO.printToUser("where would you like to go");
                String newinput  = userIO.readFromUser();
                for (String i : roomNameList){
                    if (i.equalsIgnoreCase(newinput)){

                        Room r = getRoom(currentRoom);
                        List<Door> doorList = r.getDoors();
                        for (Door d : doorList) {
                            String DID = d.getDestinationRoomId();
                            Room DestRoom = getRoom(DID);
                            if ( DestRoom != null && newinput.equalsIgnoreCase(DestRoom.getName())) {
                                if (!d.getlocked()) {
                                    currentRoom = DestRoom.getId();
                                    roomValid = true;
                                } else {
                                    userIO.printToUser("the door is locked");
                                }
                            }
                        }
                        if (!roomValid) {
                            userIO.printToUser("the room doesnt exist");
                        }
                    }


                }


                Room r = getRoom(currentRoom);
                List<Door> doorList = r.getDoors();
                for (Door d : doorList) {
                    String DID = d.getDestinationRoomId();
                    Room DestRoom = getRoom(DID);
                   if ( DestRoom != null && newinput.equalsIgnoreCase(DestRoom.getName())) {
                        if (!d.getlocked()) {
                            currentRoom = DestRoom.getId();
                            roomValid = true;
                        } else {
                            userIO.printToUser("the door is locked");
                        }
                    }
                }
                if (!roomValid) {
                    userIO.printToUser("the room doesnt exist");
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

                List<NPC> npcList = getNPCsInRoom(currentRoom);
                if (npcList != null && !npcList.isEmpty()) {
                    userIO.printToUser("Characters in the room:");
                    for (NPC n : npcList) {
                        userIO.printToUser("    " + n.getName());
                    }
                }

            } else if (command == Translator.PICKUP) {
                String itemName = translator.getItemTopickup2(input);
                Item item = getItemFromRoomByName(itemName);
                if (item == null) {
                    userIO.printToUser("pffff i dont know what youve done");
                } else {
                    Inventory inventory = player.getInventory();
                    if (inventory.addItem(item)) {
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
                if (Items.isEmpty()) {
                    userIO.printToUser("You're inventory is empty");
                }

            } else if (command == Translator.DROP) {
                String itemName = translator.getItemToPickup(input);
                Room r = getRoom(currentRoom);
                List<Item> itemList = r.getItems();
                Item fred = getItemFromInv(itemName);
                if (fred == null) {
                    userIO.printToUser("you dont have that item at the moment");
                } else {
                    itemList.add(fred);
                    removeItemFromInv(itemName);
                    userIO.printToUser("yay you dropped an item well done");
                }

            } else if (command == translator.USE) {
                String[] itemAndDoor = translator.getitemandoorstring_new(input);
                if (itemAndDoor != null) {
                    String itemName = itemAndDoor[0];
                    String doorDestination = itemAndDoor[1];
                    if (player.getInventory().doesexistByName(itemName)) {
                        Door d = getDoorfromdoorid(currentRoom, doorDestination);
                        if (d != null && d.tryUnlock(itemName)) {
                            Door d2 = getDoor(d.getDestinationRoomId(), flipDirection(d.getDirection()));
                            if (d2 != null && d2.tryUnlock(itemName)) {
                                userIO.printToUser("the door opens");
                            } else {
                                userIO.printToUser("error, check key settings for doors");
                            }
                        } else {
                            userIO.printToUser("the item was innefective");
                        }
                    } else {
                        userIO.printToUser("this item is not in your inventory");
                    }
                } else {
                    userIO.printToUser("thats invalid");
                }
            } else if (command == translator.TALK) {
                String NpcName = translator.getItemToPickup(input);
                NPC n = getNPCFromRoom(NpcName, currentRoom);
                if (n != null) {
                    String Dialogue = n.getRandomDialog();
                    userIO.printToUser(n.getName() + " says: " + Dialogue, UserIO.GREEN);
                } else {
                    userIO.printToUser("this persons not in the room with you");
                }
            }else if (command == translator.HELP) {
                userIO.printToUser("move (insert name of place you'd like to go here)to move to that place");
                userIO.printToUser("look to see your current surroundings and where you can go" );
                userIO.printToUser("pickup (insert item name here) to pickup an item" );
                userIO.printToUser("drop (insert item name here) to drop an item" );
                userIO.printToUser("talk to (insert NPC name here) to talk to an NPC " );
                userIO.printToUser("inv to look at your inventory" );
                userIO.printToUser("use (insert item name here) on (insert name of thing you'd like to use item on here) to use an item" );
                userIO.printToUser("about to see credits of this game" );
                userIO.printToUser("all of the above excluding inv can be shortened to their first letter e.g (m newport road or p house key)" );
                userIO.printToUser("have fun!!!" );
            }
            else if (command == translator.ABOUT) {
                userIO.printToUser("an untitled game");
                userIO.printToUser("that is still unfinished and in progress" );
                userIO.printToUser("coded by Nathaniel and Fraser" );
                userIO.printToUser("\nthanks for playing " );
            }
            else {
                userIO.printToUser("I'm sorry, I don't recognise that");
                movement = 0;
            }


//            if (movement == 1){
//
//                Set<String> movedPlayers = new HashSet<>();
//
//                for (Room r: rooms){
//                    if (r.getNpcs() != null) {
//                        List<NPC> copyOfList = new ArrayList<>(r.getNpcs());
//                        for (NPC n : copyOfList) {
//
//                            if (!movedPlayers.contains(n.getId()) && n.shouldMove()){
//                                Random rand = new Random();
//                                int randExit = rand.nextInt(r.getDoors().size());
//                                Door randDirection = r.getDoors().get(randExit);
//
//                               // userIO.printToUser("NPC: " + n.getName() + " is going to move to room " + getRoom(randDirection.getDestinationRoomId()).getName());
//
//                                if (!randDirection.getlocked()) {
//                                    String randDID = randDirection.getDestinationRoomId();
//                                    removeNpcFromRoom( r, n  );
//                                    addNpcFromRoom(getRoom(randDID), n);
//                                  //  userIO.printToUser("move worked!");
//
//                                    movedPlayers.add(n.getId());
//                                } else {
//                                   // userIO.printToUser("room was locked!");
//                                }
//
//
//                            }
//
//                        }
//                    }
//                    else{
//                        //userIO.printToUser("ahhhhhhhhhhhh you broke it");
//                    }
//
//                }
//            }

        }

    }

    private Room getRoom(String id) {
        for (Room r : rooms) {
            if (r.getId().equals(id)) {
                    return r;
            }
        }
        return null;
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


//    private void removeNpcFromRoom(Room currentRoom, NPC NPC){
//        List <NPC> npcList = currentRoom.getNpcs();
//        npcList.remove(NPC);
//
//    }
//
//    private void addNpcFromRoom(Room destinationRoom, NPC NPC){
//        List <NPC> npcList = destinationRoom.getNpcs();
//        npcList.add(NPC);
//
//    }
//


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

    private List<NPC> getNPCsInRoom(String roomID) {
        List<NPC> retNPCList = new ArrayList<>();
        for (NPC n : npcs) {
            if (n.getCurrentRoomID().equals(roomID)) {
                retNPCList.add(n);
            }
        }
        return retNPCList;
    }

    private NPC getNPCFromRoom(String npcName, String roomID) {
        for (NPC n : npcs) {
            if (n.getCurrentRoomID().equals(roomID)) {
                return n;
            }
        }
        return null;
    }

   public List<Room> getRoomDirection( int directionValue ) {
       Room r = getRoom(currentRoom);
       List<Room> exitName = new ArrayList<>();
       List<Door> exits = r.getDoors();
       for (Door e : exits) {
           int direction = e.getDirection();
           if (direction == directionValue) {
               String DiD = e.getDestinationRoomId();
               Room roomOb = getRoom(DiD);
               exitName.add(roomOb);


           }
       }
       return exitName;


   }
}