package entities;

public class Item {
    /**
     * Item - this is a class for creating Item objects stores all their relevant info
     * like the name, extra damage they do or any special effects it may have
     */

    public static final int WEAPON = 1;
    public static final int EFFECT = 2;
    public static final int KEY = 3;
    public static final int OTHER = 4;
    public static final int HEAL = 5;


    private String id;
    private String name;
    private int type;
    private int dmg;
    private boolean canHeal;
    private boolean canBleed;
    private boolean canBreak;
    private boolean canPar;

    Item(String i, String n, int t, int dm, boolean cH, boolean cBl, boolean cBr, boolean cP) {
        id = i;
        name = n;
        type = t;
        dmg = dm;
        canHeal = cH;
        canBleed = cBl;
        canBreak = cBr;
        canPar = cP;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }
    public int getDmg(){return dmg;}
    public  boolean getHeal(){return canHeal;}
    public boolean getBleed(){return canBleed;}
    public boolean getBreak(){return canBreak;}
    public boolean getPar(){return canPar;}
}
