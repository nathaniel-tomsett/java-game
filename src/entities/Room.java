package entities;

import java.util.ArrayList;
import java.util.List;

public class Room{

    private String id;
    private String name;
    private String description;

    private List<Item> items = new ArrayList<>();
    private List<Door> doors = new ArrayList<>();

//    things.Room(String i, String n, String d) {
//        id = i;
//        name = n;
//        items = new ArrayList<>();
//        doors = new ArrayList<>();
//        description = d;
//        npcs = new ArrayList<>();
//    }

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
