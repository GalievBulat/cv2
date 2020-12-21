package server.service.maintainance;

import javafx.util.Pair;
import protocol.RoomCommunication;
import protocol.data.Data;
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
        Data command= roomCommunication.parseCommand(message);
        if (command == Data.DISCONNECT)
            disconnect(user);
        else if (command == Data.DEPLOY) {
            int type = roomCommunication.getNumFromCommand(message,2);
            Pair<Byte ,Byte> coords = roomCommunication.getCoords(message,1);
            executionPool.add(()-> {
                gameService.add(user == gameService.getPlayer1(), type, coords.getKey(), coords.getValue());
                roomCommunication.sendCommandWithCoordsAndNum(command,user,type,coords);
            });
        }else if (command == Data.MOVE) {
            Pair<Byte, Byte> coordsStart = roomCommunication.getCoords(message,1);
            Pair<Byte, Byte> coordsDest =  roomCommunication.getCoords(message,2);
            executionPool.add(()->{
                gameService.move(user == gameService.getPlayer1() , coordsStart.getKey(), coordsStart.getValue(),
                        coordsDest.getKey(), coordsDest.getValue());
                roomCommunication.sendCommandWithCoords(command,user,coordsStart,coordsDest);
            });
        }else if (command == Data.ATTACK) {
            Pair<Byte, Byte> coordsAttacker = roomCommunication.getCoords(message,1);
            Pair<Byte, Byte> coordsAttacked = roomCommunication.getCoords(message,2);
            executionPool.add(()->{
                if (!gameService.attack(user == gameService.getPlayer1(), coordsAttacker.getKey(), coordsAttacker.getValue(),
                        coordsAttacked.getKey(), coordsAttacked.getValue())) {
                    roomCommunication.sendCommandWithCoords(command,user,coordsAttacker,coordsAttacked);
                } else {
                    if (gameService.isGameOver()) {
                        roomCommunication.sendCommandWithCoordsToUser(Data.GAME_OVER,user);
                    }else
                        roomCommunication.sendCommandWithCoords(Data.REMOVE,user,coordsAttacker,coordsAttacked);
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
            roomCommunication.sendCommandWithNumToUser(Data.CARD_GIVING,gameService.getPlayer1(),gameService.getUnitTypeRepository().getRandom().getId());
            roomCommunication.sendCommandWithNumToUser(Data.CARD_GIVING,gameService.getPlayer2(),gameService.getUnitTypeRepository().getRandom().getId());
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
