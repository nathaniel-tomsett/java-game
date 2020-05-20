package util;

import com.google.gson.Gson;
import entities.NPC;
import entities.Room;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResourceReader {

    public List<Room> loadRoomsFromResources() {

        // Load rooms...
        List<Room> listOfRooms = new ArrayList<>();
        try {
            InputStream is = ResourceReader.class.getResourceAsStream("../rooms2.json");
            while (is.available() > 0) {
                byte[] input = new byte[is.available()];
                is.read(input);
                String roomsJson = new String(input);

                Gson gson = new Gson();
                Room[] rooms = gson.fromJson(roomsJson, Room[].class);
                listOfRooms = Arrays.asList(rooms);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return listOfRooms;
    }

    public List<NPC> loadNPCs() {
        List<NPC> listOfNPCs = new ArrayList<>();
        try {
            InputStream is = ResourceReader.class.getResourceAsStream("../npcs.json");
            while (is.available() > 0) {
                byte[] input = new byte[is.available()];
                is.read(input);
                String npcJson = new String(input);

                Gson gson = new Gson();
                NPC[] rooms = gson.fromJson(npcJson, NPC[].class);
                listOfNPCs = Arrays.asList(rooms);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return listOfNPCs;
    }
}
