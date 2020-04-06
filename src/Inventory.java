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

    public void addItem(Item i) {
        if (items.size() < MAX_SIZE) {
            items.add(i);
        } else {
            System.out.println("Inventory is full");
        }
    }

    public void removeItem(Item i) {
        if (items.size() == 0) {
            System.out.println("Inventory is empty");
        }
        items.remove(i);
    }
    public boolean doesexist(String itemId){
        for (Item i : items ){
            if (i.getId().equals(itemId)){
                return true;
            }
        }
        return false;

    }
}
