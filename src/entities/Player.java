package entities;

import users.UserStream;
import java.util.Random;
public class Player {

    private String userId;
    private Inventory inventory;
    private String currentRoomID;
    private UserStream userStream;
    private int health;
    public String getCurrentRoomID() {return currentRoomID;}
    public void setCurrentRoomID(String roomID) { this.currentRoomID = roomID; }

    public Player(String name, UserStream userStream) {
        Random rand = new Random();
        int randBaseHP = rand.nextInt(15);
        this.userId = name;
        this.inventory = new Inventory();
        this.currentRoomID = "1";
        this.userStream = userStream;
        this.health = randBaseHP + 5;
    }

    public String getUserId() {
        return userId;
    }
    public int getHP() {return health;}
    public void setHP(int h) { health = h;}
    public Inventory getInventory() { return inventory; }
    public UserStream getUserStream() { return userStream; }

}
