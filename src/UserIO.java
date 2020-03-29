import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.server.ExportException;

public class UserIO {

    public void printToUser(String message) {
        System.out.println(message);
    }

    public String readFromUser() {
        try {
            System.out.print("Input> ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String value = br.readLine();
            return value;
        } catch (Exception e) {
            return "";
        }
    }
}
