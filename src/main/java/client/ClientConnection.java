package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientConnection {
    public Client connectClient(String userName,int[] rooms) {
        System.out.println("Introduce yourself: ");
        Client client = new Client(userName,rooms);
        return client;
    }
    public void maintainChat(Client client){
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (!client.socket.isClosed()) {
                if (scanner.ready())
                    client.sendMessage(scanner.readLine());
                System.out.print(client.getUpdates());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e){
            e.printStackTrace();
            try {
                client.close();
            } catch (IOException ignore) { }
        }
    }
}
