package entities;

public class Player {

    private String userId;
    private Inventory inventory;
    private String currentRoomID;
    public String getCurrentRoomID() {return currentRoomID;}
    public void setCurrentRoomID(String roomID) { this.currentRoomID = roomID; }

    public Player(String name) {
        this.userId = name;
        inventory = new Inventory();
        currentRoomID = "1";
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String name) { this.userId = name; }

    public Inventory getInventory() {return inventory;}
}
