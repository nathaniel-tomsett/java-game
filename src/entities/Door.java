package entities;

public class Door {

    private String id;
    private int direction;
    private String destinationRoomId;
    private boolean locked;
    private String key;

    Door(String i, int d, String dId, boolean l, String k) {
        id = i;
        direction = d;
        destinationRoomId = dId;
        locked = l;
        key = k;
    }

    public String getId() {
        return id;
    }

    public int getDirection() {
        return direction;
    }

    public String getDestinationRoomId() {
        return destinationRoomId;
    }

    public boolean getlocked() {return locked; }

    public boolean tryUnlock(String itemName){
        if (itemName.equalsIgnoreCase(key)){
            locked = false;
            return true;
        }
        else{
            return false;
        }
    }


}
