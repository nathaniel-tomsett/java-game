package users;

import engine.CommandHandler;
import engine.World;
import entities.NPC;
import entities.Player;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserConnections extends Thread {

    private boolean network = false;
    private World world;

    private int serverPort = 1001;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    Map<String, CommandHandler> inputProcessors = new HashMap<>();

    public UserConnections(boolean network) {
        this.network = network;
        this.world = new World();
        start();
    }


    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(serverPort);
            do {
                UserStream stream = new UserStream();
                if (network) {
                    stream.initForNetwork(serverSocket.accept());
                } else {
                    stream.initForConsole();
                }



                //TODO: hack-tastic
                List<NPC> NPCList = world.getNPCList();
                List<Player> PlayerList = new ArrayList<>();

                boolean nameOk = false;
                String username = "";
                while (!nameOk){
                    boolean foundNPC = false;


                    stream.printToUser("what is your name?");
                    username = stream.readFromUser();
                   String empty = "";
                    if (username.equals(empty)){
                        stream.printToUser("that is an invalid username");
                        continue;
                    }
                    for (NPC i : NPCList){
                        if (i.getName().equalsIgnoreCase(username)){
                            stream.printToUser("this username is already taken");
                            foundNPC = true;
                        }


                    }
                    if (foundNPC){
                        continue;

                    }
                    Player playerExists = world.getPlayer(username);

                    if (playerExists != null) {
                        stream.printToUser("username is taken");
                        continue;
                    }








                    nameOk = true;

                }
                String playerName = username;
                world.addPlayer(playerName, new Player(playerName));
                CommandHandler processor = new CommandHandler(world, playerName, stream);
                inputProcessors.put(playerName, processor);
            } while (network);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
