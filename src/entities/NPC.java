package entities;

import users.UserStream;

import java.util.List;
import java.util.Random;

public class NPC {
    Random rand = new Random();
    int randBaseHP = rand.nextInt(15);




    private String id;
    private String name;
    private int moveChance;
    private String currentRoomID;
    private List<String> dialog;
    private int HP;



    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurrentRoomID() {
        return currentRoomID;
    }

    public void setCurrentRoomID(String roomID) {
        this.currentRoomID = roomID;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int h) {
        HP = h;
    }

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

    NPC(String i, String n, int m, String c) {
        this.id = i;
        this.name = n;
        this.moveChance = m ;
        this.currentRoomID = c ;
        Random rand = new Random();
        int randBaseHP = rand.nextInt(15);
        this.HP = randBaseHP +5;
    }

    public String getRandomDialog() {
        Random r = new Random();
        int selection = r.nextInt(dialog.size());
        return dialog.get(selection);
    }

    }

