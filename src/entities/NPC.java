package entities;

import java.util.List;
import java.util.Random;

public class NPC {
    private String id;
    private String name;
    private int moveChance;
    private String currentRoomID;
    private List<String> dialog;
    private int health;

    public String getId(){return id;}
    public String getName() {return name;}
    public String getCurrentRoomID() {return currentRoomID;}
    public void setCurrentRoomID(String roomID) { this.currentRoomID = roomID; }
    public int getHP() {return health;}

     public boolean shouldMove (){
        Random rand = new Random();
        int randInt = rand.nextInt(99);
        if (randInt <= moveChance){
            return true;

        }
        else {
            return false;
        }
     }

    NPC(String i, String n, int m) {
        id = i;
        name = n;
        moveChance = m ;
    }

    public String getRandomDialog() {
        Random r = new Random();
        int selection = r.nextInt(dialog.size());
        return dialog.get(selection);
    }

}
