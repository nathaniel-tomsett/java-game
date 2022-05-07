package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventory - an class for creating inventory objects that store what Items a user is carrying
 */
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
            return false;
        }
    }

    public void removeItem(Item i) {
        if (items.size() == 0) {
        }
        items.remove(i);
    }

    /**
     * checks if an Item exists in the players inventory
     * @param itemName the item being checked for
     * @return true or false
     */
    public boolean doesExistByName(String itemName){
            for (Item i : items ){
                if (i.getName().equalsIgnoreCase(itemName)){
                    return true;
                }
            }
            return false;

    }

    /**
     * this gets the Item so its relevant info can be used for its use
     * if the previous function returned false or this one returns null the item in use will be null
     * however an attack can still be done with a null attack object since this will just ignore the item slot and you will attack with your fists
     * @param itemName the item being fetched
     * @return the item or null
     */
    public Item getItem(String itemName){
        for (Item i : items ){
            if (i.getName().equalsIgnoreCase(itemName)){
                return i;
            }
        }
        return null;

    }

}
