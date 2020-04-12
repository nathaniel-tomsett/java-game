import java.util.Random;

public class NPC {
    private String id;
    private String name;
    private int moveChance;



    public String getId(){return id;}
    public String getName() {return name;}
    public int getMoveChance() {return moveChance;}

     public boolean shouldMove (){
        Random rand = new Random();
        int randInt = rand.nextInt(99);
        if (randInt <= moveChance){
            return true;

        }
        else {
            return false;
        }





     }


    NPC(String i, String n, int m) {
        id = i;
        name = n;
        moveChance = m ;


    }


}
