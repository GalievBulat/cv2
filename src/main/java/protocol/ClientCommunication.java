package protocol;

import javafx.util.Pair;
import protocol.data.CommandData;
import protocol.helper.CoordsParser;
import protocol.helper.IOHandler;
import view.helper.Meta;
import view.model.Message;
import view.model.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientCommunication implements AutoCloseable {
    private final Socket socket;
    private final CoordsParser coordsParser = new CoordsParser();
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final User user;

    public User getUser() {
        return user;
    }

    private final IOHandler helper = new IOHandler();
    public ClientCommunication(User user){
        try {
            this.user = user;
            socket = new Socket(Meta.HOST,Meta.PORT);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            helper.writeLine(writer,CommandData.INIT.getCommand() + " "+ user.getName() + "\n");
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
    @Deprecated
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
                    String[] strings = line.split(": ");
                    if (strings.length > 1)
                        res.add(new Message(strings[0], Arrays.stream(strings).skip(1).collect(Collectors.joining())));
                }
            }
            return res;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void stop(){
        helper.writeLine(writer,CommandData.STOP.getCommand());
        try {
            close();
        } catch (IOException ignore) { }
    }
    public boolean isAlive(){
        return !socket.isClosed();
    }

    private boolean addRooms(String query){
        CommandData commandData = CommandData.determineCommand(query);
        if (commandData == CommandData.DATA){
            String[] strings = query.split(" ");
            int[] res = new int[strings.length-1];
            for (int i = 0; i < strings.length-1; i++) {
                res[i] = Integer.parseInt(strings[i+1]);
            }
            user.setRoomsAvailable(res);
            return true;
        }
        else return false;
    }
    public Pair<Byte,Byte> getCoords(String command, int num){
        return coordsParser.parseCoordinates(command.split(" ")[num ]);
    }
    @SafeVarargs
    public final void sendCommandWithCoords(CommandData type, Pair<Byte, Byte>... coords){
        sendMessage(
                type.getCommand() + " " +
                        Arrays.stream(coords)
                                .map(pair->"{" + pair.getKey() + ";" + pair.getValue() + "}")
                                .collect(Collectors.joining(" ")));
    }
    public final void sendCommandWithNum(CommandData type, int num){
        sendMessage(
                type.getCommand() + " " +
                        num);
    }
    @SafeVarargs
    public final void sendCommandWithCoordsAndNum(CommandData type, int num, Pair<Byte, Byte>... coords){
        sendMessage(
                type.getCommand() + " " +
                        Arrays.stream(coords)
                                .map(pair->"{" + pair.getKey() + ";" + pair.getValue() + "}")
                                .collect(Collectors.joining(" ")) + " " + num);
    }
    public  int getNumFromCommand(String command, int n){
        String[] parts = command.split(" ");
        return Integer.parseInt(command.split(" ")[n]);
    }
}
