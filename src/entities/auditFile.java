package entities;

import engine.CommandTranslator;
import engine.World;
import entities.Player;
import users.UserStream;

import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * audit file - a class for writing actions into audit log for testing and moderation purposes (this has been super helpful!)
 */
public class auditFile {
    private World world;
    private UserStream userStream;
    private CommandTranslator commandTranslator;
    private String lastRoom;

    /**
     * it just writes to .txt file,named after the date and Time, in a formatted way
     * @param line what is wanted to be written
     * @param userId which person did the action
     */
    public void writeLogLine(String line, String userId) {
            try {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:ms");
                String formatted = now.format(formatter);

                File fileName = new File(LocalDate.now().toString());
                String toWrite =  formatted + " -- " + userId + " -- " + line;
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName , true));
                writer.write(toWrite);
                writer.write("\n");
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
