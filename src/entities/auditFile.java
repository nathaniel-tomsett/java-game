package entities;

import engine.CommandTranslator;
import engine.World;
import entities.Player;
import users.UserStream;

import java.io.*;

public class auditFile {
    private World world;
    private String userId;
    private UserStream userStream;
    private CommandTranslator commandTranslator;
    private String lastRoom;

    public void writeLogLine(String line) {
            try {
                File fileName = new File("fileWriter.txt");
                String toWrite =  userId + ": " + line;
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName , true));
                writer.write(toWrite);
                writer.write("\n");
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
