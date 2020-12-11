package serverclient;

import serverclient.server.Server;

public class ServerMaintenance {
    public static void main(String[] args){
        Server server = new Server();
        new Thread(server::handleConnections).start();
        new Thread(server::listenToIncomingMessages).start();
    }
}
