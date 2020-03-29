public class Exit {

    private String id;
    private int direction;
    private String destinationRoomId;

    Exit(String i, int d, String dId) {
        id = i;
        direction = d;
        destinationRoomId = dId;
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

}
