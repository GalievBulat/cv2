package serverclient.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class IOHandler {
    public String readLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeLine(BufferedWriter writer,String line) {
        try {
            writer.write(line + "\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
