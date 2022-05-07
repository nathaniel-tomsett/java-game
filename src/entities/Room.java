package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Room - represents a Room in the game that players, NPCs and items can be in
 */
public class Room{

    private String id;
    private String name;
    private String description;

    private List<Item> items = new ArrayList<>();
    private List<Door> doors = new ArrayList<>();


    public List<Item> getItems() {
        return items;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public String getId() {
        return id;
    }

    public String getName() { return name;}
    public String getDescription () { return description;}


}

