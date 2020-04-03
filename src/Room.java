import java.util.ArrayList;
import java.util.List;

public class Room {

    private String id;
    private String name;
    private String description;

    private List<Item> items;
    private List<Exit> exits;

    Room(String i, String n, String d) {
        id = i;
        name = n;
        items = new ArrayList<Item>();
        exits = new ArrayList<Exit>();
        description = d;
    }

    public void setItems(List<Item> i) {
        items = i;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Exit> getExits() {
        return exits;
    }

    public String getDestination(int direction) {
        for (Exit e : exits) {
            if (direction == e.getDirection()) {
                return e.getDestinationRoomId();
            }
        }
        return "";
    }

    public void setExit(List<Exit> i) {
        exits = i;
    }
    public String getId() {
        return id;
    }

    public String getName() { return name;}
        public String getDescription () { return description;
        }
    }
