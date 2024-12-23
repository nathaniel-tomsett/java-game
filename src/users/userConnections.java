package users;

import DB.DBAccess;
import engine.World;
import entities.NPC;
import entities.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * userConnections - this class is to deal with connections being made
 * that can then be handed over to userStream once those users are on their own thread
 */
public class userConnections {

    private World world;

    private int serverPort = 1001;
    private ServerSocket serverSocket;
    private boolean consoleOff;

    private DB.DBAccess dbAccess;

    public userConnections(World world, boolean consoleOff) throws SQLException {
        this.world = world;
        this.consoleOff = consoleOff;
        this.dbAccess = new DBAccess();
    }

    /**
     *this function creates a server socket  that is then set to listen for a connection request
     * on the set port of whatever Machine its being run off of
     */
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
                        } catch (Exception e) {
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
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    /**
     * when a request to connect does occur this code is then ran
     * this code deals with getting the player Logged in/ registered
     * once the user has validated themselves they are put onto stream then
     * @param stream this is the UserStream given to the player once they connect their unique thread that handles all their interactions with the world
     */
    private void newUserConnectionRequest(UserStream stream) throws SQLException {
        List<NPC> NPCList = world.getNPCList();
        List<Player> PlayerList = new ArrayList<>();

        boolean nameOk = false;
        String username = "";
        boolean godMode = false;
        while (!nameOk){
            boolean foundNPC = false;

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
            if (username.equals("fred")){
                 godMode = true;
            }
            if (dbAccess.isUserExisting(username)){
                stream.printToUser("What is your password?");
                String password = stream.readFromUser();
               if(dbAccess.validatePassword(username, password)) {
                   stream.printToUser("password correct!");
               }
               else{
                   stream.printToUser("that's incorrect");
                   continue;
               }
            }
            else {
                stream.printToUser("What is your password?");
                String password = stream.readFromUser();
                dbAccess.addUser(username, password);
            }
            nameOk = true;
            stream.setUserId(username);
            try {
                stream.printToUser("Registering player...");
                Thread.sleep(1000);
                stream.printToUser("Building the world...");
                Thread.sleep(1000);
                stream.printToUser("Entering the game...");
                Thread.sleep(1000);
                stream.printToUser("");
            } catch (InterruptedException e) {
            }
        }
        if (username != null && !username.isEmpty()) {
            String playerName = username;
            Player newPlayer = new Player(playerName, stream);
            newPlayer.setgodmode(godMode);
            world.addNewPlayer(playerName, newPlayer);
        }
    }
}
