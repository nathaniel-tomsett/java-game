import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private final int MAX_SIZE = 8;
    private List<Item> items;

    public Inventory() {
        items = new ArrayList<Item>();
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean addItem(Item i) {
        if (items.size() < MAX_SIZE) {
            items.add(i);
            return true;
        } else {
            System.out.println("Inventory is full");
            return false;
        }
    }

    public void removeItem(Item i) {
        if (items.size() == 0) {
            System.out.println("Inventory is empty");
        }
        items.remove(i);
    }
    public boolean doesexistByName(String itemName){
        for (Item i : items ){
            if (i.getName().equalsIgnoreCase(itemName)){
                return true;
            }
        }
        return false;

    }
}
