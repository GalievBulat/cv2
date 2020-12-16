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
            int type = Integer.parseInt(strings[1]);;
            for (int i = 2; i < strings.length; i++) {
                if (type!=-1 && strings[i].matches("\\{\\d+;\\d+\\}")){
                    Pair<Byte ,Byte> coords = helper.parseCoordinates(strings[i]);
                    executionPool.add(()-> {
                        gameService.add(user == player1, type, coords.getKey(), coords.getValue());
                        sendToChatters(user,
                                        "/d dp " + type +" {" +coords.getKey() +";"+ coords.getValue()  + "}");
                    });
                    //sendToChatters(user, "unit is deploying at " + coords.getKey() + "; "+ coords.getValue());
                    //gameService.add(user == player1,type,coords.getKey(),coords.getValue());
                    return;
                }
            }

        }else if (command.equals("/mv")) {
            Pair<Byte, Byte> coordsStart = null;
            Pair<Byte, Byte> coordsDest = null;
            for (int i = 1; i < strings.length; i++) {
                if (strings[i].matches("\\{\\d+;\\d+\\}")) {
                    if (coordsStart == null)
                        coordsStart = helper.parseCoordinates(strings[i]);
                    else {
                        coordsDest = helper.parseCoordinates(strings[i]);
                        break;
                    }
                }
            }
            if (coordsStart!=null && coordsDest!=null) {
                Pair<Byte, Byte> finalCoordsStart = coordsStart;
                Pair<Byte, Byte> finalCoordsDest = coordsDest;
                executionPool.add(()->{
                    gameService.move(user == player1, finalCoordsStart.getKey(), finalCoordsStart.getValue(),
                            finalCoordsDest.getKey(), finalCoordsDest.getValue());
                    sendToChatters(user,
                                "/d mv {" +finalCoordsStart.getKey() +
                                        ";"+ finalCoordsStart.getValue()  + "} {"
                                        + finalCoordsDest.getKey() +";"+ finalCoordsDest.getValue() + "}");
                });
            }
            //gameService.move(user == player1, coordsStart.getKey(), coordsStart.getValue(), coordsDest.getKey(),coordsDest.getValue());
        }else if (command.equals("/at")) {
            Pair<Byte, Byte> coordsAttacker = null;
            Pair<Byte, Byte> coordsAttacked = null;
            for (int i = 1; i < strings.length; i++) {
                if (strings[i].matches("\\{\\d+;\\d+\\}")) {
                    if (coordsAttacker == null)
                        coordsAttacker = helper.parseCoordinates(strings[i]);
                    else {
                        coordsAttacked = helper.parseCoordinates(strings[i]);
                        break;
                    }
                }
            }
            if (coordsAttacker!=null && coordsAttacked!=null) {
                Pair<Byte, Byte> finalCoordsAttacker = coordsAttacker;
                Pair<Byte, Byte> finalCoordsAttacked = coordsAttacked;
                executionPool.add(()->{
                    gameService.attack(user == player1, finalCoordsAttacker.getKey(), finalCoordsAttacker.getValue(),
                            finalCoordsAttacked.getKey(), finalCoordsAttacked.getValue());
                    sendToChatters(user,
                                "/d at {" +finalCoordsAttacker.getKey() +
                                        ";"+ finalCoordsAttacker.getValue()  + "} {"
                                        + finalCoordsAttacked.getKey() +";"+ finalCoordsAttacked.getValue() + "}");

                });
            }
            //gameService.attack(user == player1, coordsAttacker.getKey(), coordsAttacker.getValue(), coordsAttacked.getKey(),coordsAttacked.getValue());
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
