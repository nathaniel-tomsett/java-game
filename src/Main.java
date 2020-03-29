import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

/*        Room diningRoom = new Room("1", "Dining Room");
        Room livingRoom = new Room("2", "Living Room");
        Room kitchen = new Room("3", "Kitchen");


        Item sword = new Item("1", "swordy-mc-swordface", Item.WEAPON);
        Item matilda = new Item("2", "tilda", Item.ANIMAL);
        Item key = new Item("3", "key", Item.KEY);
        Item glass = new Item("4", "glassface", Item.OTHER);

        List<Item> items = new ArrayList<>();
        items.add(sword);
        items.add(matilda);
        items.add(key);
        items.add(glass);

        diningRoom.setItems(items);

        Exit e1 = new Exit("1",Exit.EAST,"3");
        Exit e2 = new Exit("2",Exit.WEST,"1");
        Exit e3 = new Exit("3",Exit.SOUTH,"2");
        Exit e4 = new Exit("4",Exit.NORTH,"1");


        List <Exit>  diningRoomExits = new ArrayList<>();
        diningRoomExits.add(e1);
        diningRoomExits.add(e3);

        List <Exit>  livingRoomExits = new ArrayList<>();
        livingRoomExits.add(e2);

        List <Exit>  kitchenExits = new ArrayList<>();
        kitchenExits.add(e4);

       diningRoom.setExit(diningRoomExits);
       livingRoom.setExit(livingRoomExits);
       kitchen.setExit(kitchenExits);


       UserIO io = new UserIO();
       io.printToUser("nathaniel is nice");
       io.printToUser("no he is not");

       String str = io.readFromUser();
       System.out.println(str);
*/
/*
import java.io.BufferedReader;
import java.io.InputStreamReader;
BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
String s = br.readLine();
int i = Integer.parseInt(br.readLine());
 */



        World w = new World();
        w.doGameLoop();




    }
}
