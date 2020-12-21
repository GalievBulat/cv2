import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.OS;
import protocol.ClientCommunication;
import protocol.RoomCommunication;
import protocol.data.CommandData;
import server.dao.RoomsRepository;
import server.helper.Meta;
import server.model.User;
import server.service.GameService;
import server.service.maintainance.Server;
import view.model.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestServer {
    @Test
    public void sendingOtherTest() throws IOException {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream oS= new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(oS);
        User user = new User();

        Socket socket2 = mock(Socket.class);
        ByteArrayOutputStream oS2= new ByteArrayOutputStream();
        when(socket2.getOutputStream()).thenReturn(oS2);
        User user2 = new User();

        RoomCommunication roomCommunication = new RoomCommunication(0);

        roomCommunication.connect(user,socket ,1);
        roomCommunication.connect(user2,socket2 ,2);
        String message= "hi";
        roomCommunication.sendToChatters(user,message);
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(oS2.toByteArray())));
        String res ="";
        String rea =br.readLine();
        rea =br.readLine();
        while (rea != null && rea.length()>0){
            res = rea;
            rea =br.readLine();
        }
        assertTrue(res.contains(message));
    }
    @Test
    public void sendingSelfTest() throws IOException {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream oS= new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(oS);
        RoomCommunication roomCommunication = new RoomCommunication(0);
        User user = new User();
        roomCommunication.connect(user,socket ,1);
        String message= "hi";
        roomCommunication.sendToUser(user,message);
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(oS.toByteArray())));
        String res ="";
        String rea =br.readLine();
        rea =br.readLine();
        while (rea != null && rea.length()>0){
            res = rea;
            rea =br.readLine();
        }
        if(Meta.SEND_SELF)
            assertTrue(res.contains(message));
        else
            assertFalse(res.contains(message));
    }
    @Test
    public void closingTest(){
        Server server = new Server();
        server.handleMessage(CommandData.SHUTDOWN.getCommand(), new User());
        assertTrue(server.isClosed());
    }
    @Test
    public void enteringTest() throws IOException {
        Socket socket = mock(Socket.class);
        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());

        RoomCommunication roomCommunication = new RoomCommunication(0);
        User user = new User();
        roomCommunication.connect(user,socket ,1);
        assertTrue(roomCommunication.getSize()>0);
    }

}
