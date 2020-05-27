package engine;

import entities.*;
import users.UserStream;
import util.TextColours;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandHandler extends Thread {
    private World world;
    private String userId;
    private UserStream userStream;
    private CommandTranslator commandTranslator;
    private String lastRoom;

    public CommandHandler(World world, String userId, UserStream userStream) {
        this.world = world;
        this.userId = userId;
        this.userStream = userStream;
        this.commandTranslator = new CommandTranslator();
        this.lastRoom = "";
        start();
    }

    @Override
    public void run() {
        printCurrentRoom();
        while (true) {
            userStream.printToUserSameLine("Input> ", TextColours.RED);
            String input = userStream.readFromUser();
            if (input == null) {
                break;
            }
            doUserCommand(input);
        }
    }

    public void doUserCommand(String input) {
        boolean npcsShouldMove = true;
        boolean shouldPrintCurrentRoomAfterMove = false;

        String currentRoom = world.getPlayer(userId).getCurrentRoomID();

        int command = commandTranslator.getCommand(input);
        if (command == CommandTranslator.MOVE) {
            boolean roomValid = false;
            String directionName = commandTranslator.getDirectionName(input);
            int directionValue = commandTranslator.getEndMovementArg(directionName);
            List<Room> roomOB = getRoomDirection(directionValue);
            userStream.printToUser("You can move to these rooms:");
            List<String> roomNameList = new ArrayList<>();
            for (Room i: roomOB){
                String roomName2 = i.getName();
                roomNameList.add(roomName2);
                userStream.printToUser("    " + roomName2);
            }
            if (roomNameList.isEmpty()){
                userStream.printToUser("there are no rooms in this direction");
            }
            else {
                userStream.printToUser("Where would you like to go?");
                userStream.printToUserSameLine("Input> ", TextColours.RED);
                String newinput  = userStream.readFromUser();
                for (String i : roomNameList){
                    if (i.equalsIgnoreCase(newinput)){

                        Room r = getRoom(currentRoom);
                        List<Door> doorList = r.getDoors();
                        for (Door d : doorList) {
                            String DID = d.getDestinationRoomId();
                            Room DestRoom = getRoom(DID);
                            if ( DestRoom != null && newinput.equalsIgnoreCase(DestRoom.getName())) {
                                if (!d.getlocked()) {
                                    world.getPlayer(userId).setCurrentRoomID(DestRoom.getId());
                                    roomValid = true;
                                    shouldPrintCurrentRoomAfterMove = true;
                                } else {
                                    userStream.printToUser("the door is locked");
                                }
                            }
                        }
                        if (!roomValid) {
                            userStream.printToUser("the room doesnt exist");
                        }
                    }
                    if (!roomValid) {
                        userStream.printToUser("the room doesnt exist");
                    }
                }
            }
        } else if (command == CommandTranslator.LOOK) {
            Room r = getRoom(currentRoom);

            // Prints the description
            String roomDesc = r.getDescription();
            userStream.printToUser(roomDesc);

            // Print the available directions to move
            List<Door> exitList = r.getDoors();
            userStream.printToUser("Exits: ");
            for (Door e : exitList) {
                Room d = getRoom(e.getDestinationRoomId());
                //make a sub routine that takes e.getdirection into a direction then put that subroutine into the code
                userStream.printToUser("    " + commandTranslator.getdirectionstring(e.getDirection()) + " - " + d.getName());
            }

            List<Item> itemList = r.getItems();
            if (itemList != null && !itemList.isEmpty()) {
                userStream.printToUser("Items: ");
                for (Item i : itemList) {
                    userStream.printToUser("    " + i.getName());
                }
            }

            List<NPC> npcList = getNPCsInRoom(currentRoom);
            if (npcList != null && !npcList.isEmpty()) {
                userStream.printToUser("Characters in the room:");
                for (NPC n : npcList) {
                    userStream.printToUser("    " + n.getName());
                }
            }
            List<Player> playerList = getplayersInRoom(currentRoom);
            if (playerList!= null && !playerList.isEmpty()) {
                userStream.printToUser("Players in the room:");

                for (Player p : playerList) {
                    if (!p.getUserId().equalsIgnoreCase(userId)) {
                        userStream.printToUser("    " + p.getUserId());
                    }
                }
            }

        } else if (command == CommandTranslator.PICKUP) {
            String itemName = commandTranslator.getItemTopickup2(input);
            Item item = getItemFromRoomByName(itemName);
            if (item == null) {
                userStream.printToUser("pffff i dont know what youve done");
            } else {
                Inventory inventory = world.getPlayer(userId).getInventory();
                if (inventory.addItem(item)) {
                    userStream.printToUser("Added " + item.getName() + " to your inventory");
                    removeItemFromRoomByName(itemName);
                }
            }
        } else if (command == CommandTranslator.INVENTORY) {
            Inventory inv = world.getPlayer(userId).getInventory();
            List<Item> Items = inv.getItems();
            userStream.printToUser("Inventory: ");

            for (Item i : Items) {
                userStream.printToUser("name: " + i.getName());
            }
            if (Items.isEmpty()) {
                userStream.printToUser("You're inventory is empty");
            }

        } else if (command == CommandTranslator.DROP) {
            String itemName = commandTranslator.getItemTopickup2(input);
            Room r = getRoom(currentRoom);
            List<Item> itemList = r.getItems();
            Item fred = getItemFromInv(itemName);
            if (fred == null) {
                userStream.printToUser("you dont have that item at the moment");
            } else {
                itemList.add(fred);
                removeItemFromInv(itemName);
                userStream.printToUser("yay you dropped an item well done");
            }

        } else if (command == commandTranslator.USE) {
            String[] itemAndDoor = commandTranslator.getitemandoorstring_new(input);
            if (itemAndDoor != null) {
                String itemName = itemAndDoor[0];
                String doorDestination = itemAndDoor[1];
                if (world.getPlayer(userId).getInventory().doesexistByName(itemName)) {
                    Door d = getDoorfromdoorid(currentRoom, doorDestination);
                    if (d != null && d.tryUnlock(itemName)) {
                        Door d2 = getDoor(d.getDestinationRoomId(), flipDirection(d.getDirection()));
                        if (d2 != null && d2.tryUnlock(itemName)) {
                            userStream.printToUser("the door opens");
                        } else {
                            userStream.printToUser("error, check key settings for doors");
                        }
                    } else {
                        userStream.printToUser("the item was innefective");
                    }
                } else {
                    userStream.printToUser("this item is not in your inventory");
                }
            } else {
                userStream.printToUser("thats invalid");
            }
        } else if (command == commandTranslator.TALK) {
            String NpcName = commandTranslator.getItemToPickup(input);
            NPC n = getNPCFromRoom(NpcName, currentRoom);
            if (n != null) {
                String Dialogue = n.getRandomDialog();
                userStream.printToUser(n.getName() + " says: " + Dialogue, TextColours.GREEN);
            } else {
                userStream.printToUser("this persons not in the room with you");
            }
        }else if (command == commandTranslator.HELP) {
            userStream.printToUser("move (direction on compass) move to that place then once it asks for input again type one of the street names the console just showed you");
            userStream.printToUser("look to see your current surroundings and where you can go" );
            userStream.printToUser("pickup (insert item name here) to pickup an item" );
            userStream.printToUser("drop (insert item name here) to drop an item" );
            userStream.printToUser("talk to (insert things.NPC name here) to talk to an things.NPC " );
            userStream.printToUser("inv to look at your inventory" );
            userStream.printToUser("use (insert item name here) on (insert name of thing you'd like to use item on here) to use an item" );
            userStream.printToUser("about to see credits of this game" );
            userStream.printToUser("all of the above excluding inv can be shortened to their first letter e.g (m newport road or p house key)" );
            userStream.printToUser("have fun!!!" );
        }
        else if (command == commandTranslator.ABOUT) {
            userStream.printToUser("an untitled game");
            userStream.printToUser("that is still unfinished and in progress" );
            userStream.printToUser("coded by Nathaniel and Fraser" );
            userStream.printToUser("\nthanks for playing " );
        }
        else {
            userStream.printToUser("I'm sorry, I don't recognise that");
            npcsShouldMove = false;
        }

        if (shouldPrintCurrentRoomAfterMove) {
            printCurrentRoom();
        }

//        if (npcsShouldMove){
//            List<NPC> pncList = world.getNPCList();
//            for (NPC n : pncList) {
//
//                // should this things.NPC move right now?
//                if (n.shouldMove()) {
//                    //userStream.printToUser("----->"+n.getName()+" will move");
//
//                    // which room are they currently in?
//                    Room r = getRoom(n.getCurrentRoomID());
//                    Random rand = new Random();
//                    int randExit = rand.nextInt(r.getDoors().size());
//                    Door randDirection = r.getDoors().get(randExit);
//
//                    // Only move if door not locked
//                    if (!randDirection.getlocked()) {
//                        String newRoomID = randDirection.getDestinationRoomId();
//                        n.setCurrentRoomID(newRoomID);
//                        //userStream.printToUser("----->move worked!");
//                    } else {
//                        //userStream.printToUser("------>room was locked!");
//                    }
//                } else {
//                    //userStream.printToUser("----->"+n.getName()+" not moving");
//                }
//            }
//        }
//
    }

    private void printCurrentRoom() {
        String currentRoom = world.getPlayer(userId).getCurrentRoomID();
        userStream.printToUser("Currently in room: " + getRoom(currentRoom).getName());
    }

    private Room getRoom(String id) {
        List<Room> rooms = world.getRooms();
        for (Room r : rooms) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    private String getCurrentRoomID() {
        return world.getPlayer(userId).getCurrentRoomID();
    }

    private Item getItemFromRoomById(String itemId) {
        Room r = getRoom(getCurrentRoomID());
        List<Item> itemList = r.getItems();
        for (Item i : itemList) {
            if (i.getId().equals(itemId) ){
                return i;
            }

        }
        return null;
    }

    private Item getItemFromRoomByName(String itemName) {
        Room r = getRoom(getCurrentRoomID());
        List<Item> itemList = r.getItems();
        for (Item i : itemList) {
            if (i.getName().equalsIgnoreCase(itemName) ){
                return i;
            }

        }
        return null;
    }


    private Item removeItemFromRoomById(String itemId) {
        Room r = getRoom(getCurrentRoomID());
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
        Room r = getRoom(getCurrentRoomID());
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

    private void removeItemFromInv (String itemName){
        Room r = getRoom(getCurrentRoomID());
        Inventory inventory = world.getPlayer(userId).getInventory();
        List<Item> invList = inventory.getItems();
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
        Inventory inventory = world.getPlayer(userId).getInventory();
        List<Item> invList = inventory.getItems();
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

    public int flipDirection (int direction) {
        if (direction == commandTranslator.NORTH) {
            return commandTranslator.SOUTH;
        } else if (direction == commandTranslator.SOUTH) {
            return commandTranslator.NORTH;
        } else if (direction == commandTranslator.EAST) {
            return commandTranslator.WEST;
        } else if (direction == commandTranslator.WEST) {
            return commandTranslator.EAST;
        } else if (direction == commandTranslator.UP) {
            return commandTranslator.DOWN;
        } else if (direction == commandTranslator.DOWN) {
            return commandTranslator.UP;
        }
        return commandTranslator.ERROR;
    }

    private List<NPC> getNPCsInRoom(String roomID) {
        List<NPC> retNPCList = new ArrayList<>();
        List<NPC> npcList = world.getNPCList();
        for (NPC n : npcList) {
            if (n.getCurrentRoomID().equals(roomID)) {
                retNPCList.add(n);
            }
        }
        return retNPCList;
    }
    private List<Player> getplayersInRoom(String roomID) {
        List<Player> retplayerList = new ArrayList<>();
        List<Player> playerList = world.getListOfPlayersInRoom(roomID);
        for (Player p : playerList) {
            if (p.getCurrentRoomID().equals(roomID)) {
                retplayerList.add(p);
            }
        }
        return retplayerList;
    }

    private NPC getNPCFromRoom(String npcName, String roomID) {
        List<NPC> npcList = world.getNPCList();
        for (NPC n : npcList) {
            if (n.getCurrentRoomID().equals(roomID) && npcName.equalsIgnoreCase(n.getName())) {
                return n;
            }
        }
        return null;
    }

    public List<Room> getRoomDirection( int directionValue ) {
        Room r = getRoom(getCurrentRoomID());
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
