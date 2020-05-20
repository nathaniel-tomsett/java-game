package entities;

public class Player {

    private String name;
    private Inventory inventory;
    private String currentRoomID;
    public String getCurrentRoomID() {return currentRoomID;}
    public void setCurrentRoomID(String roomID) { this.currentRoomID = roomID; }

    public Player(String name) {
        this.name = name;
        inventory = new Inventory();
        currentRoomID = "1";
    }

    public String getName() {
        return name;
    }
    public void setName(String name) { this.name = name; }

    public Inventory getInventory() {return inventory;}
}
