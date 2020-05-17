public class Player {

    private String name;
    private Inventory inventory;


    public String getName() {
        return name;
    }

    public Inventory getInventory() {return inventory;}
    Player(){
    inventory = new Inventory();
    }
}
