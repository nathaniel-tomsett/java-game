package entities;

import engine.CommandTranslator;
import engine.World;
import entities.Player;
import users.UserStream;

import java.io.*;
import java.time.LocalDate;

public class auditFile {
    private World world;
    private UserStream userStream;
    private CommandTranslator commandTranslator;
    private String lastRoom;

    public void writeLogLine(String line, String userId) {
            try {
                File fileName = new File(LocalDate.now().toString());
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
