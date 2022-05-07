package entities;

public class Door {
    /**
     * Door - a connection between two rooms
     * used to allow movement between rooms
     * this allows me to have two rooms to the east of Room1
     * and allows for me to more easily have more complex interesting maps
     * since for example
     * I can have locked doors
     * or one way doors
     */
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
