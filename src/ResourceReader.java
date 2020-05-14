import com.google.gson.Gson;

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
            InputStream is = ResourceReader.class.getResourceAsStream("BH.json");
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

        // Load NPCs and add to their starting rooms
        List<NPC> listOfNPCs = new ArrayList<>();
        try {
            InputStream is = ResourceReader.class.getResourceAsStream("npcs.json");
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


        if (!listOfNPCs.isEmpty()) {
            for (NPC n : listOfNPCs) {
                for (Room r : listOfRooms) {
                    if (r.getId().equalsIgnoreCase(n.getStartingRoomID())) {
                        r.getNpcs().add(n);
                    }
                }
            }
        }


        return listOfRooms;
    }
}
