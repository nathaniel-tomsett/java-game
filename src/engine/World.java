package engine;

import entities.Door;
import entities.NPC;
import entities.Player;
import entities.Room;
import users.userConnections;
import util.ResourceReader;

import java.sql.SQLException;
import java.util.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class World extends Thread {

    // List of rooms, containing items and exits
    private List<Room> roomList;

    // List of NPCs
    private List<NPC> npcList;

    // Hash of players in the map
    private ConcurrentHashMap<String, Player> playersHash = new ConcurrentHashMap<>();

    public World() {
        this.roomList = new ArrayList<>();
        this.npcList = new ArrayList<>();
        loadTestWorld();
        start();
    }

    /**
     *this cycles through the list of rooms and finds the room that the input searched for
     * @param id
     * @return either returns the Room being searched for or null if the room doesn't exist
     */
    private Room getRoom(String id) {
        List<Room> rooms = getRooms();
        for (Room r : rooms) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    /**
     * this is used as the Single thread that runs all the NPCs it runs every 10000 ms
     * and goes through every every NPC and checks if they should move
     * and where they should move if they do
     * all this is done using the Random class that randomly generates numbers between set boundaries
     *
     */
    public void  run() {
        List<NPC> pncList = getNPCList();
        while (true) {

            for (NPC n : pncList) {

                // should this thing.NPC move right now?
                if (n.shouldMove()) {
                    //System.out.println("----->" + n.getName() + " will move");

                    // which room are they currently in?
                    Room r = getRoom(n.getCurrentRoomID());
                    Random rand = new Random();
                    int randExit = rand.nextInt(r.getDoors().size());
                    Door randDirection = r.getDoors().get(randExit);

                    // Only move if door not locked
                    if (!randDirection.getlocked()) {
                        String newRoomID = randDirection.getDestinationRoomId();
                        n.setCurrentRoomID(newRoomID);
                    }
                }
            }

            try {
                sleep(10000);
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     *this takes some player info and the player object
     * then adds it to a hashmap of all Players Currently online playing the game
     * @param userId UserID the way to Identify the Player Obj
     * @param player the player Obj - the important info being stored
     */
    public void addNewPlayer(String userId, Player player) {
        playersHash.put(userId, player);
        CommandHandler handler = new CommandHandler(this, player);
        handler.start();
    }

    /**
     * this removes a Player from the game getting them out of the hashmap
     * and killing their stream so the Player no longer has a form of communication to the game
     * @param userId ID used to find the Player Obj in the Hash Map
     */
    public void removePlayerFromGame(String userId) {
        Player removing = playersHash.get(userId);
        if (removing != null) {
            removing.getUserStream().killStream();
        }
        playersHash.remove(userId);
    }

    /**
     *this is used for when an NPC dies so it is removed from the game
     * and can no longer be interacted with
     * @param toRemove the Target NPC that is to be removed
     */
    public void removeNpc(NPC toRemove) {
        npcList.remove(toRemove);
    }

    /**
     * @param userId used to Find the Player within the Hashmap
     * @return the Player Obj associated with the given userId
     */
    public Player getPlayer(String userId) {
        return playersHash.get(userId);
    }

    /**
     * this is just used for the Audit log to gather information on where players are
     * mainly used for maintenance and testing
     * @param userId used to Find the Player within the Hashmap
     */
    public void dumpPlayersHash(String userId) {
        Set<String>   playersKey = playersHash.keySet();
        for (String p : playersKey){
            Player player = playersHash.get(p);
            new entities.auditFile().writeLogLine(
                    "Player name:" + player.getUserId() + " room: " + player.getCurrentRoomID(), userId);
        }
    }

    /**
     * loops through all players in the Hashmap then checks if they are in a certain room
     * then puts all players where this is true into a list
     * @param roomId used to identify a room here it is being used as the room to search for
     * @param forUserId this is for the AuditLog to be able to record where the Players currently are
     * @return this list is outputted.
     * these are the players that can currently interact with each other but no one else
     * since players can only interact with those in the same room as each other
     */
    public List<Player> getListOfPlayersInRoom(String roomId, String forUserId){
        new entities.auditFile().writeLogLine("Getting list of players in the room", forUserId);
        dumpPlayersHash(forUserId);

        List<Player> playerList = new ArrayList<>();
      Set<String>   playersKey = playersHash.keySet();
      for (String p : playersKey){
         Player player = playersHash.get(p);
          if (player.getCurrentRoomID().equalsIgnoreCase(roomId)){
              playerList.add(player);
          }
      }
      return playerList;
    }

    /**
     * this loads the world by making a new resourceReader
     * then takes the Lists returned by the resourceloader and put them into variables
     */
    private void loadTestWorld() {
        ResourceReader resourceReader = new ResourceReader();
        roomList = resourceReader.loadRoomsFromResources();
        npcList = new LinkedList<>(resourceReader.loadNPCs());
    }

    /**
     * these next two do exactly as they say
     */
    public List<Room> getRooms() {
        return roomList;
    }

    public List<NPC> getNPCList() {
        return npcList;
    }

    /**
     *this is the Java Main entry point where the whole program starts off
     * it initialises a World
     * console off is anOption to disable console user (defaults to console enabled)
     * then the program starts listening for users that want to connect
     */
    public static void main(String[] args) throws SQLException {
        World world = new World();
        boolean consoleOff = args.length > 0 && args[0].equalsIgnoreCase("consoleOff");
        userConnections userIO = new userConnections(world, consoleOff);
        userIO.startListeningForUsers();
    }
}