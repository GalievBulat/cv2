package server.service.maintainance;

import client.Client;
import javafx.util.Pair;
import server.helper.IOHandler;
import server.helper.Meta;
import server.model.User;
import server.service.GameService;
import server.interfaces.Instruction;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;

import static server.helper.Meta.SEND_SELF;

public class Room {
    private int id;
    private User player1;
    private User player2;
    private final IOHandler helper = new IOHandler();
    private final GameService gameService = new GameService();
    private final Map<User, Socket> sockets = new HashMap<>(Meta.USERS_IN_ROOM_LIMIT);
    private final List<String> archive = new ArrayList<>();
    private final List<Instruction> executionPool = new LinkedList<>();
    public Room() {
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
    public void disconnect(User user){
        //TODO
        sendToChatters(user, "disconnected");
        sockets.remove(user);
        user.setCurrentChat(-1);
    }
    public void connect(User user, Socket socket){
        sockets.put(user,socket);
        try {
            //TODO
            System.out.println(user.getName() + " connected to " + id);
            if (!SEND_SELF) {
                helper.writeLine(socket.getOutputStream(), String.join(Meta.DELIMITER, archive));
            }
            user.setCurrentChat(id);
            if (player1 == null){
                player1 = user;
                sendToUser(user, "/c 1");
            }else if(player2 == null){
                player2 = user;
                sendToUser(user, "/c 2");
            }
            sendToChatters(user, "connected");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendToUser(User user,String message){
        try {
            helper.writeLine(sockets.get(user).getOutputStream(), user.getName() + ": " +message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void handleCommand(String message, User user){
        //TODO
        System.out.println(message);
        String[] strings = message.split(" ");
        String command = strings[0];
        if (command.equals("/l"))
            disconnect(user);
        else if (command.equals("/dp")) {
            int type = Integer.parseInt(strings[1]);
            Pair<Byte ,Byte> coords = helper.parseCoordinates(strings[2]);
            executionPool.add(()-> {
                gameService.add(user == player1, type, coords.getKey(), coords.getValue());
                sendToChatters(user,
                                "/dp " + type +" {" +coords.getKey() +";"+ coords.getValue()  + "}");
            });
        }else if (command.equals("/mv")) {
            Pair<Byte, Byte> coordsStart =  helper.parseCoordinates(strings[1]);
            Pair<Byte, Byte> coordsDest =  helper.parseCoordinates(strings[2]);
            executionPool.add(()->{
                gameService.move(user == player1, coordsStart.getKey(), coordsStart.getValue(),
                        coordsDest.getKey(), coordsDest.getValue());
                sendToChatters(user,
                            "/mv {" +coordsStart.getKey() +
                                    ";"+ coordsStart.getValue()  + "} {"
                                    + coordsDest.getKey() +";"+ coordsDest.getValue() + "}");
            });
            //gameService.move(user == player1, coordsStart.getKey(), coordsStart.getValue(), coordsDest.getKey(),coordsDest.getValue());
        }else if (command.equals("/at")) {
            Pair<Byte, Byte> coordsAttacker = helper.parseCoordinates(strings[1]);
            Pair<Byte, Byte> coordsAttacked = helper.parseCoordinates(strings[2]);
            executionPool.add(()->{
                if (!gameService.attack(user == player1, coordsAttacker.getKey(), coordsAttacker.getValue(),
                        coordsAttacked.getKey(), coordsAttacked.getValue())) {
                    sendToChatters(user,
                            "/at {" + coordsAttacker.getKey() +
                                    ";" + coordsAttacker.getValue() + "} {"
                                    + coordsAttacked.getKey() + ";" + coordsAttacked.getValue() + "}");
                } else {
                    sendToChatters(user,
                            "/rv {"
                                    + coordsAttacked.getKey() + ";" + coordsAttacked.getValue() + "}");
                }

            });
        }
    }
    public void executePool(){
        synchronized (executionPool) {
            if (!executionPool.isEmpty())
                for (Instruction instruction : executionPool) {
                    instruction.execute();
                }
            executionPool.clear();
        }
    }
    public boolean isVacant(){
        return sockets.size()<Meta.USERS_IN_ROOM_LIMIT;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
