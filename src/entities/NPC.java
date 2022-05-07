package entities;

import java.util.List;
import java.util.Random;

/**
 * NPC - a class for creating non playable characters this is mainly just trying to emulate a player
 * but it plays the game by itself
 * mainly ive done this through lots of random chance for it to do certain things
 * but these chances can be affected
 * by things like being attacked or a player entering the same room as them
 */
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


    public List<Item> getItems(){return items;}

    /**
     * random chance of the NPCs moving to different rooms
     * each NPC has a unique moveChance
     * @return boolean whether it will move or not
     */
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

    /**
     * you can talk to the NPCs
     * like all RPGs ive added some set dialogs that is unique to each NPC
     * when you talk to an NPC it will randomly print one of its set lines to the player talking to it
     * @return the dialog to be printed to the user
     */
    public String getRandomDialog() {
        Random r = new Random();
        int selection = r.nextInt(dialog.size());
        return dialog.get(selection);
    }



}

