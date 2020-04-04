public class Door {

    private String id;
    private int direction;
    private String destinationRoomId;
    private boolean locked;

    Door(String i, int d, String dId, boolean l) {
        id = i;
        direction = d;
        destinationRoomId = dId;
        locked = l;
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

}
