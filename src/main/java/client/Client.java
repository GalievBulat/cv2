package client;

import serverclient.helper.IOHandler;
import serverclient.helper.Meta;
import serverclient.model.User;
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
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private final IOHandler helper = new IOHandler();
    public Client(String userName){
        try {
            socket = new Socket(Meta.HOST,Meta.PORT);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            user = new User(userName,null );
            //TODO chatsIDs
            helper.writeLine(writer,"/init "+ userName + " " + /*intArrToStr(rooms)*/ "" + "\n");
            reader =new BufferedReader( new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String intArrToStr(int[] arr){
        StringBuilder stringBuilder = new StringBuilder();
        for (int n : arr){
            stringBuilder.append(n);
            stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }
    public void sendMessage(String message){
        helper.writeLine(writer,message);
    }
    public String getUpdates(){
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

    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
    public void stop(){
        helper.writeLine(writer,"/stop");
    }
}
