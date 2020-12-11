package serverclient.server;

import serverclient.dao.RoomsRepository;
import serverclient.helper.IOHandler;
import serverclient.helper.Meta;
import serverclient.model.User;
import serverclient.service.GameService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements AutoCloseable {
    final Map<User, Socket> sockets = new ConcurrentHashMap<>();
    private final ServerSocket socket;
    private final IOHandler helper = new IOHandler();
    private final RoomsRepository roomsRepository = new RoomsRepository();
    private final GameService gameService = new GameService();
    // /init Sasha 0 2 3
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
                BufferedReader reader =new BufferedReader( new InputStreamReader(client.getInputStream()));
                String query = helper.readLine(reader);
                if (query.startsWith("/init")) {
                    String[] args = query.split(" ");
                    String name = args[1];
                    ArrayList<Integer> channels = new ArrayList<>();
                    for (int i = 2; i < args.length; i++) {
                        channels.add(Integer.parseInt(args[i]));
                    }
                    User user = new User(name, channels.stream().mapToInt(i->i).toArray());
                    sockets.put(user, client);
                    /*BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    writer.write(getUsers().toString() + "\n");
                    writer.flush();*/
                    System.out.println(name + " initialized");
                    helper.writeLine(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),
                            "server: Вы успешно вошли в систему! Выберите чат приступайте к диалогу");
                } else {
                    client.close();
                    System.out.println("wrong greeting");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (RuntimeException e){
                System.out.println(e.getStackTrace());
                try {
                    close();
                    alertAll(e.getMessage());
                } catch (IOException ignore) { }
            }
        }
    }
    public void listenToIncomingMessages(){
        while (!socket.isClosed()) {
            try {
                if (sockets.size()!=0)
                for (User user : sockets.keySet()) {
                    if ( !sockets.get(user).isClosed() && sockets.get(user).getInputStream().available() != 0) {
                        handleMessage(
                            helper.readLine(
                                new BufferedReader(
                                    new InputStreamReader(sockets.get(user).getInputStream()))), user);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (RuntimeException e){
                e.printStackTrace();
                try {
                    close();
                    alertAll(e.getMessage());
                } catch (IOException ignore) { }
            }
        }
    }
    public void handleMessage(String message, User user){
        try {
            if (message.charAt(0) == '/') {
                String[] strings = message.split(" ");
                String command = strings[0];
                    if (command.equals("/stop"))
                        closeClient(user);
                    else if (command.equals("/shutdown"))
                        close();
                    else if (command.equals("/leave"))
                        roomsRepository.getRoom(user.getCurrentChat()).disconnect(user);
                    else if (command.equals("/enter"))
                        roomsRepository.getRoom(Integer.parseInt(strings[1])).connect(user,sockets.get(user));
                    else if (command.equals("/move"))
                        gameService.move(Integer.parseInt(strings[1]),Integer.parseInt(strings[2]),Integer.parseInt(strings[3]));
                    else if (command.equals("/attack"))
                        gameService.attack(Integer.parseInt(strings[1]),Integer.parseInt(strings[2]));
            }/* else if (user.getCurrentChat()!=-1)
                roomsRepository.getChat(user.getCurrentChat()).sendToChatters(user , message);*/
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e){
            alertAll(e.getMessage());
        }
    }
    public void alertAll(String message){
        try {
            for(Socket socket : sockets.values()){
                if (!socket.isClosed())
                    helper.writeLine(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),message);
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
