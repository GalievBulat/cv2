package server.service.maintainance;

import javafx.util.Pair;
import protocol.ServerCommunication;
import server.dao.RoomsRepository;
import server.helper.Meta;
import server.model.User;

import java.io.*;
import java.util.stream.Collectors;

import static server.helper.Meta.CARDS_GIVING_PERIOD;
import static server.helper.Meta.COMMANDS_EXECUTION_PERIOD;

public class Server implements AutoCloseable {
    private long time = System.currentTimeMillis();
    /* private final Map<User, Socket> sockets = new ConcurrentHashMap<>();
    private final ServerSocket socket;*/
    private final ServerCommunication serverCommunication = new ServerCommunication(Meta.PORT);
    //private final IOHandler helper = new IOHandler();
    private final RoomsRepository roomsRepository = new RoomsRepository();
    // /init Sasha
    // /enter 0
    public Server() {
        /*try {
            socket = new ServerSocket(Meta.PORT);
        }catch (IOException e){
            throw new RuntimeException(e);
        }*/
    }
    @Override
    public void close() throws IOException{
        /*for (User user: sockets.keySet()){
            closeClient(user);
        }
        socket.close();*/
        serverCommunication.close();
    }
    public void handleConnections() {
        while (!serverCommunication.isClosed()) {
            /*try {
                Socket client = socket.accept();
                System.out.println("connected");
                String query = helper.readLine(client.getInputStream());
                if (query.startsWith("/i ")) {
                    String[] args = query.split(" ");
                    String name = args[1];
                    User user = new User();
                    user.setName(name);
                    sockets.put(user, client);
                    helper.writeLine(client.getOutputStream(),
                            "/d " + roomsRepository.getVacantRooms().stream().map(room -> room.getId() + "")
                                    .collect(Collectors.joining()));
                    System.out.println(name + " initialized");
                } else {
                    client.close();
                    System.out.println("wrong greeting");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (RuntimeException e){
                e.printStackTrace();
                try {
                    alertAll(e.getMessage());
                    close();
                } catch (IOException ignore) { }
            }*/
            serverCommunication.addClient(roomsRepository.getVacantRooms().stream().map(Room::getId).collect(Collectors.toList()));
        }
    }
    public void listenToIncomingMessages(){
        while (!serverCommunication.isClosed()) {
            /*if (sockets.size()!=0)
            for (User user : sockets.keySet()) {
                if (!sockets.get(user).isClosed() && sockets.get(user).getInputStream().available() != 0) {
                    handleMessage(
                        helper.readLine(sockets.get(user).getInputStream()), user);
                }
            }*/
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
            if (message.charAt(0) == '/') {
                String[] strings = message.split(" ");
                String command = strings[0];
                    if (command.equals("/s")) {
                        serverCommunication.closeClient(user);
                        if (user.getCurrentChat()!=-1){
                            roomsRepository.getRoom(user.getCurrentChat()).disconnect(user);
                        }
                    }else if (command.equals("/sd"))
                        close();
                    else if (command.equals("/e"))
                        roomsRepository.getRoom(Integer.parseInt(strings[1])).connect(user,serverCommunication.getSocket(user));
                    else
                        roomsRepository.getRoom(user.getCurrentChat()).handleCommand(message,user);
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }/* catch (RuntimeException e){
            alertAll(e.toString());
        }*/
    }

    public boolean isClosed(){
        return serverCommunication.isClosed();
    }

}
