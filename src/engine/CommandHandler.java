package engine;


import entities.*;
import org.w3c.dom.css.Counter;
import users.UserStream;
import util.TextColours;

import java.io.*;
import java.util.*;


import java.io.File;  // Import the File class
import java.io.IOException;

// stuff being used within commandHandler
public class CommandHandler extends Thread {
    private World world;
    private String userId;
    private UserStream userStream;
    private CommandTranslator commandTranslator;
    private String lastRoom;
//stuf held within commandHandler
    public CommandHandler(World world, Player player) {
        this.world = world;
        this.userId = player.getUserId();
        this.userStream = player.getUserStream();
        this.commandTranslator = new CommandTranslator();
        this.lastRoom = "";
    }

    @Override
    public void run() {
        printCurrentRoom();
        while (true) {
            //userStream.printToUser("Input> ", TextColours.RED);
            String input = userStream.readFromUser();
            if (input == null) {
                world.removePlayerFromGame(userId);
                break;
            }
            doUserCommand(input);
        }
    }

//takes an input then finds which command has been inputted and acts upon it (could this be done more efficiently)?
    public void doUserCommand(String input) {
        boolean npcsShouldMove = true;
        boolean shouldPrintCurrentRoomAfterMove = false;

        auditFile file = new auditFile();
        file.writeLogLine(input, userId);


        String currentRoom = world.getPlayer(userId).getCurrentRoomID();

        int command = commandTranslator.getCommand(input);


        if(world.getPlayer(userId).isPar) {
            userStream.printToUser("*youre mind tells you you are paralysed and all struggle is in vain*");

        }
        else if (command == CommandTranslator.MOVE) {

            boolean roomValid = false;
            String directionName = commandTranslator.getDirectionName(input);
            int directionValue = commandTranslator.getEndMovementArg(directionName);

            List<Room> roomOB = getRoomDirection(directionValue);
            userStream.printToUser("You can move to these rooms:");
            List<String> roomNameList = new ArrayList<>();
            Map<String, String> roomIndexToId = new HashMap<>();
            int Counter = 1;
            for (Room i: roomOB){
                String roomName2 = i.getName();
                roomNameList.add(roomName2);
                userStream.printToUser(Counter + ".  " + roomName2);
                roomIndexToId.put(String.valueOf(Counter), i.getId());
                Counter++;
            }
            if (roomNameList.isEmpty()){
                userStream.printToUser("there are no rooms in this direction");
            }
            else {
                int userChoice = Integer.valueOf(userStream.readFromUser());
                String destinationRoomId = roomIndexToId.get(String.valueOf(userChoice));
                Room destinationRoom = getRoom(destinationRoomId);

                if (destinationRoom != null) {
                    Room ro = getRoom(currentRoom);
                    List<Door> doorlist = ro.getDoors();
                    for (Door d : doorlist) {
                        String DID = d.getDestinationRoomId();
                        Room potentialDestRoom = getRoom(DID);
                        if (potentialDestRoom != null && destinationRoom.getId() == potentialDestRoom.getId()) {
                            if (!d.getlocked()) {
                                world.getPlayer(userId).setCurrentRoomID(destinationRoom.getId());
                                roomValid = true;
                                shouldPrintCurrentRoomAfterMove = true;

                                List<NPC> npcList = getNPCsInRoom(destinationRoom.getId());
                                if (npcList != null && !npcList.isEmpty()) {
                                    for (NPC n : npcList) {
                                        attack a = new attack(world, world.getPlayer(userId));
                                        a.NPCAtk(world, world.getPlayer(userId), n);
                                    }
                                }
                            } else {
                                userStream.printToUser("the door is locked");
                            }
                        }
                        else if (!roomValid) {
                            userStream.printToUser("the room doesnt exist");
                        }

                    }
                }
                else if (!roomValid) {
                    userStream.printToUser("the room doesnt exist");
                }
            }

                /*int p = 0;
                for (Room i: roomOB){
                    int num = i.getCounter();
                    int[] CounterArr = new int[10];
                    CounterArr[p] = num;
                    String newinput  = userStream.readFromUser();
                    int count = Integer.parseInt(newinput);
                    p++;
                    for (int c: CounterArr){
                        if (count == CounterArr[c]){
                            Room ro = getRoom(currentRoom);
                            List<Door> doorlist =ro.getDoors();
                            for (Door d : doorlist){
                                String DID = d.getDestinationRoomId();
                                Room DestRoom = getRoom(DID);
                                if( DestRoom != null && count == DestRoom.getCounter()){
                                    if (!d.getlocked()){
                                        world.getPlayer(userId).setCurrentRoomID(DestRoom.getId());
                                        roomValid = true;
                                        shouldPrintCurrentRoomAfterMove = true;
                                        for (Room q: roomOB){
                                            q.clearcounter();
                                        }

                                        List<NPC> npcList = getNPCsInRoom(DestRoom.getId());
                                        if (npcList != null && !npcList.isEmpty()) {
                                            for (NPC n : npcList) {
                                                attack a = new attack(world, world.getPlayer(userId));
                                                a.NPCAtk(world, world.getPlayer(userId), n);
                                            }
                                        }
                                    }else {
                                        userStream.printToUser("the door is locked");
                                    }
                                }
                            }if (!roomValid) {
                                userStream.printToUser("the room doesnt exist");
                            }
                        }
                        if(!roomValid){
                            userStream.printToUser("that room doesnt exist");
                        }
                    }
                }*/


/*                userStream.printToUser("Where would you like to go?");
                //userStream.printToUser("Input> ", TextColours.RED);
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

                                    List<NPC> npcList = getNPCsInRoom(DestRoom.getId());
                                    if (npcList != null && !npcList.isEmpty()) {
                                        for (NPC n : npcList) {
                                            attack a = new attack(world, world.getPlayer(userId));
                                            a.NPCAtk(world, world.getPlayer(userId), n);
                                        }
                                    }

                                } else {
                                    userStream.printToUser("the door is locked");
                                }
                            }
                        }if (!roomValid) {
                            userStream.printToUser("the room doesnt exist");
                        }
                    }
                    if (!roomValid) {
                        userStream.printToUser("the room doesnt exist");
                    }
                }
            }*/


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
            int PlayerHp = world.getPlayer(userId).getHP();
            int maxPlayerHp = world.getPlayer(userId).getMaxHp();
            userStream.printToUser("HP: " + PlayerHp + "/" + maxPlayerHp);
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

            boolean hasOnSuffix = input.contains(" on ");
            String item = null;
            String thing = null;

            if (hasOnSuffix) {
                String[] itemAndDoor = commandTranslator.getitemandoorstring_new(input);
                item = itemAndDoor[0];
                thing = itemAndDoor[1];
            } else {
                item = commandTranslator.getItemTopickup2(input);
            }

            Item itemToUse = getItemFromInv(item);
            if (itemToUse != null) {
                int type = itemToUse.getType();
                switch (type) {
                    case Item.WEAPON:
                    case Item.OTHER:
                        userStream.printToUser("the item was innefective");
                        break;
                    case Item.EFFECT:
       //                 if (item.canBreak){

                    //    }
                        //TODO
                        userStream.printToUser("work in progress");
                        break;
                    case Item.KEY:
                        if (thing != null) {
                            Door d = getDoorfromdoorid(currentRoom, thing);
                            if (d != null && d.tryUnlock(item)) {
                                Door d2 = getDoor(d.getDestinationRoomId(), flipDirection(d.getDirection()));
                                if (d2 != null && d2.tryUnlock(item)) {
                                    userStream.printToUser("the door opens");
                                } else {
                                    userStream.printToUser("error, check key settings for doors");
                                }
                            } else {
                                userStream.printToUser("the door did not unlock");
                            }
                        } else {
                            userStream.printToUser("the door does not exist");
                        }
                        break;
                    case Item.HEAL:
                        //TODO: make healing end effects
                        if (itemToUse.getHeal()){
                            int playerHp = world.getPlayer(userId).getHP();
                            int maxHp = world.getPlayer(userId).getMaxHp();
                            Player player = world.getPlayer(userId);
                            playerHp += itemToUse.getDmg();
                            if (playerHp > maxHp){
                                playerHp = maxHp;
                            }
                            player.setHP(playerHp);
                            userStream.printToUser("you healed for " + itemToUse.getDmg()+" HP");
                            removeItemFromInv(item);
                        } else{
                            userStream.printToUser("this cant heal you.");
                        }
                        break;
                }
            } else {
                userStream.printToUser("this item is not in your inventory");
            }
        } else if (command == commandTranslator.TALK) {
            String NpcName = commandTranslator.getItemToPickup(input);
            NPC n = getNPCFromRoom(NpcName, currentRoom);
            if (n != null) {
                String Dialogue = n.getRandomDialog();
                userStream.printToUser(n.getName() + " says: " + Dialogue, TextColours.RED);
            } else {
              Player p = getPlayerFromRoom(NpcName, currentRoom);
              Player  playerName = world.getPlayer(NpcName);
                if (p != null) {
                    if (!(NpcName.equalsIgnoreCase(userId))) {
                        boolean end = false;
                        userStream.printToUser("youve connected to " + playerName.getUserId());
                        while (!end) {

                            String userType = userStream.readFromUser();
                            if (userType.equalsIgnoreCase("/end")) {
                                end = true;
                                userStream.printToUser("disconnected from " + playerName.getUserId());
                                playerName.getUserStream().printToUser("disconnected from " + userId, TextColours.RED);
                            } else {
                                String userTypeFrom = ("message from " + userId + ": " + userType);
                                playerName.getUserStream().printToUser(userTypeFrom, TextColours.RED);
                            }
                        }
                    }
                    else{
                        userStream.printToUser("you cant talk to yourself it wouldnt be very interesting");
                    }
                }
                else{


                userStream.printToUser("this persons not in the room with you");
            }

            }



        }
        else if (command == CommandTranslator.ATTACK) {
            Player me = world.getPlayer(userId);
            Room r = getRoom(currentRoom);

            attack attack = new attack(world, me);
            attack.attack(r, input);
        }


        else if (command == commandTranslator.HELP) {
            //TODO: update for latest commands

            userStream.printToUser("move (direction on compass) move to that place then once it asks for input again type one of the street names the console just showed you");
            userStream.printToUser("look to see your current surroundings and where you can go" );
            userStream.printToUser("pickup (insert item name here) to pickup an item" );
            userStream.printToUser("drop (insert item name here) to drop an item" );
            userStream.printToUser("talk to (insert things.NPC name here) to talk to an things.NPC " );
            userStream.printToUser("inv to look at your inventory and HPf" );
            userStream.printToUser("use (insert item name here) on (insert name of thing you'd like to use item on here) to use an item" );
            userStream.printToUser("about to see credits of this game" );
            userStream.printToUser("all of the above excluding inv can be shortened to their first letter e.g (m east or p house key)" );
            userStream.printToUser("have fun!!!" );
        }
        else if (command == commandTranslator.ABOUT) {
            userStream.printToUser("an untitled game");
            userStream.printToUser("that is still unfinished and in progress" );
            userStream.printToUser("coded by Nathaniel Tomsett" );
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
        Player player = world.getPlayer(userId);
        if (player != null) {
            String currentRoom = world.getPlayer(userId).getCurrentRoomID();
            userStream.printToUser("Currently in room: " + getRoom(currentRoom).getName());
        }
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

// why is this not being used is it unnecessary should it be deleted?
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
        List<Player> playerList = world.getListOfPlayersInRoom(roomID, userId);
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

    private Player getPlayerFromRoom(String PlayerName, String roomID) {
        List<Player> PlayerList = world.getListOfPlayersInRoom(roomID, userId);
        for (Player p : PlayerList) {
            if (p.getCurrentRoomID().equals(roomID) && PlayerName.equalsIgnoreCase(p.getUserId())) {
                return p;
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
