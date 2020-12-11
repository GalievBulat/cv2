package view;

import client.Client;
import serverclient.model.User;

public class ConnectionHandling {
    public void handleConnection(User user){
        try {
            while (user.getName() == null)
                user.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Client client = new Client(user.getName());
    }
}
