import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResourceReader {

    public List<Room> loadRoomsFromResources() {
        List<Room> listOfRooms = new ArrayList<>();
        try {
            InputStream is = Main.class.getResourceAsStream("rooms.json");
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
}
