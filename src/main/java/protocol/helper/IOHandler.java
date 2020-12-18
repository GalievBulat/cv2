package protocol.helper;

import javafx.util.Pair;

import java.io.*;

public class IOHandler {
    public String readLine(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = reader.readLine();
            reader = null;
            return line;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void writeLine(OutputStream outputStream, String line) {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        try {
            writer.write(line + "\n");
            writer.flush();
            writer = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String readLine(BufferedReader reader) {
        try {
            String line = reader.readLine();
            return line;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void writeLine(BufferedWriter writer, String line) {
        try {
            writer.write(line + "\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
