package protocol;

import javafx.util.Pair;

public class CoordsParser {
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
