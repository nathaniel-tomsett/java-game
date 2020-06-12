package users;

import entities.auditFile;
import users.UserConnections;
import util.TextColours;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UserStream {
    private boolean network;
    private PrintWriter out;
    private BufferedReader in;
    private String userId;

    public void initForConsole() {
        network = false;
        out = new PrintWriter(System.out);
        in =  new BufferedReader(new InputStreamReader(System.in));

    }

    public void initForNetwork(Socket clientSocket) throws IOException {
        network = true;
        out = new PrintWriter(clientSocket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    }

    public String readFromUser() {
        String ret = null;
        try {
            ret = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void printToUser(String message) {
        printToUser(message, TextColours.RESET);
    }


    public void printToUserSameLine(String message) {
        printToUserSameLine(message, TextColours.RESET);
    }

    public void printToUserSameLine(String message, String colour) {
        auditFile file = new auditFile();
        file.writeLogLine(message, userId);
        out.print(colour + message + TextColours.RESET);
        out.flush();
    }

    public void printToUser(String message, String colour) {
        auditFile file = new auditFile();
        file.writeLogLine(message, userId);
        out.print(colour + message + TextColours.RESET+ "\n\r");
        out.flush();
    }

    public void killStream() {
        try {
            in.close();
            out.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
        public void setUserId (String Id){
            userId = Id;
    }
}


