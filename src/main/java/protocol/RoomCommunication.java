package protocol;

import server.helper.Meta;
import server.model.User;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static server.helper.Meta.SEND_SELF;

public class RoomCommunication {
    private final int id;
    private final IOHandler helper = new IOHandler();
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
}
