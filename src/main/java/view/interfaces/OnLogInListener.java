package view.interfaces;

import protocol.ClientCommunication;
import server.model.User;

public interface OnLogInListener {
    void onLogIn(ClientCommunication clientCommunication, int roomId);
}
