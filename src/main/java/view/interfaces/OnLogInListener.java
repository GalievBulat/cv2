package view.interfaces;

import server.model.User;

public interface OnLogInListener {
    void onLogIn(User user, int roomId);
}
