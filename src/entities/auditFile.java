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

public class auditFile {
    private World world;
    private UserStream userStream;
    private CommandTranslator commandTranslator;
    private String lastRoom;

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
