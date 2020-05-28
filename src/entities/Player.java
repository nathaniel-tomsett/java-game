package entities;

import users.UserStream;

public class Player {

    private String name;
    private Inventory inventory;
    private String currentRoomID;
    private UserStream userStream;
    public String getCurrentRoomID() {return currentRoomID;}
    public void setCurrentRoomID(String roomID) { this.currentRoomID = roomID; }

    public Player(String name, UserStream userStream) {
        this.name = name;
        this.inventory = new Inventory();
        this.currentRoomID = "1";
        this.userStream = userStream;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) { this.name = name; }
    public Inventory getInventory() { return inventory; }
    public UserStream getUserStream() { return userStream; }
}
