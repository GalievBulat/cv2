package client;

import server.helper.IOHandler;
import server.helper.Meta;
import server.model.User;
import view.models.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Client implements AutoCloseable {
    final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final User user;

    public User getUser() {
        return user;
    }

    private final IOHandler helper = new IOHandler();
    public Client(User user){
        try {
            this.user = user;
            socket = new Socket(Meta.HOST,Meta.PORT);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            helper.writeLine(writer,"/i "+ user.getName() + "\n");
            reader =new BufferedReader( new InputStreamReader(socket.getInputStream()));
            String response = helper.readLine(reader);
            if(!addRooms(response)){
                //TODO
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
    public void sendMessage(String message){
        helper.writeLine(writer,message);
    }
    public String getRowUpdates(){
        try {
            StringBuilder res = new StringBuilder();
            while (socket.getInputStream().available()!=0) {
                 res.append(helper.readLine(reader));
                 res.append("\n");
            }
            return res.toString();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Message> getMessages(){
        try {
            List<Message> res = new ArrayList<>();
            while (socket.getInputStream().available()!=0) {
                String update = helper.readLine(reader);
                for (String line: update.split(Meta.DELIMITER)) {
                    String[] strings = line.split(":");
                    if (strings.length > 1)
                        res.add(new Message(strings[0], Arrays.stream(strings).skip(1).collect(Collectors.joining())));
                }
            }
            return res;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String intArrToStr(int[] arr){
        StringBuilder stringBuilder = new StringBuilder();
        for (int n : arr){
            stringBuilder.append(n);
            stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }

    public void stop(){
        helper.writeLine(writer,"/s");
        try {
            close();
        } catch (IOException ignore) { }
    }
    public boolean isAlive(){
        return !socket.isClosed();
    }

    private boolean addRooms(String query){
        if (query.startsWith("/d")){
            String[] strings = query.split(" ");
            int[] res = new int[strings.length-1];
            for (int i = 0; i < strings.length-1; i++) {
                res[i] = Integer.parseInt(strings[i+1]);
            }
            user.setRooms(res);
            return true;
        }
        else return false;
    }
}
