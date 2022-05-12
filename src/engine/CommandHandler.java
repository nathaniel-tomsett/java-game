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
//stuff held within commandHandler
    public CommandHandler(World world, Player player) {
        this.world = world;
        this.userId = player.getUserId();
        this.userStream = player.getUserStream();
        this.commandTranslator = new CommandTranslator();
        this.lastRoom = "";
    }

    /**
     *this is the main gameplay loop
     * the Player is told the room they are in
     * given the ability to put an input
     * and if they have disconnected removes them from the game
     * the input is then ran through DoUserCommand (see below)
     * this will then do whatever the Player inputted and loop again once done
     * if the Player arent dead of course
     */
    @Override
    public void run() {
        printCurrentRoom();
        while (true) {
            String input = userStream.readFromUser();
            if (input == null) {
                world.removePlayerFromGame(userId);
                break;
            }
            doUserCommand(input);
        }
    }

    /**
     * takes an input then finds which Action has been inputted and acts upon it
     * finding the correct case statement for the action that was inputted
     * @param input a command inputted by the Player
     */

    public void doUserCommand(String input) {
        boolean npcShouldMove = true;
        boolean shouldPrintCurrentRoomAfterMove = false;

        auditFile file = new auditFile();
        file.writeLogLine(input, userId);


        String currentRoom = world.getPlayer(userId).getCurrentRoomID();

        int command = commandTranslator.getCommand(input);

        if  (world.getPlayer(userId).isPar){
            userStream.printToUser("*your mind tells you you are paralysed and all struggle is in vain*");
        }
    else{
        switch(command) {

            case CommandTranslator.MOVE:
                Movement(input, currentRoom);
                shouldPrintCurrentRoomAfterMove = true;
                break;

            case CommandTranslator.LOOK:
                Looking(currentRoom);
                break;
            case CommandTranslator.PICKUP:
                PickUp(input);
                break;

            case CommandTranslator.INVENTORY:
                CheckInv();
                break;

            case CommandTranslator.DROP:
                DropItem(input, currentRoom);
                break;

            case CommandTranslator.USE:
                UseItem(input, currentRoom);
                break;
            case CommandTranslator.TALK:
                Talk(input, currentRoom);
                break;
            case CommandTranslator.ATTACK:
                Player me = world.getPlayer(userId);
                Room r = getRoom(currentRoom);

                attack attack = new attack(world, me);
                attack.attack(r, input);
                break;
            case CommandTranslator.HELP:
                userStream.printToUser("move (direction on compass) move to that place then once it asks for input again type one of the street names the console just showed you");
                userStream.printToUser("look to see your current surroundings and where you can go");
                userStream.printToUser("pickup (insert item name here) to pickup an item");
                userStream.printToUser("drop (insert item name here) to drop an item");
                userStream.printToUser("talk to (insert things.NPC name here) to talk to an things.NPC ");
                userStream.printToUser("inv to look at your inventory and HPf");
                userStream.printToUser("use (insert item name here) on (insert name of thing you'd like to use item on here) to use an item");
                userStream.printToUser("Attack (insert NPC name here) with (insert Item name here) to attack a Npc/Player");
                userStream.printToUser("for attacking you may attack with or without an item");
                userStream.printToUser("about to see credits of this game");
                userStream.printToUser("all of the above excluding inv can be shortened to their first letter e.g (m east or p house key)");
                userStream.printToUser("have fun!!!");
            case CommandTranslator.ABOUT:
                userStream.printToUser("Traveller");
                userStream.printToUser("A multi user dungeon experience ");
                userStream.printToUser("Made For AQA CompSci NEA");
                userStream.printToUser("coded by Nathaniel Tomsett");
                userStream.printToUser("\nthanks for playing ");
            default:
                userStream.printToUser("I'm sorry, I don't recognise that");
                break;

        }
        if (shouldPrintCurrentRoomAfterMove) {
            printCurrentRoom();
        }
    }
}

    /**
     * this function is one of the most key in the game
     * it allows the player to move in any of 6 directions
     * it then finds all possible rooms in that direction that can directly be reached from Players current room
     * and then lets choose which room to go to that is in that direction
     * @param input this is the first input ("move (Direction)") used to figure out direction of travel
     * @param currentRoom used to know which rooms that can possibly be moved to from the one currently occupied
     */
    public void Movement(String input, String currentRoom) {
        boolean roomValid = false;
        String directionName = commandTranslator.getDirectionName(input);
        int directionValue = commandTranslator.getEndMovementArg(directionName);

        List<Room> roomOB = getRoomDirection(directionValue);
        userStream.printToUser("You can move to these rooms:");
        List<String> roomNameList = new ArrayList<>();
        Map<String, String> roomIndexToId = new HashMap<>();
        int Counter = 1;
        for (Room i : roomOB) {
            String roomName2 = i.getName();
            roomNameList.add(roomName2);
            userStream.printToUser(Counter + ".  " + roomName2);
            roomIndexToId.put(String.valueOf(Counter), i.getId());
            Counter++;
        }
        if (roomNameList.isEmpty()) {
            userStream.printToUser("there are no rooms in this direction");
        } else {
            int userChoice;
            try {
                userChoice = Integer.valueOf(userStream.readFromUser());
            } catch (NumberFormatException e) {
                userStream.printToUser("not a valid number");
                return;
            }
            String destinationRoomId = roomIndexToId.get(String.valueOf(userChoice));
            Room destinationRoom = getRoom(destinationRoomId);

            if (destinationRoom != null) {

                Room ro = getRoom(getCurrentRoomID());
                List<Door> doorList = ro.getDoors();
                for (Door d : doorList) {
                    String DID = d.getDestinationRoomId();
                    Room potentialDestRoom = getRoom(DID);
                    if (potentialDestRoom != null && destinationRoom.getId() == potentialDestRoom.getId()) {
                        if (!d.getlocked()) {
                            world.getPlayer(userId).setCurrentRoomID(destinationRoom.getId());
                            roomValid = true;


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

                }
            } else if (!roomValid) {
                userStream.printToUser("the room doesn't exist");
            }
        }
    }

    /**
     * this function will give the player a short description about the room
     * and what NPCS and ITEMS and Players are in it
     * @param currentRoom used to deduce which room the player is looking at
     */
    public void Looking(String currentRoom){
        Room r = getRoom(currentRoom);

        // Prints the description
        String roomDesc = r.getDescription();
        userStream.printToUser(roomDesc);

        // Print the available directions to move
        List<Door> exitList = r.getDoors();
        userStream.printToUser("Exits: ");
        for (Door e : exitList) {
            Room d = getRoom(e.getDestinationRoomId());
            //make a sub routine that takes e.getDirection into a direction then put that subroutine into the code
            userStream.printToUser("    " + commandTranslator.getDirectionString(e.getDirection()) + " - " + d.getName());
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
        List<Player> playerList = getPlayersInRoom(currentRoom);
        if (playerList!= null && !playerList.isEmpty()) {
            userStream.printToUser("Players in the room:");

            for (Player p : playerList) {
                if (!p.getUserId().equalsIgnoreCase(userId)) {
                    userStream.printToUser("    " + p.getUserId());
                }
            }
        }
    }

    /**
     * this function lets the player pickup an item from the room
     * and put it into the Players inventory
     * @param input example ("pickup (Itemname)")
     */
  public void PickUp(String input){
        String itemName = commandTranslator.getItemToPickup(input);
        Item item = getItemFromRoomByName(itemName);
        if (item == null) {
            userStream.printToUser("item not found!");
        } else {
            Inventory inventory = world.getPlayer(userId).getInventory();
            if (inventory.addItem(item)) {
                userStream.printToUser("Added " + item.getName() + " to your inventory");
                removeItemFromRoomByName(itemName);
            }
        }
    }

    /**
     *this function will show the player some information about themselves
     * (e.g. health and ITEMS currently held)
     */
   public void CheckInv(){
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
    }

    /**
     * this function lets the player drop an item from your inventory
     * into the room you are currently in
     * @param input example ("Drop (Itemname)")
     * @param currentRoom used to know which room the item needs to be placed into when dropped
     */
    public void DropItem(String input, String currentRoom){
        String itemName = commandTranslator.getItemToPickup(input);
        Room r = getRoom(currentRoom);
        List<Item> itemList = r.getItems();
        Item fred = getItemFromInv(itemName);
        if (fred == null) {
            userStream.printToUser("you don't have that item at the moment");
        } else {
            itemList.add(fred);
            removeItemFromInv(itemName);
            userStream.printToUser("yay you dropped an item well done");
        }
    }

    /**
     * this function allows you to use an item
     * this most commonly is a key on a door to unlock it
     * @param input Use (itemName) [on (a thing)] <- this bit is not always neccessary
     * @param currentRoom used to locate where the Player is to make sure the Player are physically able to perform the action the Player inputted
     */
    public void UseItem(String input, String currentRoom){

        boolean hasOnSuffix = input.contains(" on ");
        String item = null;
        String thing = null;

        if (hasOnSuffix) {
            String[] itemAndDoor = commandTranslator.getItemAndDoorString(input);
            item = itemAndDoor[0];
            thing = itemAndDoor[1];
        } else {
            item = commandTranslator.getItemToPickup(input);
        }

        Item itemToUse = getItemFromInv(item);
        if (itemToUse != null) {
            int type = itemToUse.getType();
            switch (type) {
                case Item.WEAPON:
                case Item.OTHER:
                    userStream.printToUser("the item was ineffective");
                    break;
                case Item.EFFECT:
                    //                 if (item.canBreak){

                    //    }
                    //TODO
                    userStream.printToUser("work in progress");
                    break;
                case Item.KEY:
                    if (thing != null) {
                        Door d = getDoorFromDoorId(currentRoom, thing);
                        if (d != null && d.tryUnlock(item)) {
                            Door d2 = getDoor(d.getDestinationRoomId(), commandTranslator.flipDirection(d.getDirection()));
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
    }

    /**
     * allows a Player to talk to another player if they are in the same room
     * or allows a Player to recieve a random line of dialogue from an NPC if they are in the same room
     * @param input  Talk to (Player/NPC name here)
     * @param currentRoom used to find out if the target is in the same room as the player
     */
    public void Talk(String input,String currentRoom){

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
                    userStream.printToUser("you've connected to " + playerName.getUserId());
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
                    userStream.printToUser("you cant talk to yourself it wouldn't be very interesting");
                }
            }
            else{


                userStream.printToUser("this persons not in the room with you");
            }

        }

    }


    /**
     * at this point i think enough parsers and getters and such have been shown
     * so the rest of this will not be commented
     * it is mostly parsers, getters, removes and Adds
     * which have been displayed many times before
     * nothing here is new so should be pretty easily understandable
     */






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
    private Door getDoorFromDoorId (String roomId,String destinationRoom){
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
    private List<Player> getPlayersInRoom(String roomID) {
        List<Player> retPlayerList = new ArrayList<>();
        List<Player> playerList = world.getListOfPlayersInRoom(roomID, userId);
        for (Player p : playerList) {
            if (p.getCurrentRoomID().equals(roomID)) {
                retPlayerList.add(p);
            }
        }
        return retPlayerList;
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
