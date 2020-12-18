package server.service.maintainance;

import javafx.util.Pair;
import protocol.ServerCommunication;
import protocol.data.Data;
import server.dao.RoomsRepository;
import server.helper.Meta;
import server.model.User;

import java.io.*;
import java.util.stream.Collectors;

import static server.helper.Meta.CARDS_GIVING_PERIOD;
import static server.helper.Meta.COMMANDS_EXECUTION_PERIOD;

public class Server implements AutoCloseable {
    private long time = System.currentTimeMillis();
    private final ServerCommunication serverCommunication = new ServerCommunication(Meta.PORT);
    private final RoomsRepository roomsRepository = new RoomsRepository();
    // /init Sasha
    // /enter 0
    public Server() {
    }
    @Override
    public void close() throws IOException{
        serverCommunication.close();
    }
    public void handleConnections() {
        while (!serverCommunication.isClosed()) {
            serverCommunication.addClient(roomsRepository.getVacantRooms().stream().map(Room::getId).collect(Collectors.toList()));
        }
    }
    public void listenToIncomingMessages(){
        while (!serverCommunication.isClosed()) {
            for (Pair<String,User> update : serverCommunication.getUpdates()){
                handleMessage(update.getKey(),update.getValue());
            }
            long currentTime = System.currentTimeMillis();
            for (Room room: roomsRepository.getRooms()){
                if (!room.isVacant()) {
                    if (currentTime - room.getLastTimeOfExecution() >= COMMANDS_EXECUTION_PERIOD) {
                        room.executePool();
                    }
                    if (currentTime - room.getLastTimeCardGiven() >= CARDS_GIVING_PERIOD) {
                        room.giveCards();
                    }
                }
            }
        }
    }
    public void handleMessage(String message, User user){
        try {
            Data command = serverCommunication.parseCommand(message);
            if (command == Data.STOP) {
                serverCommunication.closeClient(user);
                if (user.getCurrentChat()!=-1){
                    roomsRepository.getRoom(user.getCurrentChat()).disconnect(user);
                }
            }else if (command == Data.DISCONNECT)
                close();
            else if (command==Data.ENTER)
                roomsRepository.getRoom(serverCommunication.getRoomFromCommand(message)).connect(user,serverCommunication.getSocket(user));
            else if (command==Data.OTHER)
                roomsRepository.getRoom(user.getCurrentChat()).handleCommand(message,user);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isClosed(){
        return serverCommunication.isClosed();
    }

}
