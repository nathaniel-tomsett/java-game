package users;

import entities.auditFile;
import users.userConnections;
import util.TextColours;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * UserStream - a thread for a unique player that lets them interact with the game.
 */
public class UserStream {
    private PrintWriter out;
    private BufferedReader in;
    private String userId;
    private Socket socket;

    public void initForConsole() {
        out = new PrintWriter(System.out);
        in =  new BufferedReader(new InputStreamReader(System.in));
    }

    public void initForNetwork(Socket clientSocket) throws IOException {
        out = new PrintWriter(clientSocket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        socket = clientSocket;
    }

    /**
     * takes an input from the user
     * @return the input in the variable ret
     */
    public String readFromUser() {
        String ret = null;
        try {
            ret = in.readLine();
        } catch (IOException e) {
        }
        return ret;
    }

    /**
     * Prints a message to the user
     * @param message the thing that is printed
     */
    public void printToUser(String message) {
        printToUser(message, TextColours.PURPLE);
    }

    /**
     * Prints something to the user in colour
     * @param message the thing to print
     * @param colour the colour to use
     */
    public void printToUser(String message, String colour) {
        auditFile file = new auditFile();
        file.writeLogLine(message, userId);
        out.print(colour + message + TextColours.RESET+ "\n\r");
        out.flush();
    }

    /**
     * killStream - stops running a thread for a user, this removes that user from interacting with the game
     */
    public void killStream() {
        try {
            if (socket != null) {
                socket.close();
            } else {
                System.in.close();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void setUserId (String Id){
            userId = Id;
    }
}


