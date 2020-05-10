import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class UserIO {

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
        if (network) {
            out.println(message);
        } else {
            System.out.println(message);
        }
    }

    public String readFromUser() {
        try {
            if (network) {
                out.print("Input> ");
                out.flush();
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line = br.readLine();
                return line;
            } else {
                System.out.print("Input> ");
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
