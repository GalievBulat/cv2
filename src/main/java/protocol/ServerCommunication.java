package protocol;

import javafx.util.Pair;
import protocol.data.CommandData;
import protocol.helper.IOHandler;
import server.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static protocol.data.CommandData.INIT;

public class ServerCommunication implements AutoCloseable {
    private final ServerSocket socket;
    private final Map<User, Socket> sockets = new ConcurrentHashMap<>();
    private final IOHandler helper = new IOHandler();
    public ServerCommunication(int port){
        try {
            socket = new ServerSocket(port);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public List<Pair<String,User>> getUpdates(){
        List<Pair<String,User>> updates = new ArrayList<>();
        try {
            if (sockets.size() != 0)
                for (User user : sockets.keySet()) {
                    if (!sockets.get(user).isClosed() && sockets.get(user).getInputStream().available() != 0) {
                        updates.add(new Pair<>(
                                helper.readLine(sockets.get(user).getInputStream()), user));
                    }
                }
        } catch (RuntimeException | IOException e){
            e.printStackTrace();
            try {
                alertAll(e.toString());
                close();
            } catch (IOException ignore) { }
        }
        return updates;
    }
    public void addClient(List<Integer> rooms){
        try {
            Socket client = socket.accept();
            System.out.println("connected");
            String query = helper.readLine(client.getInputStream());
            CommandData commandData = CommandData.determineCommand(query);
            if (commandData == INIT) {
                String[] args = query.split(" ");
                String name = args[1];
                User user = new User();
                user.setName(name);
                sockets.put(user, client);
                helper.writeLine(client.getOutputStream(),
                        CommandData.DATA.getCommand() + " " + rooms.stream().map(room -> room + " ")
                                .collect(Collectors.joining()));
                System.out.println(name + " initialized");
            } else {
                client.close();
                throw new RuntimeException("wrong greeting");
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
    public int getRoomFromCommand(String message){
        return Integer.parseInt(message.split(" ")[1]);
    }
    public boolean isClosed(){
        return socket.isClosed();
    }
    public Socket getSocket(User user){
        return sockets.get(user);
    }
    @Override
    public void close() throws IOException{
        for (User user: sockets.keySet()){
            closeClient(user);
        }
        socket.close();
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
    public void closeClient(User user){
        try {
            sockets.get(user).close();
            sockets.remove(user);
        } catch (IOException ignore) { }
    }
}
