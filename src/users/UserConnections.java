package users;

import engine.CommandHandler;
import engine.World;
import entities.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class UserConnections {

    private World world;

    private int serverPort = 1001;
    private ServerSocket serverSocket;

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
                        newUserConnecterd(stream);
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
                newUserConnecterd(stream);
            }
        }.start();
    }

    private void newUserConnecterd(UserStream stream) {
        stream.printToUser("What is your name?");
        String username = stream.readFromUser();
        String playerName = username;
        world.addPlayer(playerName, new Player(playerName));
        CommandHandler processor = new CommandHandler(world, playerName, stream);
    }
}
