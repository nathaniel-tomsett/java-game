import java.util.ArrayList;
import java.util.List;

public class Room {

    private String id;
    private String name;
    private String description;

    private List<Item> items;
    private List<Door> doors;

    Room(String i, String n, String d) {
        id = i;
        name = n;
        items = new ArrayList<Item>();
        doors = new ArrayList<Door>();
        description = d;
    }

    public void setItems(List<Item> i) {
        items = i;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public String getDestination(int direction) {
        for (Door e : doors) {
            if (direction == e.getDirection()) {
                return e.getDestinationRoomId();
            }
        }
        return "";
    }

    public void setExit(List<Door> i) {
        doors = i;
    }
    public String getId() {
        return id;
    }

    public String getName() { return name;}
        public String getDescription () { return description;
        }
    }
