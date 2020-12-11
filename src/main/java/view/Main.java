package view;

import serverclient.model.User;

public class Main {
    public static void main(String[] args) {
        User user = new User();
        GameViewExecution viewExecution = new GameViewExecution();
        ConnectionHandling connectionHandling = new ConnectionHandling();
        new Thread(()->{
            viewExecution.main(user);
        }).start();
        new Thread(()-> {
            connectionHandling.handleConnection(user);}
        ).start();;
    }
}
