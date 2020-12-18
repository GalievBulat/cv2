package protocol;

import javafx.application.Platform;
import javafx.util.Pair;
import protocol.data.Data;
import protocol.helper.CoordsParser;
import protocol.helper.IOHandler;
import server.helper.Meta;
import server.model.User;
import view.model.Card;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

import static server.helper.Meta.SEND_SELF;

public class RoomCommunication {
    private final int id;
    private final IOHandler helper = new IOHandler();
    private final CoordsParser coordsParser = new CoordsParser();
    private final List<String> archive = new ArrayList<>();
    private final Map<User, Socket> sockets = new HashMap<>(Meta.USERS_IN_ROOM_LIMIT);
    public RoomCommunication(int id) {
        this.id = id;
    }
    public void connect(User user, Socket socket,int userNum){
        sockets.put(user,socket);
        try {
            //TODO
            System.out.println(user.getName() + " connected to " + id);
            helper.writeLine(socket.getOutputStream(), String.join(Meta.DELIMITER, archive));
            sendToUser(user, "/c " + userNum);
            sendToChatters(user, "connected");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendToChatters(User user,String message){
        String line= user.getName() + ": " + message;
        try {
            for (User someUser : sockets.keySet()) {
                if (!sockets.get(someUser).isClosed() ) {
                    if(SEND_SELF || !someUser.equals(user)){
                        helper.writeLine(sockets.get(someUser).getOutputStream(), line);
                    }
                } else
                    sockets.remove(someUser);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        archive.add(line);
    }
    public void sendToUser(User user,String message){
        try {
            helper.writeLine(sockets.get(user).getOutputStream(), user.getName() + ": " +message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void disconnect(User user){
        //TODO
        sendToChatters(user, "disconnected");
        sockets.remove(user);
    }
    public int getSize(){
        return sockets.size();
    }
    public Data parseCommand(String message){
        if (message.charAt(0) == '/') {
            String[] strings = message.split(" ");
            String command = strings[0];
            if (command.equals("/l"))
                return Data.DISCONNECT;
            else if (command.equals("/dp"))
                return Data.DEPLOY;
            else if (command.equals("/mv"))
                return Data.MOVE;
            else if (command.equals("/at"))
                return Data.MOVE;
        }
        return Data.ERROR;
    }
    @SafeVarargs
    public final void sendCommandWithCoords(Data type, User user, Pair<Byte, Byte>... coords){
        sendToChatters(user,
                type.getCommand() + " " +
                        Arrays.stream(coords)
                                .map(pair->"{" + pair.getKey() + ";" + pair.getValue() + "}")
                                .collect(Collectors.joining(" ")));
    }
    public final void sendCommandWithNum(Data type, User user, int num){
        sendToChatters(user,
                type.getCommand() + " " +
                        num);
    }
    @SafeVarargs
    public final void sendCommandWithCoordsAndNum(Data type, User user, int num,Pair<Byte, Byte>... coords){
        sendToChatters(user,
                type.getCommand() + " " +
                        Arrays.stream(coords)
                                .map(pair->"{" + pair.getKey() + ";" + pair.getValue() + "}")
                                .collect(Collectors.joining(" ")) + " " + num);
    }
    public Pair<Byte,Byte> getCoords(String command, int num){
        return coordsParser.parseCoordinates(command.split(" ")[num]);
    }
    public  int getNumFromCommand(String command, int n){
        return Integer.parseInt(command.split(" ")[n]);
    }
}
