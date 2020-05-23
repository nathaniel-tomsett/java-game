package engine;

import entities.Door;
import entities.NPC;
import entities.Player;
import entities.Room;
import users.UserConnections;
import util.ResourceReader;

import java.util.*;
import java.util.HashMap;

public class World extends Thread {

    // List of rooms, containing items and exits
    private List<Room> roomList;

    // List of NPCs
    private List<NPC> npcList;

    // Hash of players in the map
    private Map<String, Player> playersHash = new HashMap<>();

    public World() {
        this.roomList = new ArrayList<>();
        this.npcList = new ArrayList<>();
        loadTestWorld();
        start();
    }

    private Room getRoom(String id) {
        List<Room> rooms = getRooms();
        for (Room r : rooms) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public void  run() {
        List<NPC> pncList = getNPCList();
        while (true) {


            for (NPC n : pncList) {

                // should this things.NPC move right now?
                if (n.shouldMove()) {
                    System.out.println("----->" + n.getName() + " will move");

                    // which room are they currently in?
                    Room r = getRoom(n.getCurrentRoomID());
                    Random rand = new Random();
                    int randExit = rand.nextInt(r.getDoors().size());
                    Door randDirection = r.getDoors().get(randExit);

                    // Only move if door not locked
                    if (!randDirection.getlocked()) {
                        String newRoomID = randDirection.getDestinationRoomId();
                        n.setCurrentRoomID(newRoomID);
                        System.out.println("----->move worked!");
                    } else {
                        System.out.println("------>room was locked!");
                    }
                } else {
                    System.out.println("----->" + n.getName() + " not moving");
                }
            }

            try {
                sleep(10000);
            } catch (InterruptedException e) {

            }

        }

    }


    public void addPlayer(String userId, Player player) {
        playersHash.put(userId, player);
    }

    public Player getPlayer(String userId) {
        return playersHash.get(userId);
    }

    public List<Player> getListOfPlayersInRoom(String roomId){
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

    private void loadTestWorld() {
        ResourceReader resourceReader = new ResourceReader();
        roomList = resourceReader.loadRoomsFromResources();
        npcList = resourceReader.loadNPCs();
    }

    public List<Room> getRooms() {
        return roomList;
    }

    public List<NPC> getNPCList() {
        return npcList;
    }

    public static void main(String[] args) {
        UserConnections userIO = new UserConnections(true);
        userIO.start();
    }
}