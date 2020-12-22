package view.controllers;

import protocol.ClientCommunication;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import view.model.User;
import view.interfaces.OnLogInListener;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LoginController implements Initializable {
    @FXML
    public Button bn_login;
    @FXML
    public ChoiceBox<Integer> chb_room;
    @FXML
    public TextField tf_name;
    @FXML
    public Button bn_join;
    private User user;
    private OnLogInListener onLogInListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chb_room.setVisible(false);
        bn_join.setVisible(false);
        bn_login.setOnMouseClicked(event ->{
            String name = tf_name.getText();
            if (user!=null) {
                user.setName(name);
                ClientCommunication clientCommunication = new ClientCommunication(user);
                if(user.getRoomsAvailable().length>0) {
                    chb_room.setVisible(true);
                    bn_join.setVisible(true);
                    setRooms(user.getRoomsAvailable());
                    chb_room.setValue(user.getRoomsAvailable()[0]);
                    bn_join.setOnMouseClicked(event1 -> {
                        onLogInListener.onLogIn(clientCommunication, chb_room.getValue());
                    });
                }
            }
        });
    }
    public void setUser(User u){
        user = u;
    }
    private void setRooms(int[] rooms){
        chb_room.setItems(new ObservableListWrapper<>(Arrays.stream(rooms).boxed().collect(Collectors.toList())));
    }
    public void setOnLogInListener(OnLogInListener onLogInListener) {
        this.onLogInListener = onLogInListener;
    }

}
