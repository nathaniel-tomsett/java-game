package users;

import engine.CommandHandler;
import engine.World;
import entities.Player;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
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
    }

    private int playerCounter = 0;

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
                String playerName = "nat" + playerCounter++;
                world.addPlayer(playerName, new Player(playerName));
                CommandHandler processor = new CommandHandler(world, playerName, stream);
                inputProcessors.put(playerName, processor);
            } while (network);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
