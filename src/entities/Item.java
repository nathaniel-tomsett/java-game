package entities;

public class Item {

    public static final int WEAPON = 1;
    public static final int ANIMAL = 2;
    public static final int KEY = 3;
    public static final int OTHER = 4;

    private String id;
    private String name;
    private int type;

    Item(String i, String n, int t) {
        id = i;
        name = n;
        type = t;
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

}
