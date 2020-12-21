package server.service.maintainance;

import javafx.util.Pair;
import protocol.ServerCommunication;
import protocol.data.CommandData;
import server.dao.RoomsRepository;
import server.helper.Meta;
import server.model.User;

import java.io.*;
import java.util.stream.Collectors;

import static server.helper.Meta.CARDS_GIVING_PERIOD;
import static server.helper.Meta.COMMANDS_EXECUTION_PERIOD;

public class Server implements AutoCloseable {
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
            serverCommunication.addClient(
                    roomsRepository.getVacantRooms().stream().map(Room::getId).collect(Collectors.toList()));
        }
    }
    public void listenToIncomingMessages(){
        try {
            while (!serverCommunication.isClosed()) {
                for (Pair<String, User> update : serverCommunication.getUpdates()) {
                    handleMessage(update.getKey(), update.getValue());
                }
                long currentTime = System.currentTimeMillis();
                for (Room room : roomsRepository.getRooms()) {
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
        } catch (RuntimeException e){
            serverCommunication.alertAll(e.toString());
        }
    }
    public void handleMessage(String message, User user){
        try {
            System.out.println(message);
            CommandData command = CommandData.determineCommand(message);
            if (command == CommandData.DISCONNECT) {
                serverCommunication.closeClient(user);
                if (user.getCurrentChat()!=-1){
                    roomsRepository.getRoom(user.getCurrentChat()).disconnect(user);
                }
            }else if (command == CommandData.SHUTDOWN)
                close();
            else if (command== CommandData.ENTER)
                roomsRepository.getRoom(serverCommunication.getRoomFromCommand(message))
                        .connect(user,serverCommunication.getSocket(user));
            else
                roomsRepository.getRoom(user.getCurrentChat()).handleCommand(message,user);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isClosed(){
        return serverCommunication.isClosed();
    }

}
