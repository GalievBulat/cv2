package server.service.maintainance;

import server.dao.RoomsRepository;
import server.helper.IOHandler;
import server.helper.Meta;
import server.model.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static server.helper.Meta.COMMANDS_EXECUTION_PERIOD;

public class Server implements AutoCloseable {
    private long time = System.currentTimeMillis();
    private final Map<User, Socket> sockets = new ConcurrentHashMap<>();
    private final ServerSocket socket;
    private final IOHandler helper = new IOHandler();
    private final RoomsRepository roomsRepository = new RoomsRepository();
    // /init Sasha
    // /enter 0
    public Server() {
        try {
            socket = new ServerSocket(Meta.PORT);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    @Override
    public void close() throws IOException{
        for (User user: sockets.keySet()){
            closeClient(user);
        }
        socket.close();
    }
    public void handleConnections() {
        while (!socket.isClosed()) {
            try {
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
                            "/d " + roomsRepository.findVacantRooms().stream().map(room -> room.getId() + "")
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
            }
        }
    }
    public void listenToIncomingMessages(){
        while (!socket.isClosed()) {
            try {
                if (sockets.size()!=0)
                for (User user : sockets.keySet()) {
                    if (!sockets.get(user).isClosed() && sockets.get(user).getInputStream().available() != 0) {
                        handleMessage(
                            helper.readLine(sockets.get(user).getInputStream()), user);
                    }
                }
                long currentTime = System.currentTimeMillis();
                if(currentTime - time>=COMMANDS_EXECUTION_PERIOD){
                    for (Room room: roomsRepository.getRooms()){
                        room.executePool();
                    }
                    time = currentTime;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (RuntimeException e){
                e.printStackTrace();
                try {
                    alertAll(e.toString());
                    close();
                } catch (IOException ignore) { }
            }
        }
    }
    public void handleMessage(String message, User user){
        try {
            if (message.charAt(0) == '/') {
                String[] strings = message.split(" ");
                String command = strings[0];
                    if (command.equals("/s"))
                        closeClient(user);
                    else if (command.equals("/sd"))
                        close();
                    else if (command.equals("/e"))
                        roomsRepository.getRoom(Integer.parseInt(strings[1])).connect(user,sockets.get(user));
                    else
                        roomsRepository.getRoom(user.getCurrentChat()).handleCommand(message,user);
                    /* else if (user.getCurrentChat()!=-1)
                roomsRepository.getChat(user.getCurrentChat()).sendToChatters(user , message);*/
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e){
            alertAll(e.toString());
        }
    }
    public void alertAll(String message){
        try {
            for(Socket socket : sockets.values()){
                if (!socket.isClosed())
                    helper.writeLine(socket.getOutputStream(),"server: "+message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isClosed(){
        return socket.isClosed();
    }
    public void closeClient(User user){
        try {
            sockets.get(user).close();
            sockets.remove(user);
        } catch (IOException ignore) { }
    }
}
