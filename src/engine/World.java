package engine;

import entities.NPC;
import entities.Player;
import entities.Room;
import users.UserConnections;
import util.ResourceReader;

import java.util.*;
import java.util.HashMap;

public class World {

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
    }

    public void addPlayer(String userId, Player player) {
        playersHash.put(userId, player);
    }

    public Player getPlayer(String userId) {
        return playersHash.get(userId);
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