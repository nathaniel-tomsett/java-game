package users;

import engine.CommandHandler;
import engine.World;
import entities.NPC;
import entities.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserConnections {

    private World world;

    private int serverPort = 1001;
    private ServerSocket serverSocket;

    Map<String, CommandHandler> inputProcessors = new HashMap<>();

    public UserConnections(World world) {
        this.world = world;
    }

    public void startListeningForUsers() {
        new Thread() {
            public void run() {
                try {
                    serverSocket = new ServerSocket(serverPort);
                    while (true) {
                        UserStream stream = new UserStream();
                        stream.initForNetwork(serverSocket.accept());
                        newUserConnectionRequest(stream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            public void run() {
                UserStream stream = new UserStream();
                stream.initForConsole();
                newUserConnectionRequest(stream);
            }
        }.start();
    }

    private void newUserConnectionRequest(UserStream stream) {
        List<NPC> NPCList = world.getNPCList();
        List<Player> PlayerList = new ArrayList<>();

        boolean nameOk = false;
        String username = "";
        while (!nameOk){
            boolean foundNPC = false;

            stream.printToUser("What is your name?");
            username = stream.readFromUser();

            String empty = "";
            if (username.equals(empty)) {
                stream.printToUser("that is an invalid username");
                continue;
            }

            for (NPC i : NPCList){
                if (i.getName().equalsIgnoreCase(username)){
                    stream.printToUser("This username is already taken");
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
    }
}
