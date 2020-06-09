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
    private boolean consoleOff;

    public UserConnections(World world, boolean consoleOff) {
        this.world = world;
        this.consoleOff = consoleOff;
    }

    public void startListeningForUsers() {
        new Thread() {
            public void run() {
                try {
                    serverSocket = new ServerSocket(serverPort);
                    while (true) {
                        try {
                            UserStream stream = new UserStream();
                            stream.initForNetwork(serverSocket.accept());
                            newUserConnectionRequest(stream);
                        } catch (IOException e) {
                        }
                    }
                } catch (IOException e) {
                }
            }
        }.start();
        if (!consoleOff) {
            new Thread() {
                public void run() {
                    try {
                        UserStream stream = new UserStream();
                        stream.initForConsole();
                        newUserConnectionRequest(stream);
                    } catch (Exception e) {
                    }
                }
            }.start();
        }
    }

    private void newUserConnectionRequest(UserStream stream) {
        List<NPC> NPCList = world.getNPCList();
        List<Player> PlayerList = new ArrayList<>();

        boolean nameOk = false;
        String username = "";
        String passGuess = "";
        String password = "fred";
        int guesses = 0;
        boolean passwordOk = false;

        while (!nameOk){
            boolean foundNPC = false;

            while (!passwordOk) {
                stream.printToUser("what is the password");
                passGuess = stream.readFromUser();
                if (passGuess.equals(password)) {
                    stream.printToUser("thats correct");
                    passwordOk = true;
                } else {
                    stream.printToUser("thats incorrect");
                    guesses += 1;
                    if (guesses == 3){
                        stream.printToUser("youve guessed incorrectly too many times");
                        stream.killStream();
                        break;
                    }
                }
            }

            if (passwordOk) {
                stream.printToUser("What is your name?");
                username = stream.readFromUser();
                //here to see username

                String empty = "";
                if (username == null) {
                    break;
                }
                if (username.equals(empty)) {
                    stream.printToUser("that is an invalid username");
                    continue;
                }

                for (NPC i : NPCList) {
                    if (i.getName().equalsIgnoreCase(username)) {
                        stream.printToUser("This username is already taken");
                        foundNPC = true;
                    }
                }
                if (foundNPC) {
                    continue;
                }
                Player playerExists = world.getPlayer(username);

                if (playerExists != null) {
                    stream.printToUser("username is taken");
                    continue;
                }

                nameOk = true;
            } else {
                break;
            }
        }
        if (!username.isEmpty()) {
            String playerName = username;
            world.addNewPlayer(playerName, new Player(playerName, stream));
        }
    }
}
