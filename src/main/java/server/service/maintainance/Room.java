package server.service.maintainance;

import javafx.util.Pair;
import protocol.RoomCommunication;
import protocol.CoordsParser;
import server.helper.Meta;
import server.model.User;
import server.service.GameService;
import server.interfaces.Instruction;

import java.net.Socket;
import java.util.*;

import static server.helper.Meta.CARDS_OVERALL_AMOUNT;

public class Room {
    private long lastTimeOfExecution = System.currentTimeMillis();
    private long lastTimeCardGiven = System.currentTimeMillis();
    private final int id;
    private final CoordsParser helper = new CoordsParser();
    private final RoomCommunication  roomCommunication;
    private final GameService gameService = new GameService();
    private final List<Instruction> executionPool = new LinkedList<>();
    public Room(int id) {
        this.id = id;
        roomCommunication = new RoomCommunication(id);
    }
    public void sendToChatters(User user,String message){
        roomCommunication.sendToChatters(user, message);
    }
    public void disconnect(User user){
        //TODO
        roomCommunication.disconnect(user);
        gameService.removePlayer(user);
        user.setCurrentChat(-1);
    }
    public void connect(User user, Socket socket){
        int userNum = gameService.addPlayer(user);
        roomCommunication.connect(user, socket, userNum);
        user.setCurrentChat(id);

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
                gameService.add(user == gameService.getPlayer1(), type, coords.getKey(), coords.getValue());
                sendToChatters(user,
                                "/dp " + type +" {" +coords.getKey() +";"+ coords.getValue()  + "}");
            });
        }else if (command.equals("/mv")) {
            Pair<Byte, Byte> coordsStart =  helper.parseCoordinates(strings[1]);
            Pair<Byte, Byte> coordsDest =  helper.parseCoordinates(strings[2]);
            executionPool.add(()->{
                gameService.move(user == gameService.getPlayer1() , coordsStart.getKey(), coordsStart.getValue(),
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
                if (!gameService.attack(user == gameService.getPlayer1(), coordsAttacker.getKey(), coordsAttacker.getValue(),
                        coordsAttacked.getKey(), coordsAttacked.getValue())) {
                    sendToChatters(user,
                            "/at {" + coordsAttacker.getKey() +
                                    ";" + coordsAttacker.getValue() + "} {"
                                    + coordsAttacked.getKey() + ";" + coordsAttacked.getValue() + "}");
                } else {
                    if (gameService.isGameOver()) {
                        sendToChatters(user,
                                "/go");
                    }else
                        sendToChatters(user,
                            "/rv {" + coordsAttacked.getKey() + ";" + coordsAttacked.getValue() + "}");
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
            lastTimeOfExecution = System.currentTimeMillis();
        }
    }
    public void giveCards(){
        if (gameService.getCards_given()<=CARDS_OVERALL_AMOUNT) {
            roomCommunication.sendToUser(gameService.getPlayer1(), "/cd " + gameService.getUnitTypeRepository().getRandom().getId());
            roomCommunication.sendToUser(gameService.getPlayer2(), "/cd " + gameService.getUnitTypeRepository().getRandom().getId());
            gameService.setCards_given(gameService.getCards_given()+1);
            lastTimeCardGiven = System.currentTimeMillis();
        }
    }
    public boolean isVacant(){
        return roomCommunication.getSize()<Meta.USERS_IN_ROOM_LIMIT;
    }
    public int getId() {
        return id;
    }
    public long getLastTimeOfExecution() {
        return lastTimeOfExecution;
    }
    public long getLastTimeCardGiven() {
        return lastTimeCardGiven;
    }
}
