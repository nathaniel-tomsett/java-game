package entities;

import users.UserStream;
import java.util.Random;

/**
 * Player - a class to create Player objects that stores all relevant info about the player
 * like name and Health and their current location
 */
public class Player {

    private String userId;
    private Inventory inventory;
    private String currentRoomID;
    private UserStream userStream;
    private int health;
    private int maxHp;
    public boolean godmode;
    public boolean isPar;

    public void setCurrentRoomID(String roomID) { this.currentRoomID = roomID; }
    public String getCurrentRoomID() {return currentRoomID;}

    public void setgodmode (boolean godTrue){ this.godmode = godTrue;}
    public boolean getgodemode (){return godmode;}

    public void setIsPar (boolean canPar){this.isPar = canPar;}
    public boolean getIsPar () {return isPar;}


    public Player(String name, UserStream userStream) {
        Random rand = new Random();
        int randBaseHP = rand.nextInt(15);
        this.userId = name;
        this.inventory = new Inventory();
        this.currentRoomID = "1";
        this.userStream = userStream;
        this.health = randBaseHP + 5;
        this.maxHp = this.health;
    }

    public String getUserId() {
        return userId;
    }
    public int getHP() {return health;}
    public void setHP(int h) { health = h;}
    public Inventory getInventory() { return inventory; }
    public UserStream getUserStream() { return userStream; }
    public int getMaxHp() {return maxHp;}
    public void setMaxHp(int mh) {maxHp = mh;}

}
