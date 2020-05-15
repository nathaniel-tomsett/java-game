import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;




public class UserIO {

    public static final String RESET
            = "\u001B[0m";
    public static final String BLACK =
            "\u001B[30m";
    public static final String RED =
            "\u001B[31m";
    public static final String GREEN =
            "\u001B[32m";
    public static final String YELLOW =
            "\u001B[33m";
    public static final  String BLUE =
            "\u001B[34m";
    public static final String PURPLE =
            "\u001B[35m";
    public static final String CYAN =
            "\u001B[36m";
    public static final String WHITE =
            "\u001B[37m";

    private boolean network = false;

    private int serverPort = 1001;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    UserIO(boolean network) {
        this.network = network;
        if (this.network) {
            try {
                serverSocket = new ServerSocket(serverPort);
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void printToUser(String message) {
        printToUser(message, RESET);
    }

    public void printToUser(String message, String colour) {
        if (network) {
            out.println(message);
        } else {
            System.out.println(colour + message);
        }
    }

    public void printToUserSameLine(String message) {
        if (network) {
            out.print(message);
            out.flush();
        } else {
            System.out.print(message);
        }
    }

    public String readFromUser() {
        try {
            if (network) {
                printToUserSameLine("Input> ");
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line = br.readLine();
                return line;
            } else {
                printToUserSameLine("Input> ");
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String value = br.readLine();
                return value;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
