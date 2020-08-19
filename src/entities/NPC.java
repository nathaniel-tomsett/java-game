package entities;

import engine.World;
import users.UserStream;

import java.util.List;
import java.util.Random;

public class NPC {
    private String id;
    private String name;
    private int moveChance;
    private int AtkChance;
    private int AtkDmg;
    private double AtkChanceMult;
    private String currentRoomID;
    private List<String> dialog;
    private int HP;
    private List<Item> items;


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

    public int getAtkChance() {
        return AtkChance;
    }

    public void setAtkChance(int AC) {
        AtkChance = AC;
    }

    public int getAtkDmg() {
        return AtkDmg;
    }

    public void setAtkDmg(int AD) {
        AtkDmg = AD;
    }

    public double getAtkChanceMult() {
        return AtkChanceMult;
    }

    public void setAtkChanceMult(int ACM) {
        AtkChanceMult = ACM;
    }

    public List<Item> getitems(){return items;}

    public boolean shouldMove() {
        Random rand = new Random();
        int randInt = rand.nextInt(99);
        if (randInt <= moveChance) {
            return true;

        } else {
            return false;
        }
    }

    NPC(String i, String n, int m, String c, int AC, int AD, double ACM) {
        this.id = i;
        this.name = n;
        this.moveChance = m;
        this.currentRoomID = c;
        this.AtkChance = AC;
        this.AtkDmg = AD;
        this.AtkChanceMult = ACM;
    }

    public String getRandomDialog() {
        Random r = new Random();
        int selection = r.nextInt(dialog.size());
        return dialog.get(selection);
    }
}

