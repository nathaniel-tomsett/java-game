package entities;

public class Item {

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

    Item(String i, String n, int t, int dm, boolean cH, boolean cB) {
        id = i;
        name = n;
        type = t;
        dmg = dm;
        canHeal = cH;
        canBleed = cB;
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
}
