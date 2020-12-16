package server.helper;

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
    public Pair<Byte,Byte> parseCoordinates(String coordinates){
        if (coordinates.matches("\\{\\d+;\\d+\\}")) {
            StringBuilder s1 = new StringBuilder();
            StringBuilder s2 = new StringBuilder();
            int i = 1;
            while (coordinates.charAt(i) != ';') {
                s1.append(coordinates.charAt(i));
                i++;
            }
            i++;
            while (coordinates.charAt(i) != '}') {
                s2.append(coordinates.charAt(i));
                i++;
            }
            return new Pair<>(Byte.parseByte(s1.toString()), Byte.parseByte(s2.toString()));
        } else throw new IllegalArgumentException("wrong coords");
    }
}
